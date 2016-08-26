package com.umnix.musmarket.model;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private int latitude;
    private int longitude;

    public Location() {
    }

    public Location(int latitude, int longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public LatLng getCoordinates() {
        return new LatLng(convertCoordinate(latitude), convertCoordinate(longitude));
    }

    private double convertCoordinate(int value) {
        return value / 1_000_000.0;
    }


    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
