package com.example.donorapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {

    @Expose
    @SerializedName("id_user") String id_user;
    @Expose
    @SerializedName("username") String username;
    @Expose
    @SerializedName("email") String email;
    @Expose
    @SerializedName("gol_darah") String gol_darah;
    @Expose
    @SerializedName("rhesus") String rhesus;
    @Expose
    @SerializedName("foto") String foto;
    @Expose
    @SerializedName("lat") String lat;
    @Expose
    @SerializedName("lng") String lng;
    @Expose
    @SerializedName("telepon") String telepon;
    @Expose
    @SerializedName("alamat") String alamat;

    public UserModel(){};

    public UserModel(String id_user, String username, String email) {
        this.id_user = id_user;
        this.username = username;
        this.email = email;
    }

    public String getId_user() { return id_user; }

    public void setId_user(String id_user) { this.id_user = id_user; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGol_darah() { return gol_darah; }

    public void setGol_darah(String gol_darah) { this.gol_darah = gol_darah; }

    public String getRhesus() { return rhesus; }

    public void setRhesus(String rhesus) { this.rhesus = rhesus; }

    public String getFoto() { return foto; }

    public void setFoto(String foto) { this.foto = foto; }

    public String getLat() { return lat; }

    public void setLat(String lat) { this.lat = lat; }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTelepon() { return telepon; }

    public void setTelepon(String telepon) { this.telepon = telepon; }

    public String getAlamat() { return alamat; }

    public void setAlamat(String alamat) { this.alamat = alamat; }
}
