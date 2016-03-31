package com.example.thesamespace.bleconnecttest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static String UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
    private Button btn_scan;
    private Button btn_stopScan;
    private Button btn_clear;
    private Button btn_connect;
    private Button btn_disConnect;
    private TextView tv_bleList;
    private ListView lv_test;
    private BluetoothGatt mBluetoothGatt;
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            ShowMsg(device.getName() + " " + rssi);
            MyAdapter myAdapter = new MyAdapter(MainActivity.this, getData());
            lv_test.setAdapter(myAdapter);
        }

        @Override
        protected void ShowMsg(final String str) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_bleList.append("\n" + str);
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
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        btn_stopScan = (Button) findViewById(R.id.btn_stopScan);
        btn_stopScan.setOnClickListener(this);

        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);

        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(this);

        btn_disConnect = (Button) findViewById(R.id.btn_disConnect);
        btn_disConnect.setOnClickListener(this);
        tv_bleList = (TextView) findViewById(R.id.tv_bleList);
        lv_test = (ListView) findViewById(R.id.lv_list);
        lv_test.setOnItemClickListener(this);
    }

    private List<ListItem> getData() {
        List<ListItem> list = new ArrayList<>();
        for (int i = 0; i < myLeScaner.bleList.size(); i++) {
            String name = myLeScaner.bleList.get(i).mDevice.getName();
            int rssi = myLeScaner.bleList.get(i).getLastRssi();
            list.add(new ListItem(name, rssi));
        }
        return list;
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
            case R.id.btn_clear:
                tv_bleList.setText("BLE Info...");
                break;
            case R.id.btn_connect:
                mBluetoothGatt = myLeScaner.bleList.get(0).mDevice.connectGatt(this, false, mGattCallback);
                mBluetoothGatt.connect();
                break;
            case R.id.btn_disConnect:
                mBluetoothGatt.disconnect();
                break;
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            ShowMsg("onConnectionStateChange");
            mBluetoothGatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            ShowMsg("onServicesDiscovered");
            List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
            ShowMsg("bluetoothGattServices Size: " + bluetoothGattServices.size());
            for (BluetoothGattService bleGattService : bluetoothGattServices) {
                ShowMsg("Service UUID: " + bleGattService.getUuid().toString());

                List<BluetoothGattCharacteristic> gattCharacteristics = bleGattService.getCharacteristics();
                ShowMsg("gattCharacteristics Size: " + gattCharacteristics.size());
                for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                    if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mBluetoothGatt.readCharacteristic(gattCharacteristic);
                                    Thread.sleep(500);
                                    byte[] bytes = {9, 2, 3, 4, 5, 6, 10};
                                    gattCharacteristic.setValue(bytes);
                                    mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                                    Thread.sleep(500);
                                    mBluetoothGatt.readCharacteristic(gattCharacteristic);
                                    ShowMsg("1111111111111111111111111111111111111111111111111111111 ");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    ShowMsg("Characteristic UUID: " + gattCharacteristic.getUuid());
                    ShowMsg("Permission: " + gattCharacteristic.getPermissions());
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            ShowMsg("onCharacteristicRead " + status + " length:" + characteristic.getValue().length);
            String readStr = "";
            for (byte b : characteristic.getValue()) {
                System.out.println("characteristic" + b);
                readStr += b;
            }
            ShowMsg("Read:" + readStr);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            ShowMsg("onCharacteristicWrite" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            ShowMsg("onCharacteristicChanged");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
            ShowMsg("onDescriptorRead" + status);
            for (byte b : descriptor.getValue()) {
                System.out.println(b);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            ShowMsg("onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            ShowMsg("onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            ShowMsg("onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            ShowMsg("onMtuChanged");
        }
    };

    private void ShowMsg(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_bleList.append("\n" + str);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
