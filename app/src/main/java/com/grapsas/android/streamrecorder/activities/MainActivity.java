package com.grapsas.android.streamrecorder.activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.grapsas.android.lib.slidingtabs.slidingtabs.SlidingTabLayout;
import com.grapsas.android.streamrecorder.App;
import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.dialogs.AddFavoriteURL;
import com.grapsas.android.streamrecorder.dialogs.DeleteFile;
import com.grapsas.android.streamrecorder.exception.NeedActivityException;
import com.grapsas.android.streamrecorder.exception.NeedWorkingDirectoryException;
import com.grapsas.android.streamrecorder.fragments.MicRecordsFragment;
import com.grapsas.android.streamrecorder.fragments.StreamsRecordsFragment;
import com.grapsas.android.streamrecorder.interfaces.OnDataChanged;
import com.grapsas.android.streamrecorder.interfaces.OnPageChangeListener;
import com.grapsas.android.streamrecorder.misc.FileListItem;
import com.grapsas.android.streamrecorder.misc.IO;
import com.grapsas.android.streamrecorder.misc.IOV16;
import com.grapsas.android.streamrecorder.misc.IOV21;
import com.grapsas.android.streamrecorder.misc.MediaPlayerView;
import com.grapsas.android.streamrecorder.misc.MediaRecorderView;
import com.grapsas.android.streamrecorder.misc.MyLog;
import com.grapsas.android.streamrecorder.misc.ViewPagerListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends MyActivity implements
        MicRecordsFragment.OnFragmentInteractionListener,
        StreamsRecordsFragment.OnFragmentInteractionListener,
        MediaRecorderView.Events,
        MediaPlayerView.Events,
        DeleteFile.Response,
        OnPageChangeListener,
        AddFavoriteURL.Interaction {

    // TODO: Add elevation
    private RelativeLayout recordingLayout;
    private MediaRecorderView mediaRecorderView;
    private MediaPlayerView mediaPlayerView;

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private Snackbar snackbar;
    private Menu pMenu;

    private FloatingActionButton micFab;
    private FloatingActionMenu sFabMenu;
    private FloatingActionButton favFab;
    private FloatingActionButton preExistsFab;
    private FloatingActionButton newFab;

    private FileListItem pFli;
    private String url4Rec;


    /*
     * Activity Overrides
     */
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FragmentManager fragmentManager = getSupportFragmentManager();
        pagerAdapter = new PagerAdapter( fragmentManager, this );

        viewPager = ( ViewPager ) findViewById( R.id.viewPager );
        if( viewPager != null )
            viewPager.setAdapter( pagerAdapter );

        SlidingTabLayout slidingTabs = (SlidingTabLayout ) findViewById(R.id.tabs);
        if( slidingTabs != null ) {
            slidingTabs.setDistributeEvenly( true );
            slidingTabs.setViewPager( viewPager );
            slidingTabs.setOnPageChangeListener( new ViewPagerListener( this ) );
        }

        this.initFabs();

        this.mediaRecorderView = new MediaRecorderView( this, R.id.stub_recording );
        this.mediaPlayerView = new MediaPlayerView( this, R.id.stub_playing );
        this.recordingLayout = (RelativeLayout) findViewById( R.id.recordingLayout );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.activity_main_menu, menu );
        this.pMenu = menu;
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.remove:
                DeleteFile.newInstance( pFli.getUri() ).show( getFragmentManager(), null );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    protected void onResume() {
        MyLog.d( "----------------" );
        super.onResume();
        pagerFinishMoving();
        if( this.getUrl4Rec() != null )
            this.startStreamRecording();
    }

    @Override
    protected void onPause() {
        this.stopMicRecording();
        this.stopPlaying();
        super.onPause();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( resultCode != RESULT_OK )
            return;
        switch( requestCode ) {
            case IOV21.DIR_PICKER_RESULT_CODE:
                App.getInstance().setLastActivity( this );
                IOV21.resultDirectoryPicker( data.getData() );
                if( this.snackbar != null )
                    this.snackbar.dismiss();
                break;
            case FavoritesURLsActivity.RESULT_CODE:
                String url = data.getStringExtra( FavoritesURLsActivity.RESULT_URL_KEY );
                this.setUrl4Rec( url );
                break;
        }
    }


    /*
     * GUI tools.
     */
    private void showBottomLayout() {
        ViewGroup.LayoutParams params = this.recordingLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        this.recordingLayout.setLayoutParams( params );
    }

    private void hideBottomLayout() {
        ViewGroup.LayoutParams params = this.recordingLayout.getLayoutParams();
        params.height = 0;
        this.recordingLayout.setLayoutParams( params );
    }

    private void initFabs() {
        this.micFab = ( FloatingActionButton ) findViewById( R.id.micFab );
        this.sFabMenu = ( FloatingActionMenu ) findViewById( R.id.sFabMenu );
        this.favFab = ( FloatingActionButton ) findViewById( R.id.favFab );
        this.preExistsFab = ( FloatingActionButton ) findViewById( R.id.preExistsFab );
        this.newFab = ( FloatingActionButton ) findViewById( R.id.newFab );

        this.micFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startMicRecording();
            }
        } );
        this.favFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startFavActivity( FavoritesURLsActivity.TYPE_FAV );
            }
        } );
        this.preExistsFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startFavActivity( FavoritesURLsActivity.TYPE_PRE_EXISTS );
            }
        });
        this.newFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startAddFavoriteURL();
            }
        } );
    }

    private void showFab( boolean animate ) {
        this.hideFabs();
        switch( this.viewPager.getCurrentItem() ) {
            case 0:
                this.micFab.show( animate );
                break;
            case 1:
                this.sFabMenu.showMenu( animate );
                break;
        }
    }

    private void showFab() {
        this.showFab( false );
    }

    private void hideFabs( boolean animate ) {
        this.micFab.hide( animate );
        this.sFabMenu.hideMenu( animate );
    }

    private void hideFabs() {
        this.hideFabs( false );
    }

    protected void startFavActivity( @NonNull String type ) {
        Intent intent = new Intent( this, FavoritesURLsActivity.class );
        Bundle bundle = new Bundle();
        bundle.putString( FavoritesURLsActivity.TYPE_KEY, type );
        intent.putExtras( bundle );
        startActivityForResult( intent, FavoritesURLsActivity.RESULT_CODE );
    }

    protected void startFavActivity() {
        this.startFavActivity( FavoritesURLsActivity.TYPE_FAV );
    }


    /*
     * Tools
     */
    private void filesystemChanged() {
        this.pagerAdapter.refreshDataSets();
    }

    @NonNull
    private FileListItem[] getRecordsV21( int type ) {
        FileListItem[] records;

        try {
            records = IO.getRecords_FLIArray( type );
        } catch( NeedActivityException e ) {
            MyLog.e( "NeedActivityException" );
            e.printStackTrace();
            records = new FileListItem[ 0 ];
            if( this.snackbar != null )
                this.snackbar.dismiss();
            this.snackbar = Snackbar
                    .make( this.micFab, "Unable to access records.", Snackbar.LENGTH_INDEFINITE );
            this.snackbar.setAction( "Retry", new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) {
                            filesystemChanged();
                        }
                    } );
            this.snackbar.show();
        } catch( NeedWorkingDirectoryException e ) {
            MyLog.e( "NeedWorkingDirectoryException" );
            e.printStackTrace();
            records = new FileListItem[ 0 ];
            if( this.snackbar != null )
                this.snackbar.dismiss();
            this.snackbar = Snackbar.make(
                    this.micFab,
                    "Need read/write permissions to a directory for saving and play records.",
                    Snackbar.LENGTH_INDEFINITE );
            this.snackbar.setAction( "Select", new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) {
                            IOV21.launchDirectoryPicker();
                        }
                    } );
            this.snackbar.show();
        }

        return records;
    }

    @Override
    public FileListItem[] getRecords( int type ) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
            return this.getRecordsV21( type );
        else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            return IOV16.getRecords_FLIArray( type );

        throw new RuntimeException( "Unexpected version!" );
    }

    private void setUrl4Rec( @Nullable String url ) {
        this.url4Rec = url;
    }

    @Nullable
    public String getUrl4Rec() {
        return this.url4Rec;
    }


    /*
     * MediaRecorderView
     */
    private void startMicRecording() {
//        try {
//            this.mediaRecorderView.startRecording();
//        } catch( com.grapsas.android.streamrecorder.exception.IOException e ) {
//            e.printStackTrace();
//            if( e.getCode() == 1) {
//                Snackbar.make(
//                        fab,
//                        getString( R.string.Unable_to_create_directory_ )
//                        ,Snackbar.LENGTH_LONG
//                ).show();
//                //noinspection UnnecessaryReturnStatement
//                return;// To be sure and for the future
//            }
//            else if( e.getCode() == 2 ) {
//                Snackbar.make(
//                        fab,
//                        getString( R.string.Unable_to_prepare_MediaRecorder )
//                        ,Snackbar.LENGTH_LONG
//                ).show();
//                //noinspection UnnecessaryReturnStatement
//                return;// To be sure and for the future
//            }
//        } catch( IOException e ) {
//            e.printStackTrace();
//            Snackbar.make( fab, R.string.Unable_to_prepare_MediaRecorder, Snackbar.LENGTH_LONG )
//                    .show();
//        }
//        catch( IllegalStateException e ) {
//            e.printStackTrace();
//            Snackbar.make( fab, R.string.Unable_to_start_MediaRecorder, Snackbar.LENGTH_LONG )
//                    .show();
//        }

        this.mediaRecorderView.startRecording( MediaRecorderView.MIC_RECORDER );
    }

    private  void stopMicRecording() {
    }

    private void startStreamRecording() {
        this.mediaRecorderView.startRecording( MediaRecorderView.STREAM_RECORDER );
    }


    /*
     * MediaPlayerView
     */
    @Override
    public void startPlaying( FileListItem fileListItem ) {
        try {
            this.mediaPlayerView.startPlaying( fileListItem );
        } catch( com.grapsas.android.streamrecorder.exception.IOException e ) {
            e.printStackTrace();
            int errorCode = e.getCode();
            switch( errorCode ) {
                case 1:
                    Snackbar.make( this.micFab, R.string.Unable_to_set_data_source, Snackbar.LENGTH_LONG )
                            .show();
                    break;
                case 2:
                    Snackbar.make( this.micFab, R.string.Unable_to_prepare_MediaPlayer, Snackbar.LENGTH_LONG )
                            .show();
                    break;
            }
        }
    }

    private void stopPlaying() {
        this.mediaPlayerView.stopPlaying();
    }


    /*
     * Implements interface MediaRecorderView.Events
     */
    @Override
    public void onStartRecording() {
        this.hideFabs();
        showBottomLayout();
    }

    @Override
    public void onStopRecording() {
        hideBottomLayout();
        this.filesystemChanged();
        this.showFab();
        this.setUrl4Rec( null );
    }


    /*
     * Implements interface MediaPlayerView.Events
     */
    @Override
    public void onStartPlaying() {
        this.hideFabs();
        showBottomLayout();
    }

    @Override
    public void onStopPlaying() {
        hideBottomLayout();
        this.showFab();
    }

    @Override
    public void onPlayerViewShow( FileListItem fileListItem ) {
        this.pFli = fileListItem;
        this.pMenu.getItem( 0 ).setVisible( true );
    }

    @Override
    public void onPlayerViewHide() {
        this.pFli = null;
        this.pMenu.getItem( 0 ).setVisible( false );
    }


    /*
     * Implements interface DeleteFile.Response
     */
    @Override
    public void deleteResponse( boolean delete, @NonNull Uri file ) {
        if( !delete )
            return;
        String removeMessage;

        if( this.snackbar != null )
            this.snackbar.dismiss();

        if( IO.removeFile( file ) ) {
            removeMessage = getString( R.string.TheFileRemovedSuccessfully );
            this.onStopPlaying();
            this.filesystemChanged();
        }
        else
            removeMessage = getString( R.string.FileHasntRemoved );

        this.snackbar = Snackbar
                .make( this.micFab, removeMessage, Snackbar.LENGTH_LONG );
        this.snackbar.show();
    }


    /*
     * Implements interface OnPageChangeListener
     */
    @Override
    public void pagerStartMoving() {
        this.hideFabs();
    }

    @Override
    public void pagerFinishMoving() {
        this.showFab();
    }


    /*
     * Implements interface AddFavoriteURL.Interaction
     */
    @Override
    public void startAddFavoriteURL() {
        AddFavoriteURL.newInstance( AddFavoriteURL.TYPE.JUST_REC ).show( getFragmentManager(), "" );
    }

    @Override
    public void saveAndRec( @NonNull String url ) {
    }

    @Override
    public void save( @NonNull String url ) {
    }

    @Override
    public void rec( @NonNull String url ) {
        this.setUrl4Rec( url );
        this.startStreamRecording();
    }

    /*
     * Inner classes
     */
    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context pContext;
        List< WeakReference< OnDataChanged > > dataSets = new ArrayList<>();

        public PagerAdapter( FragmentManager fm, Context context ) {
            super( fm );
            this.pContext = context;
            dataSets.add( null );
            dataSets.add( null );
        }

        public void refreshDataSets() {
            for( WeakReference< OnDataChanged > wdc : dataSets ) {
                if( wdc != null && wdc.get() != null )
                    wdc.get().dataChanged();
            }
        }

        @Override
        public Fragment getItem( int position ) {
            switch( position ) {
                case 0:
                    MicRecordsFragment mFragment = new MicRecordsFragment();
                    dataSets.set( position, new WeakReference< OnDataChanged >( mFragment ) );
                    return mFragment;
                case 1:
                    StreamsRecordsFragment sFragment = new StreamsRecordsFragment();
                    dataSets.set( position, new WeakReference< OnDataChanged >( sFragment ) );
                    return sFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch( position ) {
                case 0:
                    return this.pContext.getString( R.string.Mic );
                case 1:
                    return this.pContext.getString( R.string.Streams );
            }

            return super.getPageTitle(position);
        }
    }

}
