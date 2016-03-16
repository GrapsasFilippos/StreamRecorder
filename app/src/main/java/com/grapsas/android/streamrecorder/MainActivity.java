package com.grapsas.android.streamrecorder;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.grapsas.android.streamrecorder.adapters.RecordsListAdapter;
import com.grapsas.android.streamrecorder.exception.NeedActivityException;
import com.grapsas.android.streamrecorder.exception.NeedWorkingDirectoryException;
import com.grapsas.android.streamrecorder.misc.FileListItem;
import com.grapsas.android.streamrecorder.misc.IO;
import com.grapsas.android.streamrecorder.misc.IOV16;
import com.grapsas.android.streamrecorder.misc.IOV21;
import com.grapsas.android.streamrecorder.misc.MediaPlayerView;
import com.grapsas.android.streamrecorder.misc.MediaRecorderView;
import com.grapsas.android.streamrecorder.misc.MyLog;

import java.io.IOException;


public class MainActivity extends MyActivity implements
        MediaRecorderView.Events,
        MediaPlayerView.Events {

    // TODO: Add elevation
    private RelativeLayout recordingLayout;
    private MediaRecorderView mediaRecorderView;
    private MediaPlayerView mediaPlayerView;
    private ListView listView;
    private FloatingActionButton fab;
    private Snackbar snackbar;

    private RecordsListAdapter adapter;

    private boolean flag = true;


    /*
     * Activity Overrides
     */
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        fab = ( FloatingActionButton ) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startRecording();
            }
        } );

        this.mediaRecorderView = new MediaRecorderView( this, R.id.stub_recording );
        this.mediaPlayerView = new MediaPlayerView( this, R.id.stub_playing );
        this.recordingLayout = (RelativeLayout) findViewById( R.id.recordingLayout );
        this.adapter = new RecordsListAdapter( new FileListItem[ 0 ] );
        this.listView = ( ListView) findViewById( R.id.listView );
        this.listView.setAdapter( adapter );
        this.listView = (ListView) findViewById( R.id.listView );
        this.listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {
                startPlaying( ( FileListItem ) adapter.getItem( position ) );
            }
        } );
    }

    @Override
    protected void onResume() {
        MyLog.d( "----------------" );
        super.onResume();
        this.refreshListView();
    }

    @Override
    protected void onPause() {
        this.stopRecording();
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
    private void refreshListView() {
        MyLog.d( "refreshListView" );
        this.adapter.refreshData( this.getRecords() );
        this.adapter.notifyDataSetChanged();
    }

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
    private FileListItem[] getRecordsV21() {
        FileListItem[] records;

        try {
            records = IO.getRecords_FLIArray();
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
                            refreshListView();
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

    private FileListItem[] getRecords() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
            return this.getRecordsV21();
        else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
            return IOV16.getRecords_FLIArray();
        }

        throw new RuntimeException( "Unexpected version!" );
    }


    /*
     * MediaRecorderView
     */
    private void startRecording() {
        try {
            this.mediaRecorderView.startRecording();
        } catch( com.grapsas.android.streamrecorder.exception.IOException e ) {
            e.printStackTrace();
            if( e.getCode() == 1) {
                Snackbar.make(
                        fab,
                        getString( R.string.Unable_to_create_directory_ )
//                                + " " + IO.getWorkingDirectory(),
                        ,Snackbar.LENGTH_LONG
                ).show();
                //noinspection UnnecessaryReturnStatement
                return;// To be sure and for the future
            }
            else if( e.getCode() == 2 ) {
                Snackbar.make(
                        fab,
                        getString( R.string.Unable_to_prepare_MediaRecorder )
//                                + " " + IO.getWorkingDirectory(),
                        ,Snackbar.LENGTH_LONG
                ).show();
                //noinspection UnnecessaryReturnStatement
                return;// To be sure and for the future
            }
        } catch( IOException e ) {
            e.printStackTrace();
            Snackbar.make( fab, R.string.Unable_to_prepare_MediaRecorder, Snackbar.LENGTH_LONG )
                    .show();
        }
        catch( IllegalStateException e ) {
            e.printStackTrace();
            Snackbar.make( fab, R.string.Unable_to_start_MediaRecorder, Snackbar.LENGTH_LONG )
                    .show();
        }
    }

    private  void stopRecording() {
    }


    /*
     * MediaPlayerView
     */
    private void startPlaying( FileListItem fileListItem ) {
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
        this.refreshListView();
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

}
