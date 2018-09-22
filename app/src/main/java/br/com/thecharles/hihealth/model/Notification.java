package br.com.thecharles.hihealth.model;

import com.google.android.gms.maps.model.LatLng;

public class Notification {

    private String tokenUser;

    private LatLng latLngUser;
    private String nameUser;
    private String messageAlert;


    public Notification() {
    }

    public String getTokenUser() {
        return tokenUser;
    }

    public Notification setTokenUser(String tokenUser) {
        this.tokenUser = tokenUser;
        return this;
    }

    public LatLng getLatLngUser() {
        return latLngUser;
    }

    public Notification setLatLngUser(LatLng latLngUser) {
        this.latLngUser = latLngUser;
        return this;
    }

    public String getNameUser() {
        return nameUser;
    }

    public Notification setNameUser(String nameUser) {
        this.nameUser = nameUser;
        return this;
    }

    public String getMessageAlert() {
        return messageAlert;
    }

    public Notification setMessageAlert(String messageAlert) {
        this.messageAlert = messageAlert;
        return this;
    }
}
