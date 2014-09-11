/*
 * Copyright (C) 2014 emmasuzuki <emma11suzuki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.emmasuzuki.quickreturnlistview.app;
import android.app.Fragment;
import android.os.Bundle;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> data = new ArrayList<String>(NUM_OF_DATA);

        for(int i = 0; i < NUM_OF_DATA; i++) {
            data.add(getString(R.string.grid));
        }

        mQuickReturnListView.setAdapter(new DataAdapter(getActivity(), R.layout.grid, data));
    }
}
