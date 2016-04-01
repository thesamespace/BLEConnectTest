package com.example.thesamespace.bleconnecttest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView lv_bleList;
    List<ListItem> list = new ArrayList<>();
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Boolean isExisted = false;
            for (int i=0;i<list.size();i++){
                if (list.get(i).getName().equals(device.getName())){
                    isExisted = true;
                    list.get(i).setRssi(rssi);
                    break;
                }
            }
            if (!isExisted) {
                list.add(new ListItem(device.getName(),rssi));
            }
            BLEAdapter bledapter = new BLEAdapter(MainActivity.this, list);
            lv_bleList.setAdapter(bledapter);
        }

        @Override
        protected void ShowMsg(final String str) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Button btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        Button btn_stopScan = (Button) findViewById(R.id.btn_stopScan);
        btn_stopScan.setOnClickListener(this);

        lv_bleList = (ListView) findViewById(R.id.lv_list);
        lv_bleList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                myLeScaner.startLeScan();
                break;
            case R.id.btn_stopScan:
                myLeScaner.stopLeScan();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, BLEServerListActivity.class);
        intent.putExtra("mDevice", myLeScaner.bleList.get(position).mDevice);
        startActivity(intent);
    }
}
