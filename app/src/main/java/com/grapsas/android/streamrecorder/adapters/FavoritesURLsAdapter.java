package com.grapsas.android.streamrecorder.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.misc.FavoritesURLs;

import org.json.JSONArray;
import org.json.JSONException;


public class FavoritesURLsAdapter extends BaseAdapter {

    private Context pContext;
    private JSONArray pJArray;


    public FavoritesURLsAdapter( @NonNull Context context ) {
        this.pContext = context;
        this.pJArray = new JSONArray();
    }

    @NonNull
    public JSONArray getData() {
        return this.pJArray;
    }

    @Override
    public void notifyDataSetChanged() {
        pJArray = FavoritesURLs.getUrls( this.pContext );
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.pJArray.length();
    }

    @Override
    public String getItem( int position ) {
        try {
            return this.pJArray.getString( position );
        } catch( JSONException e ) {
            e.printStackTrace();
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

        String url = this.getItem( position );
        if( url == null )
            url = "";
        TextView urlTV = (TextView) convertView.findViewById( R.id.url );

        urlTV.setText( url );

        return convertView;
    }
}
