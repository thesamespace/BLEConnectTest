package com.example.thesamespace.bleconnecttest;

/**
 * Created by thesamespace on 2016/3/31.
 */
public class ListItem {
    private String blename;
    private String serverName;
    private int rssi;
    private String serverUUID;


    public ListItem(String name, int rssi) {
        this.blename = name;
        this.rssi = rssi;
    }

    public ListItem() {
    }

    public String getName() {
        return blename;
    }

    private void setName(String name) {
        this.blename = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerUUID() {
        return serverUUID;
    }

    public void setServerUUID(String serverUUID) {
        this.serverUUID = serverUUID;
    }
}
