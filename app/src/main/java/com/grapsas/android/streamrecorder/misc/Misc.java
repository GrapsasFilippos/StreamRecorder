package com.grapsas.android.streamrecorder.misc;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import java.io.IOException;
import java.io.InputStream;


public final class Misc {

    private static final int MIN_IN_SEC = 60;
    private static final int HOUR_IN_SEC = 3600 /* MIN_IN_SEC * 60 */;


    public static String fromDuration( long ms ) {
        MyLog.d( "ms: " + ms );
        int duration = (int) ( 10000 / DateUtils.SECOND_IN_MILLIS);
        if (duration < 0) {
            duration = -duration;
        }

        int h = 0;
        int m = 0;

        if (duration >= HOUR_IN_SEC) {
            h = duration / HOUR_IN_SEC;
            duration -= h * HOUR_IN_SEC;
        }
        if (duration >= MIN_IN_SEC) {
            m = duration / MIN_IN_SEC;
            duration -= m * MIN_IN_SEC;
        }
        int s = duration;
        MyLog.d( h + ":" + m + ":" + s );

        return
                ( h != 0 ? h + ":" : "") +
                ( m != 0 ? m + ":" : "") +
                s;
    }

    public static String humanBytes( long bytes, boolean si ) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static @Nullable String loadJSONFromAsset(
            @NonNull Context context, @NonNull String fileName ) {
        String json = null;
        try {
            InputStream is = context.getAssets().open( fileName );
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read( buffer );
            is.close();
            json = new String( buffer, "UTF-8" );
        } catch( IOException ex ) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
