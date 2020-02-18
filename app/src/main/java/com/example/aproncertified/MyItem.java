package com.example.aproncertified;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class MyItem implements ClusterItem, Serializable {
    private LatLng mPosition;
    private String mTitle;
    private FormDetails formDetails;
    String keys;

    public MyItem(double lat, double lng, String title, FormDetails formDetails, String keys) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        this.formDetails = formDetails;
        this.keys = keys;
    }
    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public FormDetails getFormDetails() {
        return formDetails;
    }
    public String getKey() {
        return keys;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return null;
    }

}
