package com.grapsas.android.streamrecorder.misc;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import com.grapsas.android.streamrecorder.App;
import com.grapsas.android.streamrecorder.exception.NeedActivityException;
import com.grapsas.android.streamrecorder.exception.NeedWorkingDirectoryException;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@TargetApi( Build.VERSION_CODES.LOLLIPOP)
public class IOV21 {

    public static final int DIR_PICKER_RESULT_CODE = 1;


    /*
     * Filesystem tools
     */
    public static void launchDirectoryPicker() {
        Activity lastActivity = App.getInstance().getLastActivity();
        if( lastActivity == null )
            return;
        Intent intent = new Intent( Intent.ACTION_OPEN_DOCUMENT_TREE );
        lastActivity.startActivityForResult( intent, DIR_PICKER_RESULT_CODE );
    }

    public static void resultDirectoryPicker( @NonNull Uri treeUri ) {
        Activity lastActivity = App.getInstance().getLastActivity();
        if( lastActivity == null )
            return;
        lastActivity.getContentResolver().takePersistableUriPermission( treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
    }

    @NonNull
    private static Uri getWorkingDirectoryUri() throws
            NeedActivityException, NeedWorkingDirectoryException {
        Activity lastActivity = App.getInstance().getLastActivity();
        if( lastActivity == null )
            throw new NeedActivityException();
        List< UriPermission > UriPermissions = lastActivity.getContentResolver().getPersistedUriPermissions();
        if( UriPermissions.size() == 0 )
            throw new NeedWorkingDirectoryException();
        else {
//            for( UriPermission permission : UriPermissions) {
//                MyLog.d( permission.toString() );
//            }
            //noinspection UnnecessaryLocalVariable
            Uri wDir = UriPermissions.get( 0 ).getUri();
            return wDir;
        }
    }

    @NonNull
    private static DocumentFile[] getFiles( @NonNull Uri wDirUri ) throws
            NeedWorkingDirectoryException, NeedActivityException {
        Activity lastActivity = App.getInstance().getLastActivity();
        if( lastActivity == null )
            throw new NeedActivityException();

        DocumentFile wDirDF = DocumentFile.fromTreeUri( lastActivity, wDirUri );

        return wDirDF.listFiles();
    }

    @NonNull
    private static DocumentFile[] getFiles() throws
            NeedWorkingDirectoryException, NeedActivityException {
        return getFiles( getWorkingDirectoryUri() );
    }

    @Nullable
    public static FileDescriptor createNewFile() {
        Activity lastActivity = App.getInstance().getLastActivity();
        Uri wDirUri;
        try {
            wDirUri = getWorkingDirectoryUri();
        } catch( NeedActivityException e ) {
            e.printStackTrace();
            return null;
        } catch( NeedWorkingDirectoryException e ) {
            e.printStackTrace();
            return null;
        }
        if( lastActivity == null )
            return null;
        DocumentFile wDirDF = DocumentFile.fromTreeUri( lastActivity, wDirUri );

        DocumentFile newFile = wDirDF.createFile( "video/3gpp", IO.generateFileName() );
        try {
            AssetFileDescriptor afd = lastActivity
                    .getContentResolver()
                    .openAssetFileDescriptor( newFile.getUri(), "w" );
            if( afd == null )
                throw new NullPointerException();
            return afd.getFileDescriptor();
        } catch( FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }


    /*
     * Records tools
     */
    @NonNull
    private static DocumentFile[] getRecords_DF() throws
            NeedActivityException, NeedWorkingDirectoryException {
        DocumentFile[] dFiles = getFiles();

        ArrayList< ComparableDocumentFile > recordsAL = new ArrayList<>();
        for( DocumentFile dFile : dFiles ) {
            if( dFile.getType().equals( "audio/3gpp" ) )
                recordsAL.add( new ComparableDocumentFile( dFile ) );
        }
        Collections.sort( recordsAL );

        DocumentFile[] recordsArray = new DocumentFile[ recordsAL.size() ];
        int numOfRecords = recordsAL.size();
        for( int i = 0; i < numOfRecords; i++ )
            recordsArray[ i ] = recordsAL.remove( recordsAL.size() - 1 ).getDFile();

        return recordsArray;
    }

    @NonNull
    public static FileListItem[] getRecords_FLIArray() throws
            NeedWorkingDirectoryException, NeedActivityException {
        DocumentFile[] dFiles = getRecords_DF();

        FileListItem[] fileListItems = new FileListItem[ dFiles.length ];
        for( int i = 0; i < dFiles.length; i++ )
            fileListItems[ i ] = new FileListItem( dFiles[ i ] );

        return fileListItems;
    }

}
