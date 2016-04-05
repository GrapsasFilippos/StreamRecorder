package com.grapsas.android.streamrecorder.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.grapsas.android.streamrecorder.MyActivity;
import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.dialogs.AddFavoriteURL;
import com.grapsas.android.streamrecorder.dialogs.DeleteFavoriteURL;
import com.grapsas.android.streamrecorder.fragments.FavoritesURLsActivityFragment;
import com.grapsas.android.streamrecorder.misc.FavoritesURLs;
import com.grapsas.android.streamrecorder.misc.MyLog;


public class FavoritesURLsActivity extends MyActivity implements
        AddFavoriteURL.Interaction,
        DeleteFavoriteURL.Interaction {

    public static final int RESULT_CODE = 2;
    public static final String RESULT_URL_KEY = "url";


    /*
     * Activity Overrides
     */
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_favorites_urls );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        if( getSupportActionBar() != null )
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if( item.getItemId() == android.R.id.home )
            finish();
        return super.onOptionsItemSelected( item );
    }


    // Tools
    @Nullable
    public AddFavoriteURL.Interaction getFragment( int rId ) {
        AddFavoriteURL.Interaction fragment = (AddFavoriteURL.Interaction)
                getSupportFragmentManager().findFragmentById( rId );
        return fragment;
    }

    public void urlSelected( @NonNull String url ) {
        Intent intent = new Intent();
        intent.putExtra( RESULT_URL_KEY, url );
        setResult( RESULT_OK, intent );
        finish();
    }


    /*
     * Implements AddFavoriteURL.Interaction
     */
    @Override
    public void startAddFavoriteURL() {
        AddFavoriteURL.newInstance().show( getFragmentManager(), "" );
    }

    @Override
    public void saveAndRec( String url ) {
        AddFavoriteURL.Interaction fragment = this.getFragment( R.id.fragment );
        fragment.saveAndRec( url );
    }

    @Override
    public void save( String url ) {
        AddFavoriteURL.Interaction fragment = this.getFragment( R.id.fragment );
        if( fragment == null )
            return;
        fragment.save( url );
    }


    /*
     * Implements DeleteFavoriteURL.Interaction
     */
    @Override
    public void startDeleteFavoriteURL( int position, @NonNull String url ) {
        DeleteFavoriteURL.newInstance( position, url ).show( getFragmentManager(), "" );
    }

    @Override
    public void DeleteURL( int position ) {
        FavoritesURLs.removeURL( this, position );

        if( getFragment( R.id.fragment ) instanceof FavoritesURLsActivityFragment )
            ( ( FavoritesURLsActivityFragment ) getFragment( R.id.fragment ) ).notifyDataSetChanged();
    }
}
