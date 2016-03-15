package com.grapsas.android.streamrecorder.misc;


import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;


public class ComparableDocumentFile implements Comparable< ComparableDocumentFile > {

    private DocumentFile mDFile;

    public ComparableDocumentFile( DocumentFile dFile ) {
        this.mDFile = dFile;
    }

    public long getLastModified() {
        return mDFile.lastModified();
    }

    public DocumentFile getDFile() {
        return this.mDFile;
    }


    /*
     * Implements Comparable
     */
    @Override
    public int compareTo( @Nullable ComparableDocumentFile another ) {
        if( another == null )
            return -1;
        if( this.getLastModified() > another.getLastModified() )
            return 1;
        else if( this.getLastModified() < another.getLastModified() )
            return -1;
        else
            return 0;
    }
}
