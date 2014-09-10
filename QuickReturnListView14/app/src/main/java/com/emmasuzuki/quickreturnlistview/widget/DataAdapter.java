package com.emmasuzuki.quickreturnlistview.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.emmasuzuki.quickreturnlistview.R;

import java.util.List;

/**
 * Created by emmasuzuki on 7/22/14.
 */
public class DataAdapter extends ArrayAdapter<String> {

    public DataAdapter(Context context, int resource, List<String> data) {
        super(context, resource, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        if (position % 2 == 0) {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.light_purple));
        }

        return convertView;
    }
}
