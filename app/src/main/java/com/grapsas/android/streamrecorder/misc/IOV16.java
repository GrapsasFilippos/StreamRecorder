package com.grapsas.android.streamrecorder.misc;


import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;

import com.grapsas.android.streamrecorder.exception.NeedActivityException;
import com.grapsas.android.streamrecorder.exception.NeedWorkingDirectoryException;

public class IOV16 {

    private static final String WORKING_DIRECTORY_NAME = "StreamRecorder";

    @NonNull
    public static String getWorkingDirectoryPath() {
        return Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/" + WORKING_DIRECTORY_NAME + "/";
    }

//    @NonNull
//    private static Uri getWorkingDirectoryUriV16() {
//    }

//    @NonNull
//    private static DocumentFile[] getFilesV16() {
//    }

}
