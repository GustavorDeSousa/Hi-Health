package br.com.thecharles.hihealth.model;

public class User {

    private String name;
    private String email;
    private String phone;
    private String birthDay;
    private String genre;
    private String document;
    private String address;
    private String height;
    private String  weight;
    private String blood;
    private String obs;

    public User() {
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

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
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
