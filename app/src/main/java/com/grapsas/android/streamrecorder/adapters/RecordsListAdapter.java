package com.grapsas.android.streamrecorder.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.misc.FileListItem;


public class RecordsListAdapter extends BaseAdapter {

    private FileListItem[] mFileListItems;

    public RecordsListAdapter( @NonNull FileListItem[] fileListItem ) {
        this.mFileListItems = fileListItem;
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

        fileName.setText( fileListItem.getName() );
        modified.setText( fileListItem.getModifiedHuman() );
        size.setText( fileListItem.getSizeHuman( parent.getContext() ) );

        return convertView;
    }
}
