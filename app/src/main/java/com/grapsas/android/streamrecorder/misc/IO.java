package com.grapsas.android.streamrecorder.misc;


import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.grapsas.android.streamrecorder.exception.NeedActivityException;
import com.grapsas.android.streamrecorder.exception.NeedWorkingDirectoryException;

import java.io.FileDescriptor;
import java.util.Calendar;


public class IO {

    /*
     * General tools
     */
    @NonNull
    public static String generateFileName() {
        Calendar c = Calendar.getInstance();

        //noinspection UnnecessaryLocalVariable
        String fileName = ""
                + c.get( Calendar.YEAR ) + "-"
                + one2tow( c.get( Calendar.MONTH ) + 1 ) + "-"
                + one2tow( c.get( Calendar.DAY_OF_MONTH ) ) + "."
                + one2tow( c.get( Calendar.HOUR_OF_DAY ) ) + ":"
                + one2tow( c.get( Calendar.MINUTE ) ) + ":"
                + one2tow( c.get( Calendar.SECOND ) );

        return fileName;
    }

    @NonNull
    public static String one2tow( int number ) {
        return (number < 10) ? "0" + number : number+"";
    }


    /*
     * Filesystem tools
     */
    @Nullable
    public static FileDescriptor createNewFile() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
            return IOV21.createNewFile();
        else
            return IOV16.createNewFile();
    }


    /*
     * Records tools
     */
    @NonNull
    public static FileListItem[] getRecords_FLIArray() throws
            NeedActivityException, NeedWorkingDirectoryException {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            //noinspection UnnecessaryLocalVariable
            FileListItem[] files = IOV21.getRecords_FLIArray();
            return files;
        }
        else {
            return IOV16.getRecords_FLIArray();
        }
    }

}
