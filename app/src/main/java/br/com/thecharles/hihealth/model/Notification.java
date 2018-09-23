package br.com.thecharles.hihealth.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Notification implements Serializable{

    private String tokenUser;

    private LatLng latLngUser;
    private String nameUser;
    private String messageAlert;
    private String idUser;

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

    public String getIdUser() {
        return idUser;
    }

    public Notification setIdUser(String idUser) {
        this.idUser = idUser;
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
