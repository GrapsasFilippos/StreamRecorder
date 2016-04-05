package com.grapsas.android.streamrecorder.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.grapsas.android.streamrecorder.R;


public class DeleteFavoriteURL extends DialogFragment {

    public static final String POSITION_KEY = "position";
    public static final String URL_KEY = "url";

    private String pUrl;
    private int pPosition;


    public DeleteFavoriteURL() {}

    public static DeleteFavoriteURL newInstance( int position, String url ) {
        DeleteFavoriteURL fragment = new DeleteFavoriteURL();
        Bundle bundle = new Bundle();
        bundle.putInt( POSITION_KEY, position );
        bundle.putString( URL_KEY, url );
        fragment.setArguments( bundle );
        return fragment;
    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Bundle bundle = getArguments();
        this.pPosition = bundle.getInt( POSITION_KEY );
        this.pUrl = bundle.getString( URL_KEY );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

        builder.setMessage( getString( R.string.RemoveURL ) + "\n" + this.pUrl );
        builder.setPositiveButton( R.string.Cancel, null);
        builder.setNegativeButton( R.string.Remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                Activity activity = getActivity();
                if( activity != null && activity instanceof Interaction )
                    ( ( Interaction ) activity ).DeleteURL( pPosition );
            }
        } );

        return builder.create();
    }

    public interface Interaction {
        void startDeleteFavoriteURL( int position, @NonNull String url );
        void DeleteURL( int position );
    }
}
