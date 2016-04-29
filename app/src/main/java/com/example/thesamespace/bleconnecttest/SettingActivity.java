package com.example.thesamespace.bleconnecttest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class SettingActivity extends Activity implements AdapterView.OnItemClickListener {
    private final static String UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
    private ListView lv_bleCharacteristicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        lv_bleCharacteristicList = (ListView) findViewById(R.id.lv_blecharacteristicList);
        lv_bleCharacteristicList.setOnItemClickListener(SettingActivity.this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
