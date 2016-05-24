package com.grapsas.android.streamrecorder.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.activities.FavoritesURLsActivity;
import com.grapsas.android.streamrecorder.misc.FavoritesURLs;
import com.grapsas.android.streamrecorder.misc.Misc;
import com.grapsas.android.streamrecorder.misc.media.MediaURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FavoritesURLsAdapter extends BaseAdapter {

    private Context pContext;
    private String pType;
    private JSONArray pJArray;


    public FavoritesURLsAdapter( @NonNull Context context ) {
        this.init( context, null );
    }

    public FavoritesURLsAdapter( @NonNull Context context, @NonNull String type ) {
        this.init( context, type );
    }

    private void init( @NonNull Context context, @Nullable String type ) {
        this.pContext = context;
        this.pJArray = new JSONArray();
        if( type == null ) {
            this.pType = FavoritesURLsActivity.TYPE_DEFAULT;
        }
        else {
            this.pType = type;
        }
    }


    @NonNull
    public JSONArray getData() {
        return this.pJArray;
    }

    @Override
    public void notifyDataSetChanged() {
        if( this.pType.equals( FavoritesURLsActivity.TYPE_FAV ) ) {
            pJArray = FavoritesURLs.getUrls( this.pContext );
        }
        else if( this.pType.equals( FavoritesURLsActivity.TYPE_PRE_EXISTS ) ) {
            String jString = Misc.loadJSONFromAsset(
                    this.pContext, FavoritesURLsActivity.PRE_EXISTS_FILE_NAME );
            try {
                pJArray = new JSONArray( jString );
            } catch( JSONException e ) {
                e.printStackTrace();
            }
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.pJArray.length();
    }

    @Override
    public MediaURL getItem( int position ) {
        if( this.pType.equals( FavoritesURLsActivity.TYPE_FAV ) ) {
            try {
                return new MediaURL( null, this.pJArray.getString( position ) );
            } catch( JSONException e ) {
                e.printStackTrace();
            }
        }
        else if( this.pType.equals( FavoritesURLsActivity.TYPE_PRE_EXISTS ) ) {
            try {
                JSONObject jObject = this.pJArray.getJSONObject( position );
                return new MediaURL( jObject.getString( "title" ), jObject.getString( "url" ) );
            } catch( JSONException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public long getItemId( int position ) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        if( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate( R.layout.list_item_favarites_url, parent, false );
        }

        TextView titleTV = (TextView) convertView.findViewById( R.id.title );
        TextView urlTV = (TextView) convertView.findViewById( R.id.url );

        MediaURL mediaURL = this.getItem( position );
        String title;
        String url;

        if( mediaURL.getTitle() == null ) {
            url = mediaURL.getURL();
            title = url;
            urlTV.setVisibility( View.GONE );
        }
        else {
            title = mediaURL.getTitle();
            url = mediaURL.getURL();
            urlTV.setVisibility( View.VISIBLE );
        }

        titleTV.setText( title );
        urlTV.setText( url );

        return convertView;
    }
}
