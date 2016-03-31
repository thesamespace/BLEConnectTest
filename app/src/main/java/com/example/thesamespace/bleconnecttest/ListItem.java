package com.example.thesamespace.bleconnecttest;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class ListItem {
    private String name;
    private int rssi;

    public ListItem(String name, int rssi) {
        this.name = name;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
