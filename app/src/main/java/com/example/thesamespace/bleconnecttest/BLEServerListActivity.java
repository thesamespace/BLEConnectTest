package com.example.thesamespace.bleconnecttest;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class BLEServerListActivity extends Activity implements AdapterView.OnItemClickListener {
    private final static String UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
    private ListView lv_bleServerList;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private List<ListItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleserverlist);
        init();
        Bundle bundle = getIntent().getExtras();
        mDevice = (BluetoothDevice) bundle.get("mDevice");
        if (mDevice != null) {
            mBluetoothGatt = mDevice.connectGatt(BLEServerListActivity.this, false, mGattCallback);
            System.out.println("获取mDevice成功!");
        } else {
            System.out.println("获取mDevice失败!");
            return;
        }
    }

    private void init() {
        lv_bleServerList = (ListView) findViewById(R.id.lv_bleServerList);
        lv_bleServerList.setOnItemClickListener(this);
    }

    private List<ListItem> getData() {
        List<ListItem> list = new ArrayList<>();
        ListItem listItem = new ListItem();
        listItem.setServerUUID("1234");
        listItem.setServerName("abcd");
        list.add(listItem);
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void ShowServers(final List<ListItem> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ServerAdapter serverAdapter = new ServerAdapter(BLEServerListActivity.this, list);
                lv_bleServerList.setAdapter(serverAdapter);
            }
        });

    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            gatt.discoverServices();
//            mBluetoothGatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            System.out.println("发现服务");
            ShowMsg("onServicesDiscovered");
            List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
            ShowMsg("bluetoothGattServices Size: " + bluetoothGattServices.size());
            for (BluetoothGattService bleGattService : bluetoothGattServices) {
//                ShowMsg("Service UUID: " + bleGattService.getUuid().toString());
                ListItem listItem = new ListItem();
                listItem.setServerName(bleGattService.toString());
                listItem.setServerUUID(bleGattService.getUuid().toString());
                list.add(listItem);
//                List<BluetoothGattCharacteristic> gattCharacteristics = bleGattService.getCharacteristics();
//                ShowMsg("gattCharacteristics Size: " + gattCharacteristics.size());
//                for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//
//                    if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    mBluetoothGatt.readCharacteristic(gattCharacteristic);
//                                    Thread.sleep(500);
//                                    byte[] bytes = {9, 2, 3, 4, 5, 6, 10};
//                                    gattCharacteristic.setValue(bytes);
//                                    mBluetoothGatt.writeCharacteristic(gattCharacteristic);
//                                    Thread.sleep(500);
//                                    mBluetoothGatt.readCharacteristic(gattCharacteristic);
//                                    ShowMsg("1111111111111111111111111111111111111111111111111111111 ");
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
//                    }
//
//                    ShowMsg("Characteristic UUID: " + gattCharacteristic.getUuid());
//                    ShowMsg("Permission: " + gattCharacteristic.getPermissions());
//                }
            }
            ShowServers(list);
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
//                tv_bleList.append("\n" + str);
            }
        });
    }

}
