package com.example.thesamespace.bleconnecttest;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class BLEServerListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static String UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private TextView tv_bleName;
    private TextView tv_bleInfo;
    private TextView tv_bleRssi;
    private EditText edt_command;
    private Button btn_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleserverlist);
        init();
        Bundle bundle = getIntent().getExtras();
        mDevice = (BluetoothDevice) bundle.get("mDevice");
        if (mDevice != null) {
            mBluetoothGatt = mDevice.connectGatt(BLEServerListActivity.this, false, mGattCallback);
            setBLEInfo();
            System.out.println("获取mDevice成功!");
        } else {
            System.out.println("获取mDevice失败!");
            tv_bleName.setText("get mDevice error");
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothGatt.disconnect();
    }

    private void init() {
        tv_bleName = (TextView) findViewById(R.id.tv_bleName);
        tv_bleInfo = (TextView) findViewById(R.id.tv_bleInfo);
        tv_bleRssi = (TextView) findViewById(R.id.tv_bleRssi);
        edt_command = (EditText) findViewById(R.id.edt_command);

        btn_write = (Button) findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            System.out.println("发现服务");
            List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
            List<ListItem> list = new ArrayList<>();
            int i = 0;
            for (BluetoothGattService bleGattService : bluetoothGattServices) {
                ListItem listItem = new ListItem();
                listItem.setServerName("Server" + i);
                i++;
                listItem.setServerUUID(bleGattService.getUuid().toString());
                list.add(listItem);
//                List<BluetoothGattCharacteristic> gattCharacteristics = bleGattService.getCharacteristics();
//                for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                    if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    gatt.readCharacteristic(gattCharacteristic);
//                                    Thread.sleep(500);
//                                    byte[] bytes = {9, 2, 3, 4, 5, 6, 10};
//                                    gattCharacteristic.setValue(bytes);
//                                    gatt.writeCharacteristic(gattCharacteristic);
//                                    Thread.sleep(500);
//                                    gatt.readCharacteristic(gattCharacteristic);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
//                    }
//                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            setBLERssi(rssi);
        }
    };

    private void setBLEInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_bleName.setText(mDevice.getName());
                tv_bleInfo.setText("Address:" + mDevice.getAddress());
                tv_bleInfo.append("\nUUID:" + mDevice.getUuids());
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        mBluetoothGatt.readRemoteRssi();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setBLERssi(final int rssi) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_bleRssi.setText("Rssi:" + rssi);
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ShowMsg(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                tv_bleList.append("\n" + str);
            }
        });
    }


}
