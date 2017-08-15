package com.venky97vp.android.googlemapsdemo;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by venky on 14-08-17.
 */

public class ResultApi implements Comparable<ResultApi>{
    private ScanResult scanResult;
    private double distance;
    private LatLng latLng;

    public ResultApi(ScanResult scanResult, double distance) {
        this.scanResult = scanResult;
        this.distance = distance;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public int compareTo(@NonNull ResultApi o) {
        if(this.getDistance()==o.getDistance()){
            return 0;
        }else if(this.getDistance()< o.getDistance()){
            return -1;
        }else{
            return 1;
        }
    }
}
