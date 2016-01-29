package com.grapsas.android.streamrecorder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.grapsas.android.streamrecorder.adapters.RecordsListAdapter;
import com.grapsas.android.streamrecorder.misc.FileListItem;
import com.grapsas.android.streamrecorder.misc.IO;
import com.grapsas.android.streamrecorder.misc.MediaPlayerView;
import com.grapsas.android.streamrecorder.misc.MediaRecorderView;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        MediaRecorderView.Events,
        MediaPlayerView.Events {

    // TODO: Add elevation
    private RelativeLayout recordingLayout;
    private MediaRecorderView mediaRecorderView;
    private MediaPlayerView mediaPlayerView;
    private ListView listView;
    private FloatingActionButton fab;

    private RecordsListAdapter adapter;


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
        this.adapter = new RecordsListAdapter( this.getRecordsArray() );
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
        super.onResume();
        this.refreshListView();
    }

    @Override
    protected void onPause() {
        this.stopRecording();
        this.stopPlaying();
        super.onPause();
    }


    private void refreshListView() {
        this.adapter.refreshData( this.getRecordsArray() );
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

    @NonNull
    private FileListItem[] getRecordsArray() {
        FileListItem[] fileListItem;
        String errorMessage = null;

        try {
            IO.checkWorkingDirectory();
            fileListItem = IO.getRecordsArray();
        } catch( com.grapsas.android.streamrecorder.exception.IOException e ) {
            fileListItem = new FileListItem[ 0 ];
            e.printStackTrace();
            switch( e.getCode() ) {
                case 1:
                    errorMessage = getString( R.string.Unable_to_create_directory_ );
                    break;
                case 2:
                    errorMessage = getString( R.string.Is_t_directory__ ) + " "
                            + IO.getWorkingDirectory();
                    break;
                case 3:
                    errorMessage = getString( R.string.Directory_doesn_t_exist__ ) + " "
                            + IO.getWorkingDirectory();
                    break;
                default:
                    throw new RuntimeException( "Unexpected error!" );
            }
        }

        if( errorMessage != null )
            Snackbar.make(
                    fab,
                    errorMessage,
                    Snackbar.LENGTH_LONG )
                    .show();

        return fileListItem;
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
                                + " " + IO.getWorkingDirectory(),
                        Snackbar.LENGTH_LONG
                ).show();
                return;// To be sure and for the future
            }
            else if( e.getCode() == 2 ) {
                Snackbar.make(
                        fab,
                        getString( R.string.Unable_to_prepare_MediaRecorder )
                                + " " + IO.getWorkingDirectory(),
                        Snackbar.LENGTH_LONG
                ).show();
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
        this.mediaRecorderView.stopRecording();
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
