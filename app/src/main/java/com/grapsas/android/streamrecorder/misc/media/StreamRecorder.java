package com.grapsas.android.streamrecorder.misc.media;


import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Chronometer;

import com.grapsas.android.streamrecorder.misc.Misc;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StreamRecorder implements Recorder {

    private ThreadsConnector pTC;
    private NetworkByteByByteCopy networkBBCp;

    private String pUrl;
    private WeakReference< Chronometer > pwChronometer;


    public StreamRecorder( String url, Chronometer chronometer ) {
        this.pUrl = url;
        this.pwChronometer = new WeakReference<>( chronometer );
    }

    @Nullable
    public Chronometer getChronometer() {
        if( this.pwChronometer == null || this.pwChronometer.get() == null )
            return null;
        return this.pwChronometer.get();
    }


    @Override
    public boolean startRecording( FileDescriptor fd ) {
        if( this.networkBBCp != null  ) {
            if( this.networkBBCp.isFinished() )
                this.networkBBCp = null;
            else
                this.networkBBCp.cancel( true );
        }

        this.pTC = new ThreadsConnector( fd, pUrl );
        if( this.getChronometer() != null )
            this.getChronometer().setOnChronometerTickListener(
                    ( new MediaChronometer( this.pTC ) ) );
        this.networkBBCp = new NetworkByteByByteCopy( this.pTC );
        this.networkBBCp.execute();

        return true;
    }

    @Override
    public boolean stopRecording() {
        if( this.getChronometer() != null )
            this.getChronometer().setOnChronometerTickListener( null );
        this.networkBBCp.stop();

        return true;
    }


    private static class MediaChronometer implements Chronometer.OnChronometerTickListener {

        private ThreadsConnector pTC;
        private ParcelFileDescriptor pPFD;
        private MediaPlayer pPlayer;


        public MediaChronometer( ThreadsConnector threadsConnector ) {
            this.pTC = threadsConnector;
            try {
                this.pPFD = ParcelFileDescriptor.dup( this.pTC.getFD() );
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChronometerTick( Chronometer chronometer ) {
            String str = "";
            str += Misc.humanBytes( this.pTC.getBytesRead(), true );

            chronometer.setText( str );
        }

    }

    private static class ThreadsConnector {

        // TODO: keep pfd instead of fd.
        private FileDescriptor pFileDescriptor;

        private boolean pRecording;
        private long pBytesRead;
        private boolean pRecordingFinished = false;

        private String pUrl;


        public ThreadsConnector( @NonNull FileDescriptor fileDescriptor, @NonNull String url ) {
            this.pUrl = url;
            this.pFileDescriptor = fileDescriptor;
        }

        @NonNull
        public FileDescriptor getFD() {
            return this.pFileDescriptor;
        }

        @NonNull
        public String getUrl() {
            return this.pUrl;
        }

        public boolean isRecording() {
            return this.pRecording;
        }

        public void setRecording( boolean recording ) {
            this.pRecording = recording;
        }

        public long getBytesRead() {
            return this.pBytesRead;
        }

        public void setBytesRead( long bytesRead ) {
            this.pBytesRead = bytesRead;
        }

        public boolean isRecordingFinished() {
            return this.pRecordingFinished;
        }

        public void setRecordingFinished( boolean finished ) {
            this.pRecordingFinished = finished;
        }

    }


    private static class NetworkByteByByteCopy extends AsyncTask< Void, Void, Boolean > {

        private ThreadsConnector pTC;

        private boolean pStop = false;
        private boolean pFinished = false;


        public NetworkByteByByteCopy( ThreadsConnector threadsConnector ) {
            this.pTC = threadsConnector;
        }

        @Override
        protected Boolean doInBackground( Void... params ) {
            ParcelFileDescriptor pfd;
            try {
                pfd = ParcelFileDescriptor.dup( this.pTC.getFD() );
            } catch( IOException e ) {
                e.printStackTrace();
                return false;
            }

            FileOutputStream outputStream;
            try {
                URL url = new URL( this.pTC.getUrl() );
                InputStream inputStream;

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url( url )
                        .build();
                Response response = okHttpClient.newCall( request ).execute();
                inputStream = response.body().byteStream();

                outputStream = new FileOutputStream( pfd.getFileDescriptor() );
                this.pTC.setRecording( true );
                int c;
                int bytesRead = 0;
                boolean lStop = false;
                while( ( c = inputStream.read() ) != -1 && !lStop ) {
                    this.pTC.setBytesRead( ++bytesRead );
                    outputStream.write( c );
                    lStop = this.isCancelled() || this.pStop;
                }
                this.pTC.setRecording( false );
                this.pTC.setRecordingFinished( true );
                outputStream.close();
            } catch( MalformedURLException e ) {
                e.printStackTrace();
                return false;
            } catch( IOException e ) {
                e.printStackTrace();
                return false;
            }

            this.pFinished = true;
            return true;
        }

        @Override
        protected void onCancelled( Boolean aBoolean ) {
            super.onCancelled( aBoolean );
            this.pTC.setRecordingFinished( true );
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.pTC.setRecordingFinished( true );
        }


        public void stop() {
            this.pStop = true;
            this.pTC.setRecordingFinished( true );
        }

        public boolean isFinished() {
            return this.pFinished;
        }

    }


}
