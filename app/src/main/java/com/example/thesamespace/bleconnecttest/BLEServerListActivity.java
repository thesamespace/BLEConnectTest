package com.example.thesamespace.bleconnecttest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class BLEServerListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static String Characteristic_UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
    private final static String Server_UUID_KEY_DATA = "0000fff0-0000-1000-8000-00805f9b34fb";

    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic gattCharacteristic;
    private TextView tv_bleName;
    private TextView tv_bleInfo;
    private TextView tv_bleRssi;
    private TextView tv_log;
    private EditText edt_command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleserverlist);
        init();
        Bundle bundle = getIntent().getExtras();
        mDevice = (BluetoothDevice) bundle.get("mDevice");
        connectGATT();
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
        tv_log = (TextView) findViewById(R.id.tv_log);
        edt_command = (EditText) findViewById(R.id.edt_command);

        Button btn_write = (Button) findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);

        Button btn_read = (Button) findViewById(R.id.btn_read);
        btn_read.setOnClickListener(this);

        Button btn_clearLog = (Button) findViewById(R.id.btn_clearLog);
        btn_clearLog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            try {
                                Thread.sleep(1000);
                                if (gattCharacteristic != null) {
                                    PrintLog("获取gattCharacteristic成功");
                                    byte[] bytes = {Byte.parseByte(edt_command.getText().toString())};
                                    gattCharacteristic.setValue(bytes);
                                    mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                                    break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
            case R.id.btn_read:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            try {
                                Thread.sleep(1000);
                                if (gattCharacteristic != null) {
                                    PrintLog("获取gattCharacteristic成功");
                                    mBluetoothGatt.readCharacteristic(gattCharacteristic);
                                    break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
            case R.id.btn_clearLog:
                tv_log.setText("Log:");
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
            PrintLog(status + "," + newState);
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            PrintLog("发现服务");
            BluetoothGattService bleGattService = gatt.getService(UUID.fromString(Server_UUID_KEY_DATA));
            gattCharacteristic = bleGattService.getCharacteristic(UUID.fromString(Characteristic_UUID_KEY_DATA));
//            List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
//            List<ListItem> list = new ArrayList<>();
//            int i = 0;
//            for (BluetoothGattService bleGattService : bluetoothGattServices) {
//                ListItem listItem = new ListItem();
//                listItem.setServerName("Server" + i);
//                i++;
//                listItem.setServerUUID(bleGattService.getUuid().toString());
//                list.add(listItem);
//                List<BluetoothGattCharacteristic> gattCharacteristics = bleGattService.getCharacteristics();
//                for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                    if (gattCharacteristic.getUuid().toString().equals(Characteristic_UUID_KEY_DATA)) {
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
//            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] bytes = characteristic.getValue();
            String str = "";
            for (byte b : bytes) {
                str += b;
            }
            PrintLog("onCharacteristicChanged:" + str);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] bytes = characteristic.getValue();
            String str = "";
            for (byte b : bytes) {
                str += b;
            }
            PrintLog("onCharacteristicRead: " + str + " status:" + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            byte[] bytes = characteristic.getValue();
            String str = "";
            for (byte b : bytes) {
                str += b;
            }
            PrintLog("onCharacteristicWrite: " + str + " status:" + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            setBLERssi(rssi);
        }
    };

    private void connectGATT() {
        if (mDevice != null) {
            mBluetoothGatt = mDevice.connectGatt(BLEServerListActivity.this, false, mGattCallback);
            setBLEInfo();
            PrintLog("获取mDevice成功");
        } else {
            PrintLog("获取mDevice失败");
            return;
        }
    }

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

    private void PrintLog(final String logStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_log.append("\n" + logStr);
            }
        });
    }


}
