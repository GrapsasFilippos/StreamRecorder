package com.grapsas.android.streamrecorder.dialogs;


import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.grapsas.android.streamrecorder.R;


public class AddFavoriteURL extends DialogFragment {

    protected EditText urlTV;


    public AddFavoriteURL() {}

    public static AddFavoriteURL newInstance() {
        Bundle args = new Bundle();
        AddFavoriteURL fragment = new AddFavoriteURL();
        fragment.setArguments( args );
        return fragment;
    }


    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate( R.layout.dialog_content_add_favorite_url, null );
        this.urlTV = (EditText) contentView.findViewById( R.id.editText );

        builder.setView( contentView );
        builder.setPositiveButton( R.string.Save_Rec, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                AddFavoriteURL.Interaction listener = (AddFavoriteURL.Interaction) getActivity();
                listener.saveAndRec( urlTV.getText().toString() );
            }
        } );
        builder.setNeutralButton( R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                AddFavoriteURL.Interaction listener = (AddFavoriteURL.Interaction) getActivity();
                listener.save( urlTV.getText().toString() );
            }
        } );
        builder.setNegativeButton( R.string.Cancel, null );

        return builder.create();
    }


    public interface Interaction {
        void startAddFavoriteURL();
        void saveAndRec( String url );
        void save( String url );
    }
}
