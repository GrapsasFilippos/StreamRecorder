package com.grapsas.android.streamrecorder.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.dialogs.AddFavoriteURL;
import com.grapsas.android.streamrecorder.dialogs.DeleteFavoriteURL;
import com.grapsas.android.streamrecorder.fragments.FavoritesURLsActivityFragment;
import com.grapsas.android.streamrecorder.misc.FavoritesURLs;


public class FavoritesURLsActivity extends MyActivity implements
        AddFavoriteURL.Interaction,
        DeleteFavoriteURL.Interaction {

    public static final String TYPE_KEY = "type";
    public static final String TYPE_FAV = "fav";
    public static final String TYPE_PRE_EXISTS = "pre_exists";
    public static final String TYPE_DEFAULT = TYPE_FAV;

    public static final int RESULT_CODE = 2;
    public static final String RESULT_URL_KEY = "url";

    public static final String PRE_EXISTS_FILE_NAME = "urls.json";

    private String pType;


    /*
     * Activity Overrides
     */
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_favorites_urls );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        }

        if( this.getType().equals( TYPE_FAV ) ) {
            this.setTitle( R.string.title_activity_favorites_urls );
        }
        else if( this.getType().equals( TYPE_PRE_EXISTS ) ) {
            this.setTitle( R.string.title_activity_preexists_urls );
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if( item.getItemId() == android.R.id.home )
            finish();
        return super.onOptionsItemSelected( item );
    }


    // Tools
    @NonNull
    public String getType() {
        if( this.pType == null ) {
            Bundle args = getIntent().getExtras();
            this.pType = args.getString( TYPE_KEY );
        }
        if( this.pType == null ) {
            this.pType = TYPE_DEFAULT;
        }
        return this.pType;
    }

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
    public void startAddFavoriteURL( ) {
        AddFavoriteURL.newInstance( AddFavoriteURL.TYPE.FAVORITE ).show( getFragmentManager(), "" );
    }

    @Override
    public void saveAndRec( @NonNull String url ) {
        this.save( url );
        this.rec( url );
    }

    @Override
    public void save( @NonNull String url ) {
        AddFavoriteURL.Interaction fragment = this.getFragment( R.id.fragment );
        if( fragment == null )
            return;
        fragment.save( url );
    }

    @Override
    public void rec( @NonNull String url ) {
        this.urlSelected( url );
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
