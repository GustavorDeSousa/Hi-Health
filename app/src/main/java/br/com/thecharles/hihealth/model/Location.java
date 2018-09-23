package br.com.thecharles.hihealth.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.com.thecharles.hihealth.config.SettingsFirebase;

public class Location implements Serializable{

    private String idUser;
    private String address;
    private LatLng latLng;

    public void save(String uId) {
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference firebaseRefDebug = database.child("debug");
        DatabaseReference sendor = firebaseRefDebug.child("users").child(uId).child("location");

        sendor.setValue(this);
    }

    public Location() {
    }

    public String getIdUser() {
        return idUser;
    }

    public Location setIdUser(String idUser) {
        this.idUser = idUser;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Location setAddress(String address) {
        this.address = address;
        return this;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Location setLatLng(LatLng latLng) {
        this.latLng = latLng;
        return this;
    }
}
