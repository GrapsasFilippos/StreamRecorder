package com.grapsas.android.streamrecorder.misc;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grapsas.android.streamrecorder.R;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class MediaPlayerView implements MediaPlayer.OnCompletionListener {

    private int mStubResourceId;
    private WeakReference< AppCompatActivity > weakActivity;

    private MediaPlayer player;

    private View playingView;
    private TextView durationT;
    private ImageButton playPauseButton;


    public MediaPlayerView( AppCompatActivity activity, int stubResourceId ) {
        this.weakActivity = new WeakReference<>( activity );
        this.mStubResourceId = stubResourceId;
    }

    @Nullable
    private AppCompatActivity getActivity() {
        return weakActivity.get();
    }


    public void showPlayingView( @NonNull String fileName, @NonNull String fileSize, @NonNull String duration ) {
        AppCompatActivity activity = getActivity();
        if( activity == null )
            return;
        // Use recordingView for first time.
        if( this.playingView == null ) {
            this.playingView = ( ( ViewStub ) activity.findViewById( this.mStubResourceId ) ).inflate();
            this.playPauseButton = (ImageButton ) this.playingView.findViewById( R.id.playPause );
            this.durationT = (TextView ) this.playingView.findViewById( R.id.duration );
            this.playingView.findViewById( R.id.stop ).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    stopPlaying();
                }
            } );
            this.playPauseButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    playPausePlaying();
                }
            } );
        }
        // Reuse recordingView
        else
            this.playingView.setVisibility( View.VISIBLE );

        TextView fileNameV = (TextView) this.playingView.findViewById( R.id.fileName );
        TextView fileSizeV= (TextView) this.playingView.findViewById( R.id.fileSize );

        fileNameV.setText( fileName );
        fileSizeV.setText( fileSize );
        this.durationT.setText( duration );
    }

    private void hidePlayingView() {
        if( this.playingView == null )
            return;
        this.playingView.setVisibility( View.GONE );
    }


    /*
     * Media Player
     */
    public void startPlaying( FileListItem fileListItem ) throws com.grapsas.android.streamrecorder.exception.IOException {
        AppCompatActivity activity = getActivity();
        if( activity == null )
            return;
        this.stopPlaying( true, false );

        Uri fileUri = Uri.parse( fileListItem.getPath() );
        this.player = new MediaPlayer();
        this.player.setOnCompletionListener( this );
        this.player.setAudioStreamType( AudioManager.STREAM_MUSIC );
        try {
            this.player.setDataSource( activity, fileUri );
        } catch( IOException e ) {
//            e.printStackTrace();
            com.grapsas.android.streamrecorder.exception.IOException e2 =
                    new com.grapsas.android.streamrecorder.exception.IOException(
                            "Unable to MediaPlayer.setDataSource()", 1, e );
            MyLog.e( "setdatasource" );
            throw e2;
        }
        try {
            this.player.prepare();
        } catch( IOException e ) {
//            e.printStackTrace();
            com.grapsas.android.streamrecorder.exception.IOException e2 =
                    new com.grapsas.android.streamrecorder.exception.IOException(
                            "Unable to MediaPlayer.prepare()", 2, e );
            MyLog.e( "prepare" );
            throw e2;

        }
        int duration = this.player.getDuration();
        MyLog.d( duration+"" );
        duration /= 1000;
        String durationS = String.format( "%d:%02d:%02d", duration / 3600, ( duration % 3600 ) / 60, ( duration % 60 ) );
        this.showPlayingView( fileListItem.getName(), fileListItem.getSizeHuman( activity ), durationS );
        this.playPausePlaying();
    }

    public void playPausePlaying() {
        if( this.player == null )
            return;

        if( this.player.isPlaying() ) {
            this.player.pause();
            this.playPauseButton.setImageResource( R.drawable.ic_play_arrow_black_24dp );
        }
        else {
            this.player.start();
            this.playPauseButton.setImageResource( R.drawable.ic_pause_black_24dp );
        }

        this.triggerOnStartPlaying();
    }

    private void stopPlaying( boolean player, boolean gui ) {
        if( gui )
            this.hidePlayingView();

        if( !player || this.player == null )
            return;
        this.player.stop();
        this.player.release();
        this.player = null;

        this.triggerOnStopPlaying();
    }

    public void stopPlaying() {
        this.stopPlaying( true, true );
    }


    /*
     * Implements MediaPlayer.OnCompletionListener
     */
    @Override
    public void onCompletion( MediaPlayer mp ) {
        this.playPauseButton.setImageResource( R.drawable.ic_play_arrow_black_24dp );
    }


    /*
     * Public interface
     */
    public interface Events {
        void onStartPlaying();
        void onStopPlaying();
    }

    private void triggerOnStartPlaying() {
        AppCompatActivity activity = getActivity();
        if( activity == null || !( activity instanceof Events ) )
            return;
        ( (Events) activity).onStartPlaying();
    }

    private void triggerOnStopPlaying() {
        AppCompatActivity activity = getActivity();
        if( activity == null || !( activity instanceof Events ) )
            return;
        ( (Events) activity).onStopPlaying();
    }

}
