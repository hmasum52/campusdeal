package com.codervai.campusdeal.model;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

@Parcel
public class MyLocation {
    private double lat;
    private double lng;
    private String fullAddress;

    public MyLocation() {
    }

    public MyLocation(LatLng latLng, String fullAddress) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
        this.fullAddress = fullAddress;
    }

    public MyLocation(double lat, double lng, String fullAddress) {
        this.lat = lat;
        this.lng = lng;
        this.fullAddress = fullAddress;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
