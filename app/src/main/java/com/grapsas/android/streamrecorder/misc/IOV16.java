package com.grapsas.android.streamrecorder.misc;


import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.grapsas.android.streamrecorder.exception.IOException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;


public class IOV16 {

    private static final String WORKING_DIRECTORY_NAME = "StreamRecorder";


    /*
     * Filesystem tools
     */
    @NonNull
    private static String getWorkingDirectoryPath() {
        return Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/" + WORKING_DIRECTORY_NAME + "/";
    }

    private static int checkDirectory( @NonNull File directory, boolean autoCreate )
            throws IOException {
        if( !directory.exists() ) {
            if( !autoCreate )
                throw new IOException( "Directory doesn't exist", 3 );
            else if( !directory.mkdirs() ) {
                throw new IOException( "Unable to create directories", 1 );
            }
            return 2;

        }
        if( !directory.isDirectory() )
            throw new IOException( "Isn't directory", 2 );

        return 1;
    }

    private static int checkDirectory( @NonNull File directory ) throws IOException {
        return checkDirectory( directory, true );
    }

    public static int checkWorkingDirectory( boolean autoCreate ) throws IOException {
        File wDir = new File( getWorkingDirectoryPath() );

        return checkDirectory( wDir, autoCreate );
    }

    public static int checkWorkingDirectory() throws IOException {
        return checkWorkingDirectory( true );
    }

    @NonNull
    private static File[] getFileList( @NonNull String directoryPath ) throws IOException {
        File dir = new File( directoryPath );
        checkDirectory( dir, true );

        return dir.listFiles();
    }

    @NonNull
    private static File[] getFileList() throws IOException {
        return getFileList( getWorkingDirectoryPath() );
    }

    @Nullable
    public static FileDescriptor createNewFile( @NonNull String prefix, @NonNull String suffix ) {
        File wDirFile = new File( getWorkingDirectoryPath() );
        File newFile = new File( wDirFile, prefix + IO.generateFileName() + suffix );

        try {
            if( !newFile.createNewFile() )
                return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile( newFile, "rw" );
        } catch( FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        }

        FileDescriptor fd;
        try {
            fd = raf.getFD();
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        }

        return fd;
    }

    public static boolean removeFile( File file ) {
        return file.delete();
    }


    /*
     * Records tools
     */
    @NonNull
    private static ArrayList< ComparableFiles > getRecords( int type ) throws IOException {
        File[] filesList;
        ArrayList< ComparableFiles > comparableFiles = new ArrayList<>();

        String fileName;
        filesList = getFileList( getWorkingDirectoryPath() );
        for( int i = 0; i < filesList.length; i++ ) {
            fileName = filesList[ i ].getName();
            if(
                    ( ( type & IO.MIC_RECORDS ) == IO.MIC_RECORDS &&
                            fileName.substring( 0, 2 ).equals( "m." ) ) ||
                    ( ( type & IO.STREAM_RECORDS ) == IO.STREAM_RECORDS &&
                            fileName.substring( 0, 2 ).equals( "s." ) )
                ) {
                comparableFiles.add( new ComparableFiles( filesList[ i ] ) );
            }
        }

        Collections.sort( comparableFiles );

        return comparableFiles;
    }

    @NonNull
    public static FileListItem[] getRecords_FLIArray( int type ) {
        FileListItem[] fli;
        ArrayList< ComparableFiles > recordsAL;
        try {
            recordsAL = getRecords( type );
        } catch( IOException e ) {
            e.printStackTrace();
            return new FileListItem[ 0 ];
        }

        fli = new FileListItem[ recordsAL.size() ];
        for( int i = 0; i < fli.length; i++ )
            //noinspection UnnecessaryLocalVariable
            fli[ i ] = new FileListItem( recordsAL.remove( 0 ).getFile() );

        return fli;
    }

}
