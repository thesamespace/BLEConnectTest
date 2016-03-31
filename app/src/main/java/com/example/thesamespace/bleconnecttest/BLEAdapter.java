package com.example.thesamespace.bleconnecttest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class BLEAdapter extends BaseAdapter {
    private List<ListItem> mData;
    private Context mContext;

    public BLEAdapter(Context context, List<ListItem> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.blelist_item, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_rssi = (TextView) view.findViewById(R.id.tv_rssi);
        tv_name.setText(mData.get(position).getName());
        tv_rssi.setText("rssi:"+mData.get(position).getRssi());
        return view;
    }
}
