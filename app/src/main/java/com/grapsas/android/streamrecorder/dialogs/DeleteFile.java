package com.grapsas.android.streamrecorder.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.grapsas.android.streamrecorder.R;


public class DeleteFile extends DialogFragment {

    private Uri pUri;


    public static DeleteFile newInstance( Uri uri ) {
        DeleteFile fragment = new DeleteFile();
        Bundle bundle = new Bundle();
        bundle.putString( "uri", uri.toString() );
        fragment.setArguments( bundle );
        return fragment;
    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        Bundle bundle = getArguments();
        this.pUri = Uri.parse( bundle.getString( "uri" ) );
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

        builder.setMessage( getString( R.string.RemoveFilePermanently ) + "\n" + pUri.getPath() );
        builder.setPositiveButton( R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                Activity activity = getActivity();
                if( activity != null && activity instanceof Response )
                    ( ( Response ) activity ).deleteResponse( false, pUri );
            }
        } );
        builder.setNegativeButton( R.string.Remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                Activity activity = getActivity();
                if( activity != null && activity instanceof Response )
                    ( ( Response ) activity ).deleteResponse( true, pUri );
            }
        } );

        return builder.create();
    }


    public interface Response {
        void deleteResponse( boolean delete, @NonNull Uri file );
    }
}
