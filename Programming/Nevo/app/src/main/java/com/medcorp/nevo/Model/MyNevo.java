package com.medcorp.nevo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaillysu on 15/12/28.
 */
public class MyNevo {
    private String ble_firmware_version;
    private String mcu_firmware_version;
    private String app_version;
    private int battery_level;
    private boolean available_version;
    private List<String> firmwareURLs;
    public MyNevo(String ble_firmware_version,String mcu_firmware_version,String app_version,int battery_level,boolean available_version,List<String> firmwareURLs)
    {
        this.ble_firmware_version = ble_firmware_version;
        this.mcu_firmware_version = mcu_firmware_version;
        this.app_version = app_version;
        this.battery_level = battery_level;
        this.available_version = available_version;
        this.firmwareURLs = firmwareURLs;
    }

    public String getBle_firmware_version() {
        return ble_firmware_version;
    }

    public void setBle_firmware_version(String ble_firmware_version) {
        this.ble_firmware_version = ble_firmware_version;
    }

    public String getMcu_firmware_version() {
        return mcu_firmware_version;
    }

    public void setMcu_firmware_version(String mcu_firmware_version) {
        this.mcu_firmware_version = mcu_firmware_version;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public int getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    public boolean isAvailable_version() {
        return available_version;
    }

    public void setAvailable_version(boolean available_version) {
        this.available_version = available_version;
    }

    public List<String> getFirmwareURLs() {
        return firmwareURLs;
    }

    public void setFirmwareURLs(List<String> firmwareURLs) {
        this.firmwareURLs = firmwareURLs;
    }
}
