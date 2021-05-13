package com.jobesk.nourv.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by phamngocthanh on 7/19/17.
 */

@IgnoreExtraProperties
public class BluetoothDevice implements Serializable {

    public String id ="";
    public String userId ="";
    public String deviceAddress ="";
    public String deviceTitle="";
    public String timePaired="";
    public String comment="";
    public String lastUpdated="";
    public String type="";
    public String battery="";
    public String latitude="";
    public String longitude="";
    public String rssi="";

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceTitle() {
        return deviceTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDeviceTitle(String deviceTitle) {
        this.deviceTitle = deviceTitle;
    }
    public String getTimePaired() {
        return timePaired;
    }

    public void setTimePaired(String timePaired) {
        this.timePaired = timePaired;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("deviceAddress", deviceAddress);
        result.put("deviceTitle",deviceTitle);
        result.put("timePaired",timePaired);
        result.put("comment",comment);
        result.put("lastUpdated",lastUpdated);
        result.put("type",type);
        result.put("id",id);
        result.put("userId",userId);
        result.put("battery",battery);
        result.put("latitude",latitude);
        result.put("longitude",longitude);
        result.put("rssi",rssi);

        return result;
    }

    @Override
    public String toString() {
        return "BluetoothDevice{" +
                "userId='" + userId + '\'' +
                "id='" + id + '\'' +
                "deviceAddress='" + deviceAddress + '\'' +
                ", deviceTitle='" + deviceTitle + '\'' +
                ", timePaired='" + timePaired + '\'' +
                ", comment='" + comment + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", type='" + type + '\'' +
                ", rssi='" + rssi + '\'' +
                '}';
    }
}
