package br.com.thecharles.hihealth.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.com.thecharles.hihealth.config.SettingsFirebase;

public class Sensor implements Serializable {

    private String heartRate;
    private String heartRateMax;
    private String heartRateMin;
    private String stepCount;


    public void save(String uId) {
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference firebaseRefDebug = database.child("debug");
        DatabaseReference sendor = firebaseRefDebug.child("users").child(uId).child("sensor");

        sendor.setValue(this);
    }


    public Sensor() {
    }

    public Sensor(String heartRate, String heartRateMax, String heartRateMin, String stepCount) {
        this.heartRate = heartRate;
        this.heartRateMax = heartRateMax;
        this.heartRateMin = heartRateMin;
        this.stepCount = stepCount;
    }

    public Sensor(String heartRate, String stepCount) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
    }

    public String getHeartRateMax() {
        return heartRateMax;
    }

    public Sensor setHeartRateMax(String heartRateMax) {
        this.heartRateMax = heartRateMax;
        return this;
    }

    public String getHeartRateMin() {
        return heartRateMin;
    }

    public Sensor setHeartRateMin(String heartRateMin) {
        this.heartRateMin = heartRateMin;
        return this;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public Sensor setHeartRate(String heartRate) {
        this.heartRate = heartRate;
        return this;
    }

    public String getStepCount() {
        return stepCount;
    }

    public Sensor setStepCount(String stepCount) {
        this.stepCount = stepCount;
        return this;
    }
}
