package com.venky97vp.android.googlemapsdemo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by venky on 11-08-17.
 */

public class Position {
    private String title;
    private String id;
    private LatLng position;
    private String description;
    private int imageId;
    private String distance;
    private String duration;

    public Position(String title, LatLng position) {
        this.title = title;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setPosition(double lat, double lng) {
        position = new LatLng(lat, lng);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
