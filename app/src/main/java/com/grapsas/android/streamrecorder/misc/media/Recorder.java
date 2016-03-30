package com.grapsas.android.streamrecorder.misc.media;


import java.io.FileDescriptor;


public interface Recorder {

    boolean startRecording( FileDescriptor fd );
    boolean stopRecording();

}
