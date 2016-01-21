package com.grapsas.android.streamrecorder.misc;


import android.support.annotation.Nullable;
import android.util.Log;

public class MyLog {

    public static final String TAG = "Filippos";

    public static void v( @Nullable String message ) {
        Log.v( TAG, message );
    }

    public static void d( @Nullable String message ) {
        Log.d( TAG, message );
    }

    public static void i( @Nullable String message ) {
        Log.i( TAG, message );
    }

    public static void w( @Nullable String message ) {
        Log.w( TAG, message );
    }

    public static void e( @Nullable String message ) {
        Log.e( TAG, message );
    }

    public static void wtf( @Nullable String message ) {
        Log.wtf( TAG, message );
    }

    public static void t( @Nullable String message ) {
        Log.v( TAG + ":Toast", message);
    }
}
