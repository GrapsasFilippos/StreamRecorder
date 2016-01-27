package com.grapsas.android.streamrecorder.misc;


import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Formatter;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;

public class FileListItem {

    private String mPath;
    private String mName;
    private long mModified;
    private long mSize;
    private long mDuration;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yy HH:mm:ss" );

    public FileListItem( @NonNull File file ) {
        this.mPath = file.getPath();
        this.mName = file.getName();
        this.mModified = file.lastModified();
        this.mSize = file.length();
    }

    public void setData( @NonNull String path, @NonNull String name, long modified, long size ) {
        this.mPath = path;
        this.mName = name;
        this.mModified = modified;
        this.mSize = size;
    }

    @NonNull
    public String getPath() {
        return this.mPath;
    }

    @NonNull
    public String getName() {
        return this.mName;
    }

    public long getModified() {
        return this.mModified;
    }

    @NonNull
    public String getModifiedHuman() {
        return simpleDateFormat.format( new Date( this.getModified() ) );
    }

    public long getSize() {
        return this.mSize;
    }

    @NonNull
    public String getSizeHuman( Context context ) {
        return Formatter.formatShortFileSize( context, this.getSize() );
    }

    public long getDuration() {
        return this.mDuration;
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
