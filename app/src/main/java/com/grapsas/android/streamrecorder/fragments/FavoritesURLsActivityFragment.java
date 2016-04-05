package com.grapsas.android.streamrecorder.fragments;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.activities.FavoritesURLsActivity;
import com.grapsas.android.streamrecorder.adapters.FavoritesURLsAdapter;
import com.grapsas.android.streamrecorder.dialogs.AddFavoriteURL;
import com.grapsas.android.streamrecorder.dialogs.DeleteFavoriteURL;
import com.grapsas.android.streamrecorder.misc.FavoritesURLs;

import org.json.JSONArray;


public class FavoritesURLsActivityFragment extends Fragment implements
        AddFavoriteURL.Interaction {


    private AddFavoriteURL.Interaction pAddListener;
    private DeleteFavoriteURL.Interaction pDeleteListener;

    private FavoritesURLsAdapter adapter;


    public FavoritesURLsActivityFragment() {
    }


    /*
     * Fragment overrides
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View rootView = inflater.inflate( R.layout.fragment_favorites_urls, container, false );

        this.initListView( rootView );
        this.initFAB( rootView );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if( getActivity() instanceof AddFavoriteURL.Interaction )
            this.pAddListener = (AddFavoriteURL.Interaction) getActivity();
        if( getActivity() instanceof DeleteFavoriteURL.Interaction )
            this.pDeleteListener = (DeleteFavoriteURL.Interaction) getActivity();
    }

    @Override
    public void onPause() {
        this.pAddListener = null;
        this.pDeleteListener = null;
        super.onPause();
    }

    /*
     * GUI Tools
     */
    private void initListView( @NonNull View rootView ) {
        adapter = new FavoritesURLsAdapter( this.getContext() );
        ListView listView = (ListView) rootView.findViewById( R.id.listView );
        listView.setAdapter( adapter );
        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView< ? > parent, View view, int position, long id ) {
                if( pDeleteListener == null )
                    return false;
                pDeleteListener.startDeleteFavoriteURL( position, adapter.getItem( position ) );
                return true;
            }
        } );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {
                FavoritesURLsActivity activity = (FavoritesURLsActivity) getActivity();
                activity.urlSelected( adapter.getItem( position ) );
            }
        } );

        adapter.notifyDataSetChanged();
    }

    private void initFAB( @NonNull View rootView ) {
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( pAddListener != null )
                    pAddListener.startAddFavoriteURL();
            }
        } );
    }


    /*
     * Implements AddFavoriteURL.Interaction
     */
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


    /*
     * Implements AddFavoriteURL.Interaction
     */
    @Override
    public void startAddFavoriteURL() {
    }

    @Override
    public void saveAndRec( String url ) {
    }

    @Override
    public void save( String url ) {
        JSONArray jsonArray = adapter.getData();
        jsonArray.put( url );
        FavoritesURLs.setUrls( getContext(), jsonArray );
        adapter.notifyDataSetChanged();
    }

}
