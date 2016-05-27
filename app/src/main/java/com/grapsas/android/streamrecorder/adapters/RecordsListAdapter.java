package com.grapsas.android.streamrecorder.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.activities.MainActivity;
import com.grapsas.android.streamrecorder.misc.FileListItem;
import com.grapsas.android.streamrecorder.misc.MyLog;

import java.lang.ref.WeakReference;


public class RecordsListAdapter extends BaseAdapter {

    private WeakReference< Activity > weakActivity;
    private FileListItem[] mFileListItems;

    public RecordsListAdapter( @Nullable Activity activity, @NonNull FileListItem[]fileListItem ) {
        if( activity != null ) {
            this.weakActivity = new WeakReference<>( activity );
        }
        this.mFileListItems = fileListItem;
    }

    @Nullable
    private Activity getActivity() {
        if( this.weakActivity == null ) {
            return null;
        }
        return this.weakActivity.get();
    }

    public void refreshData( @NonNull FileListItem[] fileListItems ) {
        this.mFileListItems = fileListItems;
    }

    @Override
    public int getCount() {
        return this.mFileListItems.length;
    }

    @Override
    public Object getItem( int position ) {
        return this.mFileListItems[ position ];
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
            convertView = inflater.inflate( R.layout.list_item_record, parent, false );
        }

        FileListItem fileListItem = (FileListItem) getItem( position );

        TextView fileName = (TextView) convertView.findViewById( R.id.fileName );
        TextView modified = (TextView) convertView.findViewById( R.id.modifiedV );
        TextView size = (TextView) convertView.findViewById( R.id.sizeV );
        ImageButton imageButton = (ImageButton) convertView.findViewById( R.id.imageButton );

        fileName.setText( fileListItem.getName() );
        modified.setText( fileListItem.getModifiedHuman() );
        size.setText( fileListItem.getSizeHuman( parent.getContext() ) );
        imageButton.setTag( fileListItem );
        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                MainActivity mActivity = ( (MainActivity) getActivity() );
                if( mActivity != null ) {
                    mActivity.showPlayingView( v );
                }
            }
        } );

        return convertView;
    }
}
