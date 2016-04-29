package com.example.thesamespace.bleconnecttest;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by thesamespace on 2016/4/28.
 */
public class SettingFragmentContent extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private final static String Characteristic_UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
    private final static String Server_UUID_KEY_DATA = "0000fff0-0000-1000-8000-00805f9b34fb";
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private EditText edt_bleName;
    private boolean readFlage = false;
    private boolean writeFlage = false;
    private boolean getBLEConfigFlage = false;
    private byte[] writebytes;
    private BLEConfig bleConfig = new BLEConfig();
    private Spinner spainer_power;
    private Spinner spainer_frequency;
    private RadioGroup rad_group;
    private RadioButton rad_altBeacon;
    private RadioButton rad_iBeacon;
    private ProgressBar progress_waitbar;

    public SettingFragmentContent(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settingcontent, container, false);
        initView(view);
        initSpinner(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressBar();
        readBLEConfig();
    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress_waitbar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress_waitbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mBluetoothGatt.disconnect();
    }

    private void initView(View view) {
        Button btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        TextView tv_bleName = (TextView) view.findViewById(R.id.tv_bleName);
        TextView tv_bleAddress = (TextView) view.findViewById(R.id.tv_bleAddress);
        tv_bleName.setText(mBluetoothDevice.getName());
        tv_bleAddress.setText("Address:" + mBluetoothDevice.getAddress());

        edt_bleName = (EditText) view.findViewById(R.id.edt_bleName);
        edt_bleName.setText(mBluetoothDevice.getName());

        rad_group = (RadioGroup) view.findViewById(R.id.rad_group);
        rad_group.setOnCheckedChangeListener(this);
        rad_altBeacon = (RadioButton) view.findViewById(R.id.rad_altBeacon);
        rad_iBeacon = (RadioButton) view.findViewById(R.id.rad_iBeacon);

        progress_waitbar = (ProgressBar) view.findViewById(R.id.progress_waitbar);
    }

    private void initSpinner(View view) {
        spainer_frequency = (Spinner) view.findViewById(R.id.spainer_frequency);
        spainer_power = (Spinner) view.findViewById(R.id.spainer_power);
        String[] str_frequency = new String[]{"100ms", "200ms", "500ms", "1000ms"};
        String[] str_power = new String[]{"-23dbm", "-6dbm", "0dbm", "4dbm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, str_frequency);
        ArrayAdapter<String> adapter_power = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, str_power);
        spainer_frequency.setAdapter(adapter);
        spainer_power.setAdapter(adapter_power);
    }

    private void setBLEConfig() {
        showProgressBar();
        bleConfig.setPower(spainer_power.getSelectedItemPosition());
        bleConfig.setFrequency(spainer_frequency.getSelectedItemPosition());
        if (rad_altBeacon.isChecked()) {
            bleConfig.setBeaconType(0);
        } else if (rad_iBeacon.isChecked()) {
            bleConfig.setBeaconType(1);
        }

        int value = bleConfig.getPower();
        value += bleConfig.getBeaconType() << 4;
        value += bleConfig.getFrequency() << 5;
        byte[] bytes = new byte[]{(byte) value};
        writeCharacteristic(bytes);
    }

    private void connectGATT() {
        if (mBluetoothDevice != null) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(getActivity(), false, mGattCallback);
        } else {
            printLog("mBluetoothDevice 为空");
            return;
        }
    }

    private void writeCharacteristic(byte[] bytes) {
        writeFlage = true;
        writebytes = bytes;
        connectGATT();
    }

    private void readCharacteristic() {
        readFlage = true;
        connectGATT();
    }

    private void readBLEConfig() {
        showProgressBar();
        getBLEConfigFlage = true;
        readCharacteristic();
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    printLog("Connecting...");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    printLog("Connected to GATT server");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    printLog("Disconnected to GATT server");
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    printLog("Disconnecting to GATT server");
                    break;
            }
            if (readFlage || writeFlage) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BluetoothGattService bleGattService = gatt.getService(UUID.fromString(Server_UUID_KEY_DATA));
            BluetoothGattCharacteristic gattCharacteristic = bleGattService.getCharacteristic(UUID.fromString(Characteristic_UUID_KEY_DATA));
            if (readFlage) {
                readFlage = false;
                gatt.readCharacteristic(gattCharacteristic);
            }
            if (writeFlage) {
                writeFlage = false;
                gattCharacteristic.setValue(writebytes);
                gatt.writeCharacteristic(gattCharacteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] bytes = characteristic.getValue();
            String str = "";
            for (byte b : bytes) {
                str += b;
            }
            printLog("onCharacteristicChanged:" + str);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                printLog("read fail!  status:" + status);
                return;
            }

            byte[] bytes = characteristic.getValue();
            if (getBLEConfigFlage) {
                getBLEConfigFlage = false;
                getBLEConfig(bytes[0]);
            }
            hideProgressBar();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                printLog("write fail!  status:" + status);
                return;
            }
            hideProgressBar();
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                printLog("read RemoteRssi fail!  status:" + status);
                return;
            }
        }
    };

    private void printLog(final String logStr) {
        Log.d("printLog", logStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                setBLEConfig();
                break;
        }
    }

    private void getBLEConfig(int value) {
        Log.d("test", (value & 0B11) + "");
        bleConfig.setPower(value & 0B11);
        bleConfig.setBeaconType((value & 0B10000) >> 4);
        bleConfig.setFrequency((value & 0B1100000) >> 5);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spainer_power.setSelection(bleConfig.getPower());
                spainer_frequency.setSelection(bleConfig.getFrequency());
                if (bleConfig.getBeaconType() == 1) {
                    rad_iBeacon.setChecked(true);
                } else {
                    rad_altBeacon.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    private class BLEConfig {
        private String bleName;
        private int power;
        private int frequency;
        private int beaconType;

        public String getBleName() {
            return bleName;
        }

        public void setBleName(String bleName) {
            this.bleName = bleName;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public int getBeaconType() {
            return beaconType;
        }

        public void setBeaconType(int beaconType) {
            this.beaconType = beaconType;
        }
    }
}
