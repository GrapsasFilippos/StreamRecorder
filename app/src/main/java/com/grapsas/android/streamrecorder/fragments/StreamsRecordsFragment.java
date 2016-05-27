package com.grapsas.android.streamrecorder.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.grapsas.android.streamrecorder.R;
import com.grapsas.android.streamrecorder.adapters.RecordsListAdapter;
import com.grapsas.android.streamrecorder.interfaces.OnDataChanged;
import com.grapsas.android.streamrecorder.misc.FileListItem;
import com.grapsas.android.streamrecorder.misc.IO;


public class StreamsRecordsFragment extends Fragment implements OnDataChanged {

    private OnFragmentInteractionListener mListener;

    private ListView listView;
    private RecordsListAdapter adapter;


    public static StreamsRecordsFragment newInstance( String param1, String param2 ) {
        StreamsRecordsFragment fragment = new StreamsRecordsFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    public StreamsRecordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View rootView = inflater.inflate( R.layout.fragment_mic_records, container, false );

        this.adapter = new RecordsListAdapter( getActivity(), new FileListItem[ 0 ] );
        this.listView = ( ListView ) rootView.findViewById( R.id.listView );
        this.listView.setAdapter( adapter );
        this.listView = (ListView) rootView.findViewById( R.id.listView );
        this.listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {
                if( mListener != null )
                    mListener.startPlaying( ( FileListItem ) adapter.getItem( position ) );
            }
        } );

        return rootView;
    }

    @Override
    public void onAttach( Activity activity) {
        super.onAttach( activity );
        if( activity instanceof OnFragmentInteractionListener ) {
            mListener = ( OnFragmentInteractionListener ) activity;
        } else {
            throw new RuntimeException( activity.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.refreshListView();
    }

    private void refreshListView() {
        if( this.mListener == null )
            return;
        this.adapter.refreshData( mListener.getRecords( IO.STREAM_RECORDS ) );
        this.adapter.notifyDataSetChanged();
    }


    /*
     *
     */
    @Override
    public void dataChanged() {
        this.refreshListView();
    }

    /*
     *
     */
    public interface OnFragmentInteractionListener {

        FileListItem[] getRecords( int type );
        void startPlaying( FileListItem fileListItem );

    }
}
