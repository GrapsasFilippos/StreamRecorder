package com.grapsas.android.streamrecorder.misc;


import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayDeque;

public class FileListItem {

    private String mPath;
    private String mName;

    public FileListItem( @NonNull File file ) {
        this.mPath = file.getPath();
        this.mName = file.getName();
    }

    public void setData( @NonNull String path, @NonNull String name ) {
        this.mPath = path;
        this.mName = name;
    }

    @NonNull
    public String getPath() {
        return this.mPath;
    }

    @NonNull
    public String getmName() {
        return this.mName;
    }


    /*
     * Tools
     */
    @NonNull
    public static ArrayDeque< FileListItem > getFilesDeque( @NonNull ComparableFiles[] comparableFiles ) {
        ArrayDeque< FileListItem > fileListItems = new ArrayDeque<>();

        for( int i = 0; i < comparableFiles.length; i++ )
            fileListItems.addLast( new FileListItem( comparableFiles[ i ].getFile() ) );

        return fileListItems;
    }

    @NonNull
    public static FileListItem[] getFilesArray( @NonNull ComparableFiles[] comparableFiles ) {
        FileListItem[] fileListItems = new FileListItem[ comparableFiles.length ];

        for( int i = 0; i < comparableFiles.length; i++ )
            fileListItems[ i ] = new FileListItem( comparableFiles[i ].getFile() );

        return fileListItems;
    }
}
