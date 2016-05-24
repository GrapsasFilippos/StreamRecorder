package com.grapsas.android.streamrecorder.misc.media;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by graphi on 5/24/16.
 */
public class MediaURL {

    private String pTitle;
    private String pURL;


    public MediaURL( @Nullable String title, @NonNull String url ) {
        this.pTitle = title;
        this.pURL = url;
    }


    @Nullable
    public String getTitle() {
        return this.pTitle;
    }

    @NonNull
    public String getURL() {
        return this.pURL;
    }
}
