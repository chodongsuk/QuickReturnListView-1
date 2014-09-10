package com.emmasuzuki.quickreturnlistview.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmasuzuki.quickreturnlistview.R;
import com.emmasuzuki.quickreturnlistview.view.QuickReturnListView;
import com.emmasuzuki.quickreturnlistview.widget.DataAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emmasuzuki on 7/23/14.
 */
public class QuickReturnGridFragment extends Fragment {

    private static final int NUM_OF_DATA = 500;

    private QuickReturnListView mQuickReturnListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quick_return_grid, container, false);

        mQuickReturnListView = (QuickReturnListView) view.findViewById(R.id.quick_return_gridview);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> data = new ArrayList<String>(NUM_OF_DATA);

        for(int i = 0; i < NUM_OF_DATA; i++) {
            data.add(getString(R.string.grid));
        }

        mQuickReturnListView.setAdapter(new DataAdapter(getActivity(), R.layout.grid, data));
    }
}
