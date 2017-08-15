package com.venky97vp.android.googlemapsdemo;

import android.net.wifi.ScanResult;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by venky on 13-08-17.
 */

public class WiFiApi {
    private LatLng latLng;
    private ScanResult scanResult;
    private String name;

    public WiFiApi(String name,LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
