package com.example.donorapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {

    @Expose
    @SerializedName("message") String message;
    @Expose
    @SerializedName("error") Boolean error;
    @Expose
    @SerializedName("picture") String picture;
    @Expose
    @SerializedName("result") List<UserModel> result;
    @Expose
    @SerializedName("data") List<RequestModel> data;
    @Expose
    @SerializedName("hasil") List<EventModel> hasil;
    @Expose
    @SerializedName("user") List<UserModel> user;

    public ResponseModel(){};

    public ResponseModel(String message, Boolean error, List<UserModel> result, List<RequestModel> data, List<UserModel> user) {
        this.message = message;
        this.error = error;
        this.result = result;
        this.data = data;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public List<UserModel> getResult() {
        return result;
    }

    public void setResult(List<UserModel> result) {
        this.result = result;
    }

    public List<RequestModel> getData() {
        return data;
    }

    public void setData(List<RequestModel> data) {
        this.data = data;
    }

    public List<EventModel> getHasil() { return hasil; }

    public void setHasil(List<EventModel> hasil) { this.hasil = hasil; }

    public List<UserModel> getUser() { return user; }

    public void setUser(List<UserModel> user) { this.user = user; }
}
