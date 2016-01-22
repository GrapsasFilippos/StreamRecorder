package com.grapsas.android.streamrecorder;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import com.grapsas.android.streamrecorder.misc.IO;
import com.grapsas.android.streamrecorder.misc.MyLog;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private View recordingView;
    // TODO: Add elevation
    private RelativeLayout recordingLayout;
    private FloatingActionButton fab;

    private MediaRecorder recorder;


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
                if( recorder == null ) {
                    startRecording();
//                    Snackbar.make( view, "Recording started.", Snackbar.LENGTH_LONG )
//                            .setAction( "Action", null ).show();
                } else {
                    stopRecording();
//                    Snackbar.make( view, "Recording stopped.", Snackbar.LENGTH_LONG )
//                            .setAction( "Action", null ).show();
                }
            }
        } );

        this.recordingLayout = (RelativeLayout) findViewById( R.id.recordingLayout );
        MyLog.d( IO.getWorkingDirectory() + "audiorecordtest.3gp" );
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

    public void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        recorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        MyLog.d( IO.getWorkingDirectory() + "audioRecordTest.3gp" );
        recorder.setOutputFile( IO.getWorkingDirectory() + "audioRecordTest.3gp" );
        recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );

        try {
            recorder.prepare();
        } catch( IOException e ) {
            e.printStackTrace();
        }

        this.showRecordingView();
        recorder.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        this.hideRecordingView();
    }

}
