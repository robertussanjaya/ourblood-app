package com.example.donorapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestModel {

    @Expose
    @SerializedName("id_request") String id_request;
    @Expose
    @SerializedName("id_user") String id_user;
    @Expose
    @SerializedName("nama_pasien") String nama_pasien;
    @Expose
    @SerializedName("no_hp") String no_hp;
    @Expose
    @SerializedName("lokasi") String lokasi;
    @Expose
    @SerializedName("gol_dar") String gol_dar;
    @Expose
    @SerializedName("rhesus") String rhesus;
    @Expose
    @SerializedName("foto") String Foto;
    @Expose
    @SerializedName("keterangan") String keterangan;
    @Expose
    @SerializedName("timestamp") String timestamp;
    @Expose
    @SerializedName("lat") String lat;
    @Expose
    @SerializedName("lng") String lng;
    @Expose
    @SerializedName("jumlah") String jumlah;

    public RequestModel(){}

    public String getId_user() { return id_user; }

    public void setId_user(String id_user) { this.id_user = id_user; }

    public String getLat() { return lat; }

    public void setLat(String lat) { this.lat = lat; }

    public String getLng() { return lng; }

    public void setLng(String lng) { this.lng = lng; }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public String getId_request() {
        return id_request;
    }

    public void setId_request(String id_request) {
        this.id_request = id_request;
    }

    public String getNama_pasien() {
        return nama_pasien;
    }

    public void setNama_pasien(String nama_pasien) {
        this.nama_pasien = nama_pasien;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getGol_dar() {
        return gol_dar;
    }

    public void setGol_dar(String gol_dar) {
        this.gol_dar = gol_dar;
    }

    public String getRhesus() {
        return rhesus;
    }

    public void setRhesus(String rhesus) {
        this.rhesus = rhesus;
    }

    public String getJumlah() { return jumlah; }

    public void setJumlah(String jumlah) { this.jumlah = jumlah; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getNo_hp() { return no_hp; }

    public void setNo_hp(String no_hp) { this.no_hp = no_hp; }

    public String getKeterangan() { return keterangan; }

    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
}
