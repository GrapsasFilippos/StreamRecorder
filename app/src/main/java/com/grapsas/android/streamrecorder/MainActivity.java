package com.grapsas.android.streamrecorder;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.grapsas.android.streamrecorder.adapters.RecordsListAdapter;
import com.grapsas.android.streamrecorder.misc.IO;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    // TODO: Add elevation
    private RelativeLayout recordingLayout;
    private View recordingView;
    private Chronometer chronometer;
    private ListView listView;
    private FloatingActionButton fab;

    private MediaRecorder recorder;
    private RecordsListAdapter adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        this.recordingLayout = (RelativeLayout) findViewById( R.id.recordingLayout );
        this.adapter = new RecordsListAdapter( IO.getRecordsArray() );
        this.listView = (ListView) findViewById( R.id.listView );
        this.listView.setAdapter( adapter );

        fab = ( FloatingActionButton ) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if( recorder == null ) {
                    startRecording();
                } else {
                    stopRecording();
                }
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
        super.onPause();
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

    private void showRecordingView() {
        // Use recordingView for first time.
        if( this.recordingView == null ) {
            this.recordingView = ( ( ViewStub ) findViewById( R.id.stub_import ) ).inflate();
            this.recordingView.findViewById( R.id.stop ).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    stopRecording();
                }
            } );
            this.chronometer = (Chronometer) findViewById( R.id.chronometer );
        }
        // Reuse recordingView
        else
            this.recordingView.setVisibility( View.VISIBLE );

        this.fab.hide();
        showBottomLayout();
    }

    private void hideRecordingView() {
        this.recordingView.setVisibility( View.GONE );

        hideBottomLayout();
        this.fab.show();
    }


    private void refreshListView() {
        this.adapter.refreshData( IO.getRecordsArray() );
        this.adapter.notifyDataSetChanged();
    }

    public void startRecording() {
        try {
            IO.checkWorkingDirectory();
        } catch( com.grapsas.android.streamrecorder.misc.IOException e ) {
            e.printStackTrace();
            if( e.getCode() == 1) {
                Snackbar.make(
                    fab,
                    getString( R.string.Unable_to_create_directory_ )
                        + " " + IO.getWorkingDirectory(),
                    Snackbar.LENGTH_LONG
                ).show();
                return;
            }
            else if( e.getCode() == 2 ) {
                Snackbar.make(
                    fab,
                    getString( R.string.Unable_to_prepare_MediaRecorder )
                        + " " + IO.getWorkingDirectory(),
                    Snackbar.LENGTH_LONG
                ).show();
                return;
            }
        }

        String outputFilePath = IO.getWorkingDirectory() + IO.generateFileName() + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        recorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        recorder.setOutputFile( outputFilePath );
        recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );

        try {
            recorder.prepare();
        } catch( IOException e ) {
            e.printStackTrace();
            this.stopRecording( 1 );
            Snackbar.make( fab, R.string.Unable_to_prepare_MediaRecorder, Snackbar.LENGTH_LONG )
                    .show();
            return;
        }

        this.showRecordingView();

        try {
            recorder.start();
        }
        catch( IllegalStateException e ) {
            e.printStackTrace();
            this.stopRecording( 2 );
            Snackbar.make( fab, R.string.Unable_to_start_MediaRecorder, Snackbar.LENGTH_LONG )
                    .show();
            return;
        }

        this.chronometer.setBase( SystemClock.elapsedRealtime() );
        this.chronometer.start();
    }

    /*
     * Recording steps must be with the following order for errorStage parameter:
     * - No errors ( errorStage: 0 )
     * - MediaRecorder.prepare() ( errorStage: 1 )
     * - this.showRecording()
     * - MediaRecorder.start() ( errorStage: 2 )
     */
    public void stopRecording( int errorStage ) {
        if( this.recorder == null )
            return;
        if( errorStage == 0 || errorStage >= 2 )
            this.recorder.stop();
        this.recorder.release();
        this.recorder = null;

        if( errorStage == 0 || errorStage >= 2 )
            this.hideRecordingView();

        this.chronometer.stop();
        this.refreshListView();
    }

    public void stopRecording() {
        this.stopRecording( 0 );
    }

}
