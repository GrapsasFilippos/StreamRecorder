package com.grapsas.android.streamrecorder.misc;


import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.grapsas.android.streamrecorder.exception.IOException;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class IO {

    private static final String WORKING_DIRECTORY_NAME = "StreamRecorder";


    @NonNull
    public static String getWorkingDirectory() {
        return Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/" + WORKING_DIRECTORY_NAME + "/";
    }

    private static int checkDirectory( @NonNull File directory, boolean autoCrate ) throws IOException {
        if( !directory.exists() ) {
            if( !autoCrate )
                throw new IOException( "Directory doesn't exist", 3 );
            else if( !directory.mkdirs() ) {
                throw new IOException( "Unable to create directories", 1 );
            }
            return 1;

        }
        if( !directory.isDirectory() )
            throw new IOException( "Isn't directory", 2 );

        return 0;
    }

    public static int checkWorkingDirectory() throws IOException {
        File wDir = new File( getWorkingDirectory() );

        return checkDirectory( wDir, true );
    }

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

    @NonNull
    public static File[] getFileList( @NonNull String directoryPath ) throws IOException {
        File dir = new File( directoryPath );
        checkDirectory( dir, false );

        return dir.listFiles();
    }

    @NonNull
    private static ComparableFiles[] getRecords() throws IOException {
        File[] filesList;
        ComparableFiles[] comparableFiles;

        filesList = getFileList( getWorkingDirectory() );
        comparableFiles = new ComparableFiles[ filesList.length ];
        for( int i = 0; i < filesList.length; i++ )
            comparableFiles[ i ] = new ComparableFiles( filesList[ i ] );

        Arrays.sort( comparableFiles, Collections.reverseOrder() );

        return comparableFiles;
    }

    @Nullable
    public static ArrayDeque< FileListItem > getRecordsDeque() throws IOException {
        return FileListItem.getFilesDeque( getRecords() );
    }

    @Nullable
    public static FileListItem[] getRecordsArray() throws IOException {
        return FileListItem.getFilesArray( getRecords() );
    }


}
