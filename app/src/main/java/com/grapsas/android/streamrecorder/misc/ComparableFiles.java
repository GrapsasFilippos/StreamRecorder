package com.grapsas.android.streamrecorder.misc;

import android.support.annotation.NonNull;

import java.io.File;

public class ComparableFiles implements Comparable< ComparableFiles > {

    private File mFile;

    public ComparableFiles( File file ) {
        this.mFile = file;
    }

    @NonNull
    public File getFile() {
        return this.mFile;
    }

    @Override
    public int compareTo( ComparableFiles another ) {
        if( this.mFile.lastModified() > another.getFile().lastModified() )
            return 1;
        else if( this.mFile.lastModified() < another.getFile().lastModified() )
            return -1;
        else
            return 0;
    }

}
