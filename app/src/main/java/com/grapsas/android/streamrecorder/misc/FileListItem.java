package com.grapsas.android.streamrecorder.misc;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.text.format.Formatter;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;


public class FileListItem {

    private Uri mUri;
    private String mName;
    private long mModified;
    private long mSize;

    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat( "dd/MM/yy HH:mm:ss" );

    public FileListItem( @NonNull File file ) {
        this.mUri = Uri.fromFile( file );
        this.mName = file.getName();
        this.mModified = file.lastModified();
        this.mSize = file.length();
    }

    public FileListItem( @NonNull DocumentFile dFile ) {
        this.mUri = dFile.getUri();
        this.mName = dFile.getName();
        this.mModified = dFile.lastModified();
        this.mSize = dFile.length();
    }

    public void setData( @NonNull Uri uri, @NonNull String name, long modified, long size ) {
        this.mUri = uri;
        this.mName = name;
        this.mModified = modified;
        this.mSize = size;
    }

    @NonNull
    public Uri getUri() {
        return this.mUri;
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


    /*
     * Tools
     */
    @NonNull
    public static ArrayDeque< FileListItem > getFilesDeque(
            @NonNull ComparableFiles[] comparableFiles ) {
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

    @NonNull
    public static FileListItem[] getFilesArray( @NonNull ComparableDocumentFile[] comparableDFile ) {
        FileListItem[] fileListItems = new FileListItem[ comparableDFile.length ];

        for( int i = 0; i < comparableDFile.length; i++ )
            fileListItems[ i ] = new FileListItem( comparableDFile[i ].getDFile() );

        return fileListItems;
    }
}
