package com.grapsas.android.streamrecorder.misc;


import android.os.Environment;

public class IO {

    public static String getWorkingDirectory() {
        return Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/StreamRecorder/";
    }

}
