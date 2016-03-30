package com.grapsas.android.streamrecorder.misc.media;


import android.media.MediaRecorder;
import android.support.annotation.Nullable;

import java.io.FileDescriptor;


public class MicRecorder implements Recorder {

    private MediaRecorder recorder;

    private Exception exception;


    public boolean startRecording( FileDescriptor fd ) {
        recorder = new MediaRecorder();
        recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        recorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        recorder.setOutputFile( fd );
        recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );

        try {
            recorder.prepare();
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            this.stopRecording();
            this.exception = new Exception( e );
            return false;
        }

        try {
            recorder.start();
        }
        catch( IllegalStateException e ) {
            e.printStackTrace();
            this.stopRecording();
            this.exception = new Exception( e );
            return false;
        }

        return true;
    }

    public boolean stopRecording() {
        if( this.recorder == null )
            return true;
        this.recorder.stop();
        this.recorder.release();
        this.recorder = null;

        return true;
    }

    @Nullable
    public Exception getException() {
        return this.exception;
    }

}
