package com.grapsas.android.streamrecorder;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;


import java.lang.ref.WeakReference;


public class App implements Application.ActivityLifecycleCallbacks {

    private static App mApp;
    private static WeakReference< Activity > weakLastActivity;
    private static WeakReference< Context > weakPreActivityContext;


    public static synchronized App getInstance() {
        if( mApp == null )
            mApp = new App();
        return mApp;
    }

    @Nullable
    public Context getAvailableContext() {
        if( this.getLastActivity() != null)
            return this.getLastActivity();
        else
            return this.getPreActivityContext();
    }

    public void setPreActivityContext( @Nullable Context context ) {
        if( context == null )
            weakPreActivityContext = null;
        else
            weakPreActivityContext = new WeakReference<>( context );
    }

    @Nullable
    public Context getPreActivityContext() {
        if( weakPreActivityContext == null )
            return null;
        else
            return weakPreActivityContext.get();
    }

    public void setLastActivity( @Nullable Activity activity ) {
        if( activity == null )
            weakLastActivity = null;
        else {
            setPreActivityContext( activity.getApplicationContext() );
            weakLastActivity = new WeakReference<>( activity );
        }
    }

    @Nullable
    public Activity getLastActivity() {
        if( weakLastActivity == null )
            return null;
        return weakLastActivity.get();
    }


    /*
     * Implements Application.ActivityLifecycleCallbacks
    */
    @Override
    public void onActivityCreated( Activity activity, Bundle savedInstanceState ) {
        setLastActivity( activity );
    }

    @Override
    public void onActivityStarted( Activity activity ) {
        setLastActivity( activity );
    }

    @Override
    public void onActivityResumed( Activity activity ) {
        setLastActivity( activity );
    }

    @Override
    public void onActivityPaused( Activity activity ) {
        setLastActivity( null );
    }

    @Override
    public void onActivityStopped( Activity activity ) {}

    @Override
    public void onActivitySaveInstanceState( Activity activity, Bundle outState ) {}

    @Override
    public void onActivityDestroyed( Activity activity ) {}

}
