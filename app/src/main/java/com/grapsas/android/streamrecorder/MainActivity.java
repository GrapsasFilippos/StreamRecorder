package com.grapsas.android.streamrecorder;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.grapsas.android.lib.slidingtabs.slidingtabs.SlidingTabLayout;
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
        OnPageChangeListener {

    // TODO: Add elevation
    private RelativeLayout recordingLayout;
    private MediaRecorderView mediaRecorderView;
    private MediaPlayerView mediaPlayerView;

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private Snackbar snackbar;
    private Menu pMenu;

    private FileListItem pFli;


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

        viewPager = (ViewPager ) findViewById( R.id.viewPager );
        viewPager.setAdapter( pagerAdapter );

        SlidingTabLayout slidingTabs = (SlidingTabLayout ) findViewById(R.id.tabs);
        slidingTabs.setDistributeEvenly( true );
        slidingTabs.setViewPager( viewPager );
        slidingTabs.setOnPageChangeListener( new ViewPagerListener( this ) );

        fab = ( FloatingActionButton ) findViewById( R.id.fab );

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


    /*
     * Tools
     */
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
                    .make( this.fab, "Unable to access records.", Snackbar.LENGTH_INDEFINITE );
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
                    this.fab,
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
                    Snackbar.make( fab, R.string.Unable_to_set_data_source, Snackbar.LENGTH_LONG )
                            .show();
                    break;
                case 2:
                    Snackbar.make( fab, R.string.Unable_to_prepare_MediaPlayer, Snackbar.LENGTH_LONG )
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
        this.fab.hide();
        showBottomLayout();
    }

    @Override
    public void onStopRecording() {
        hideBottomLayout();
        this.filesystemChanged();
        this.fab.show();
    }


    /*
     * Implements interface MediaPlayerView.Events
     */
    @Override
    public void onStartPlaying() {
        this.fab.hide();
        showBottomLayout();
    }

    @Override
    public void onStopPlaying() {
        hideBottomLayout();
        this.fab.show();
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
                .make( this.fab, removeMessage, Snackbar.LENGTH_LONG );
        this.snackbar.show();
    }

    /*
     * Implements interface OnPageChangeListener
     */
    @Override
    public void pagerStartMoving() {
    }

    @Override
    public void pagerFinishMoving() {
        int icon;
        if( this.viewPager.getCurrentItem() == 0 ) {
            icon = R.drawable.ic_record_voice_over_white_24px;
            this.fab.setBackgroundTintList( ColorStateList.valueOf( getResources().getColor( R.color.colorAccent ) ) );
            this.fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    startMicRecording();
                }
            } );
        }
        else if( this.viewPager.getCurrentItem() == 1 ) {
            icon = R.drawable.ic_add_white_24dp;
            this.fab.setBackgroundTintList( ColorStateList.valueOf( getResources().getColor( R.color.colorPrimary ) ) );
            this.fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    startStreamRecording();
                }
            } );
        }
        else
            return;

        this.fab.setRippleColor( Color.RED );
        // app:borderWidth="0dp"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.fab.setImageDrawable( getResources().getDrawable( icon, this.getTheme() ) );
        } else {
            this.fab.setImageDrawable(getResources().getDrawable( icon ) );
        }
    }







    private void filesystemChanged() {
        pagerAdapter.refreshDataSets();
    }

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
