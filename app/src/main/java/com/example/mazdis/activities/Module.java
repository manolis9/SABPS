package com.example.mazdis.activities;

import com.google.android.gms.maps.model.LatLng;

public class Module {
    private String title;
    private double rate;
    private LatLng latLng;
    private String address;

    public Module(String title, String address, double rate) {
        this.title = title;
        this.address = address;
        this.rate = rate;

    }


    public String getTitle() {
        return title;
    }

    public String getAddress() { return address; }

    public double getRate(){
        return rate;
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public void setLatLng(LatLng latLng){
        this.latLng = latLng;
    }


//    public void setTitle(String title) {
//        this.title = title;
//    }

//    public void setPrice(double price){
//        this.price = price;
//    }


}
