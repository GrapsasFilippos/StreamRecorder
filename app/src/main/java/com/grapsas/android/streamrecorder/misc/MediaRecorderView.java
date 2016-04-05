package com.grapsas.android.streamrecorder.misc;


import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Chronometer;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.activities.MainActivity;
import com.grapsas.android.streamrecorder.misc.media.MicRecorder;
import com.grapsas.android.streamrecorder.misc.media.Recorder;
import com.grapsas.android.streamrecorder.misc.media.StreamRecorder;

import java.io.FileDescriptor;
import java.lang.ref.WeakReference;


public class MediaRecorderView {

    public static final int MIC_RECORDER = 1;
    public static final int STREAM_RECORDER = 2;


    private int mStubResourceId;
    private WeakReference< AppCompatActivity > weakActivity;

    private Recorder recorder;

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


    private boolean createRecordingView() {
        AppCompatActivity activity = this.getActivity();
        if( activity == null )
            return false;
        if( this.recordingView != null )
            return false;
        this.recordingView = ( ( ViewStub ) activity.findViewById( this.mStubResourceId ) ).inflate();
        this.recordingView.findViewById( R.id.stop ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                stopRecording();
            }
        } );
        this.chronometer = ( Chronometer ) activity.findViewById( R.id.chronometer );
        return true;
    }

    private void showRecordingView() {
        if( !createRecordingView() )
            this.recordingView.setVisibility( View.VISIBLE );
    }

    private void hideRecordingView() {
        this.recordingView.setVisibility( View.GONE );
    }


    /*
     * Media Recorder
     */
    public void startRecording( int type ) {
        AppCompatActivity activity = this.getActivity();
        if( activity == null )
            return;

        if( this.recorder != null ) {
            this.stopRecording();
            return;
        }

        FileDescriptor fd;
        String prefix;
        String suffix;
        if( type == MIC_RECORDER ) {
            prefix = "m.";
            suffix = ".3gp";
            this.recorder = new MicRecorder();
        }
        else if( type == STREAM_RECORDER ) {
            String url = ( (MainActivity) getActivity() ).getUrl4Rec();
            if( url == null )
                return;
            prefix = "s.";
            suffix = "";
            createRecordingView();
            this.recorder = new StreamRecorder( url, this.chronometer );
        }
        else
            return;

        fd = IO.createNewFile( prefix, suffix );
        if( fd == null )
            return;

        if( !this.recorder.startRecording( fd ) ) {
            this.stopRecording();
            return;
        }

        this.showRecordingView();
        this.chronometer.setBase( SystemClock.elapsedRealtime() );
        this.chronometer.start();
        this.triggerStartRecording();
    }


    public void stopRecording() {
        if( this.recorder == null )
            return;
        this.recorder.stopRecording();
        this.recorder = null;

        this.chronometer.stop();
        this.hideRecordingView();
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
