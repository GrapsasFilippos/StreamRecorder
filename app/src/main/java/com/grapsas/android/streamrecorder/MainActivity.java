package com.grapsas.android.streamrecorder;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.grapsas.android.streamrecorder.misc.IO;
import com.grapsas.android.streamrecorder.misc.MyLog;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private MediaRecorder recorder;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = ( FloatingActionButton ) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
//                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
//                        .setAction( "Action", null ).show();
                if( recorder == null ) {
                    startRecording();
                    Snackbar.make( view, "Recording started.", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
                }
                else {
                    stopRecording();
                    Snackbar.make( view, "Recording stopped.", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
                }
            }
        } );


        MyLog.d( IO.getWorkingDirectory() + "audiorecordtest.3gp" );
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

        recorder.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

}
