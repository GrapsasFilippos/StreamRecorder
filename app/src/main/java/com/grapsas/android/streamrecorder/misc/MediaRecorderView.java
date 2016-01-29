package com.grapsas.android.streamrecorder.misc;


import android.media.MediaRecorder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Chronometer;

import com.grapsas.android.streamrecorder.R;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class MediaRecorderView {

    private int mStubResourceId;
    private WeakReference< AppCompatActivity > weakActivity;

    private MediaRecorder recorder;

    private View recordingView;
    private Chronometer chronometer;


    public MediaRecorderView( AppCompatActivity activity, int stubResourceId ) {
        this.weakActivity = new WeakReference<>( activity );
        this.mStubResourceId = stubResourceId;
    }

    @Nullable
    private AppCompatActivity getActivity() {
        return weakActivity.get();
    }


    private void showRecordingView() {
        AppCompatActivity activity = this.getActivity();
        if( activity == null )
            return;
        // Use recordingView for first time.
        if( this.recordingView == null ) {
            this.recordingView = ( ( ViewStub ) activity.findViewById( R.id.stub_recording ) ).inflate();
            this.recordingView.findViewById( R.id.stop ).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    stopRecording();
                }
            } );
            this.chronometer = (Chronometer ) activity.findViewById( R.id.chronometer );
        }
        // Reuse recordingView
        else
            this.recordingView.setVisibility( View.VISIBLE );
    }

    private void hideRecordingView() {
        this.recordingView.setVisibility( View.GONE );
    }


    /*
     * Media Recorder
     */
    public void startRecording() throws com.grapsas.android.streamrecorder.exception.IOException,
            IOException, IllegalStateException {
        AppCompatActivity activity = this.getActivity();
        if( activity == null )
            return;
//        try {
            IO.checkWorkingDirectory();
//        } catch( IOException e ) {
//            e.printStackTrace();
//            if( e.getCode() == 1) {
//                Snackbar.make(
//                        fab,
//                        activity.getString( R.string.Unable_to_create_directory_ )
//                                + " " + IO.getWorkingDirectory(),
//                        Snackbar.LENGTH_LONG
//                ).show();
//                return;
//            }
//            else if( e.getCode() == 2 ) {
//                Snackbar.make(
//                        fab,
//                        activity.getString( R.string.Unable_to_prepare_MediaRecorder )
//                                + " " + IO.getWorkingDirectory(),
//                        Snackbar.LENGTH_LONG
//                ).show();
//                return;
//            }
//        }

        String outputFilePath = IO.getWorkingDirectory() + IO.generateFileName() + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        recorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        recorder.setOutputFile( outputFilePath );
        recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );

        try {
            recorder.prepare();
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            this.stopRecording( 1 );
//            Snackbar.make( fab, R.string.Unable_to_prepare_MediaRecorder, Snackbar.LENGTH_LONG )
//                    .show();
//            return;
            throw e;
        }

        this.showRecordingView();
        this.chronometer.setBase( SystemClock.elapsedRealtime() );

        try {
            recorder.start();
        }
        catch( IllegalStateException e ) {
            e.printStackTrace();
            this.stopRecording( 2 );
//            Snackbar.make( fab, R.string.Unable_to_start_MediaRecorder, Snackbar.LENGTH_LONG )
//                    .show();
//            return;
            throw e;
        }

        this.chronometer.start();
        this.triggerStartRecording();
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
    }

    public void stopRecording() {
        this.stopRecording( 0 );
        this.triggerStopRecording();
    }


    /*
     * Public interface
     */
    public interface Events {
        void onStartRecording();
        void onStopRecording();
    }

    private void triggerStartRecording() {
        AppCompatActivity activity = getActivity();
        if( activity == null || !( activity instanceof Events ) )
            return;
        ( (Events) activity).onStartRecording();
    }

    private void triggerStopRecording() {
        AppCompatActivity activity = getActivity();
        if( activity == null || !( activity instanceof Events ) )
            return;
        ( (Events) activity).onStopRecording();
    }

}
