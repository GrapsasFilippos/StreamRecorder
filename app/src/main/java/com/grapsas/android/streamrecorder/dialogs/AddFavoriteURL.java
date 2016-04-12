package com.grapsas.android.streamrecorder.dialogs;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.grapsas.android.streamrecorder.R;


public class AddFavoriteURL extends DialogFragment {

    public enum TYPE { FAVORITE, JUST_REC }

    private final static String TYPE_KEY = "type";

    protected EditText urlTV;
    private TYPE pType;


    public AddFavoriteURL() {}

    public static AddFavoriteURL newInstance( TYPE type ) {
        Bundle args = new Bundle();
        args.putSerializable( TYPE_KEY, type );

        AddFavoriteURL fragment = new AddFavoriteURL();
        fragment.setArguments( args );

        return fragment;
    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        this.pType = (TYPE) getArguments().getSerializable( TYPE_KEY );
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate( R.layout.dialog_content_add_favorite_url, null );
        this.urlTV = (EditText) contentView.findViewById( R.id.editText );
        ImageButton clearB = (ImageButton) contentView.findViewById( R.id.clear );

        clearB.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                urlTV.setText( "" );
            }
        } );

        ClipboardManager clipboard = (ClipboardManager ) getActivity()
                .getSystemService( Context.CLIPBOARD_SERVICE );
        if(
                clipboard.hasPrimaryClip() &&
                clipboard.getPrimaryClipDescription()
                        .hasMimeType( ClipDescription.MIMETYPE_TEXT_PLAIN ) ) {
            ClipData.Item cItem = clipboard.getPrimaryClip().getItemAt(0);
            this.urlTV.setText( cItem.getText() );
        }

        builder.setView( contentView );
        this.initButtons( builder );

        return builder.create();
    }


    /*
     * GUI Tools
     */
    private void initButtons(  AlertDialog.Builder builder ) {
        if( this.pType == TYPE.FAVORITE ) {
            builder.setNeutralButton( R.string.Save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    AddFavoriteURL.Interaction listener = ( AddFavoriteURL.Interaction ) getActivity();
                    listener.save( urlTV.getText().toString() );
                }
            } );
            builder.setPositiveButton( R.string.Save_Rec, new DialogInterface.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    AddFavoriteURL.Interaction listener = ( AddFavoriteURL.Interaction ) getActivity();
                    listener.saveAndRec( urlTV.getText().toString() );
                }
            } );
        }
        else
            builder.setPositiveButton( R.string.Rec, new DialogInterface.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    AddFavoriteURL.Interaction listener = ( AddFavoriteURL.Interaction ) getActivity();
                    listener.rec( urlTV.getText().toString() );
                }
            } );

        builder.setNegativeButton( R.string.Cancel, null );
    }


    /*
     * Interfaces
     */
    public interface Interaction {
        void startAddFavoriteURL();
        void saveAndRec( @NonNull String url );
        void save( @NonNull String url );
        void rec( @NonNull String url );
    }
}
