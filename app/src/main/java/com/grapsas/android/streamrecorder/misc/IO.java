package com.grapsas.android.streamrecorder.misc;


import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.Calendar;

public class IO {

    private static final String WORKING_DIRECTORY_NAME = "StreamRecorder";


    @NonNull
    public static String getWorkingDirectory() {
        return Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/" + WORKING_DIRECTORY_NAME + "/";
    }

    public static boolean checkWorkingDirectory() throws IOException {
        File wDir = new File( getWorkingDirectory() );

        if( !wDir.exists() ) {
            if( !wDir.mkdirs() )
                throw new IOException( "Unable to create directories", 1 );
            return true;
        }
        if( !wDir.isDirectory() )
            throw new IOException( "Isn't directory", 2 );

        return true;
    }

    @NonNull
    public static String generateFileName() {
        Calendar c = Calendar.getInstance();

        String fileName = ""
                + c.get( Calendar.YEAR )
                + one2tow( c.get( Calendar.MONTH ) + 1 )
                + one2tow( c.get( Calendar.DAY_OF_MONTH ) )
                + one2tow( c.get( Calendar.HOUR_OF_DAY ) )
                + one2tow( c.get( Calendar.MINUTE ) )
                + one2tow( c.get( Calendar.SECOND ) );

        return fileName;
    }

    @NonNull
    public static String one2tow( int number ) {
        return (number < 10) ? "0" + number : number+"";
    }

}
