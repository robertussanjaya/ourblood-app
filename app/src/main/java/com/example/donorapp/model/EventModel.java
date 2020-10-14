package com.example.donorapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventModel {

    @Expose
    @SerializedName("id_event") String id_event;
    @Expose
    @SerializedName("id_user") String id_user;
    @Expose
    @SerializedName("nama_event") String nama_event;
    @Expose
    @SerializedName("lokasi") String lokasi;
    @Expose
    @SerializedName("tanggal") String tanggal;
    @Expose
    @SerializedName("waktu") String waktu;
    @Expose
    @SerializedName("waktu_selesai") String waktu_selesai;
    @Expose
    @SerializedName("keterangan") String keterangan;
    @Expose
    @SerializedName("foto") String foto;
    @Expose
    @SerializedName("lat") String lat;
    @Expose
    @SerializedName("lng") String lng;

    public EventModel() { }

    public String getId_event() {
        return id_event;
    }

    public void setId_event(String id_event) {
        this.id_event = id_event;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama_event() {
        return nama_event;
    }

    public void setNama_event(String nama_event) {
        this.nama_event = nama_event;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getWaktu_selesai() {
        return waktu_selesai;
    }

    public void setWaktu_selesai(String waktu_selesai) {
        this.waktu_selesai = waktu_selesai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
