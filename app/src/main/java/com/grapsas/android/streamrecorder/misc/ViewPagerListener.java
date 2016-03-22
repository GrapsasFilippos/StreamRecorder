package com.grapsas.android.streamrecorder.misc;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.grapsas.android.streamrecorder.interfaces.OnPageChangeListener;

import java.lang.ref.WeakReference;


public class ViewPagerListener implements ViewPager.OnPageChangeListener {

    private WeakReference< OnPageChangeListener > pListener;
    private boolean moving;


    public ViewPagerListener( @Nullable OnPageChangeListener listener ) {
        this.moving = false;
        if( listener != null )
            this.pListener = new WeakReference<>( listener );
    }

    @Override
    public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
    }

    @Override
    public void onPageSelected( int position ) {
        this.triggerStopMoving();
    }

    @Override
    public void onPageScrollStateChanged( int state ) {
        String sState = "error";
            if( state == ViewPager.SCROLL_STATE_IDLE )
                sState = "SCROLL_STATE_IDLE";
            else if( state == ViewPager.SCROLL_STATE_DRAGGING ) {
                sState = "SCROLL_STATE_DRAGGING";
                this.triggerStartMoving();
            }
            else if( state == ViewPager.SCROLL_STATE_SETTLING ) {
                sState = "SCROLL_STATE_SETTLING";
            }

//        MyLog.d( "ViewPagerListener.onPageScrollStateChanged( " + sState + " )" );

        if( state != ViewPager.SCROLL_STATE_IDLE )
            this.triggerStartMoving();
        else
            this.triggerStopMoving();
    }

    private void triggerStartMoving() {
        if( this.moving )
            return;

//        MyLog.d( "Start Moving" );
        this.moving = true;
        if( this.pListener != null && this.pListener.get() != null )
            this.pListener.get().pagerStartMoving();
    }

    private void triggerStopMoving() {
        if( !this.moving )
            return;

//        MyLog.d( "Stop Moving" );
        this.moving = false;
        if( this.pListener != null && this.pListener.get() != null )
            this.pListener.get().pagerFinishMoving();
    }


}
