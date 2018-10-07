package br.com.thecharles.hihealth.model;

import java.io.Serializable;

public class Message implements Serializable {

    private String idSender;
    private String message;
    private String file;

    private String token;
    private String nameSender;

    public Message() {
    }



    public String getIdSender() {
        return idSender;
    }

    public Message setIdSender(String idSender) {
        this.idSender = idSender;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getFile() {
        return file;
    }

    public Message setFile(String file) {
        this.file = file;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Message setToken(String token) {
        this.token = token;
        return this;
    }

    public String getNameSender() {
        return nameSender;
    }

    public Message setNameSender(String nameSender) {
        this.nameSender = nameSender;
        return this;
    }
}
