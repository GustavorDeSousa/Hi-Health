package br.com.thecharles.hihealth.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

import br.com.thecharles.hihealth.config.SettingsFirebase;

public class User implements Serializable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String photo;
    private boolean alert;
    private String birthDay;
    private String genre;
    private String document;
    private String address;
    private String height;
    private String weight;
    private String blood;
    private String obs;

    public User() {
    }

    public void save(String uId) {
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference firebaseRefDebug = database.child("debug");
        DatabaseReference user = firebaseRefDebug.child("users").child(uId).child("registered");

        user.setValue(this);
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public User setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public boolean isAlert() {
        return alert;
    }

    public User setAlert(boolean alert) {
        this.alert = alert;
        return this;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public User setBirthDay(String birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public User setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public String getDocument() {
        return document;
    }

    public User setDocument(String document) {
        this.document = document;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public User setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public User setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getBlood() {
        return blood;
    }

    public User setBlood(String blood) {
        this.blood = blood;
        return this;
    }

    public String getObs() {
        return obs;
    }

    public User setObs(String obs) {
        this.obs = obs;
        return this;
    }
}
