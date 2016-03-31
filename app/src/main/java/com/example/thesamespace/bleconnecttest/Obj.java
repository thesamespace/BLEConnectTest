package com.example.thesamespace.bleconnecttest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import java.io.Serializable;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class Obj implements Serializable {
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
    }
}
