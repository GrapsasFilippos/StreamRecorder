package com.grapsas.android.streamrecorder.misc;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;


public class FavoritesURLs {

    public static final String PREFERENCES_FILE_NAME = "favoritesURLs";
    public static final String URLS_KEY = "urls";

    private static SharedPreferences sharedPreferences;


    @NonNull
    public static synchronized SharedPreferences getSharedPreferences( @NonNull Context context ) {
        if( sharedPreferences == null )
            sharedPreferences = context.getSharedPreferences(
                    PREFERENCES_FILE_NAME, Context.MODE_PRIVATE );
        return sharedPreferences;
    }

    public static boolean setUrls( @NonNull Context context, @NonNull JSONArray jsonArray ) {
        SharedPreferences.Editor editor = getSharedPreferences( context ).edit();
        editor.putString( URLS_KEY, jsonArray.toString() );
        editor.apply();

        return true;
    }

    @NonNull
    public static JSONArray getUrls( @NonNull Context context ) {
        String jString = getSharedPreferences( context ).getString( URLS_KEY, "[]" );
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray( jString );
        } catch( JSONException e ) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    @NonNull
    private static JSONArray removeURLv16( @NonNull JSONArray jsonArray, int position ) {
        JSONArray newJArray = new JSONArray();

        int len = jsonArray.length();
        try {
            for( int i = 0; i < len; i++ )
                if( i != position )
                    newJArray.put( jsonArray.getString( i ) );
        } catch( JSONException e ) {
            e.printStackTrace();
        }

        return newJArray;
    }

    @TargetApi( Build.VERSION_CODES.KITKAT )
    @NonNull
    private static JSONArray removeURLv19( @NonNull JSONArray jsonArray, int position ) {
        jsonArray.remove( position );
        return jsonArray;
    }

    public static boolean removeURL( Context context, int position ) {
        JSONArray jsonArray = getUrls( context );
        JSONArray newArray;

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            newArray = removeURLv19( jsonArray, position );
        else
            newArray = removeURLv16( jsonArray, position );

        setUrls( context, newArray );

        return true;
    }
}
