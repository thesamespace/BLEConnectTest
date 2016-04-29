package com.example.thesamespace.bleconnecttest;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView lv_bleList;
    private Button btn_scan;
    private List<ListItem> list = new ArrayList<>();
    private int[] record = new int[4];
    private BLEAdapter bledapter;
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName().equals("MYBLE")) {
//                String scanRecordStr = "";
//                for (byte b : scanRecord) {
//                    scanRecordStr += String.format("%X,", b);
//                }
                switch (scanRecord[24]) {
                    case 0:
                        record[0]++;
                        break;
                    case 1:
                        record[1]++;
                        break;
                    case 2:
                        record[2]++;
                        break;
                    case 3:
                        record[3]++;
                        break;
                }
                String temp = "";
                temp += "0:" + record[0] + "\n";
                temp += "1:" + record[1] + "\n";
                temp += "2:" + record[2] + "\n";
                temp += "3:" + record[3] + "\n";
                ShowMsg(temp);
            }
            Boolean isExisted = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(device.getName())) {
                    isExisted = true;
                    list.get(i).setRssi(rssi);
                    break;
                }
            }
            if (!isExisted) {
                list.add(new ListItem(device.getName(), rssi));
            }
            if (bledapter != null)
                bledapter.notifyDataSetChanged();
        }

        @Override
        protected void ShowMsg(final String logstr) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    tv_log.setText(logstr);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        lv_bleList = (ListView) findViewById(R.id.lv_list);
        lv_bleList.setOnItemClickListener(this);
        bledapter = new BLEAdapter(MainActivity.this, list);
        lv_bleList.setAdapter(bledapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                if (btn_scan.getText().equals("Scan")) {
                    btn_scan.setText("Stop");
                    myLeScaner.startLeScan();
                } else {
                    btn_scan.setText("Scan");
                    myLeScaner.stopLeScan();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fly_settingContent, new SettingFragmentContent(myLeScaner.bleList.get(position).mDevice));
        fragmentTransaction.commit();
    }
}
