package com.example.donorapp.api;

import com.example.donorapp.model.ResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiUser {

    @FormUrlEncoded
    @POST("user/register.php")
    Call<ResponseModel> register(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("no_telp") String no_telp,
            @Field("alamat") String alamat,
            @Field("gol_dar") String gol_dar,
            @Field("rhesus") String rhesus,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("token") String token
    );

    @Multipart
    @POST("donor/requestDonor.php")
    Call<ResponseModel> requestDonor(
            @Part("id_user") RequestBody id_user,
            @Part("nama_pasien") RequestBody nama_pasien,
            @Part("no_hp") RequestBody no_hp,
            @Part("lokasi") RequestBody lokasi,
            @Part("jumlah") RequestBody jumlah,
            @Part("gol_dar") RequestBody gol_dar,
            @Part("rhesus") RequestBody rhesus,
            @Part MultipartBody.Part image,
            @Part("ket") RequestBody ket,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng
            );

    @FormUrlEncoded
    @POST("user/login.php")
    Call<ResponseModel> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("token") String token
    );

    @GET("donor/getDonor.php")
    Call<ResponseModel> getDonor();

    @GET("event/getEvent.php")
    Call<ResponseModel> getEvent();

    @FormUrlEncoded
    @POST("donor/getDonorById.php")
    Call<ResponseModel> getDonorById(
            @Field("id_user") String id_user
    );

    @FormUrlEncoded
    @POST("event/getEventById.php")
    Call<ResponseModel> getEventById(
            @Field("id_user") String id_user
    );

    @Multipart
    @POST("event/createEvent.php")
    Call<ResponseModel> createEvent(
            @Part("id_user") RequestBody id_user,
            @Part("nama_event") RequestBody nama_event,
            @Part("lokasi") RequestBody lokasi,
            @Part("tanggal") RequestBody tanggal,
            @Part("waktu") RequestBody waktu,
            @Part("waktu_selesai") RequestBody waktu_selesai,
            @Part("keterangan") RequestBody keterangan,
            @Part MultipartBody.Part image,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng
    );

    @FormUrlEncoded
    @POST("user/getUserById.php")
    Call<ResponseModel> getUserId(
            @Field("id_user") String id_user
    );

    @FormUrlEncoded
    @POST("user/getNotifDonor.php")
    Call<ResponseModel> getNotifDonor(
            @Field("gol_dar") String gol_dar,
            @Field("rhesus") String rhesus
    );

    @Multipart
    @POST("donor/editRequest.php")
    Call<ResponseModel> editDonor(
            @Part("id_request") RequestBody id_request,
            @Part("id_user") RequestBody id_user,
            @Part("nama_pasien") RequestBody nama_pasien,
            @Part("no_hp") RequestBody no_hp,
            @Part("lokasi") RequestBody lokasi,
            @Part("jumlah") RequestBody jumlah,
            @Part("gol_dar") RequestBody gol_dar,
            @Part("rhesus") RequestBody rhesus,
            @Part MultipartBody.Part image,
            @Part("ket") RequestBody ket,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng
    );

    @FormUrlEncoded
    @POST("donor/requestTerpenuhi.php")
    Call<ResponseModel> terpenuhi(
            @Field("id_request") String id_request
    );

    @FormUrlEncoded
    @POST("donor/getDonorBySpinner.php")
    Call<ResponseModel> spinner(
            @Field("gol_dar") String gol_dar,
            @Field("rhesus") String rhesus
    );

    @Multipart
    @POST("event/editEvent.php")
    Call<ResponseModel> editEvent(
            @Part("id_event") RequestBody id_event,
            @Part("id_user") RequestBody id_user,
            @Part("nama_event") RequestBody nama_event,
            @Part("lokasi") RequestBody lokasi,
            @Part("tanggal") RequestBody tanggal,
            @Part("waktu") RequestBody waktu,
            @Part("waktu_selesai") RequestBody waktu_selesai,
            @Part("keterangan") RequestBody keterangan,
            @Part MultipartBody.Part image,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng
    );

    @FormUrlEncoded
    @POST("event/eventSelesai.php")
    Call<ResponseModel> hapusEvent(
            @Field("id_event") String id_event
    );

    @Multipart
    @POST("user/editProfile.php")
    Call<ResponseModel> editProfil(
            @Part("id_user") RequestBody id_user,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("password_baru") RequestBody password_baru,
            @Part("no_telp") RequestBody no_telp,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part image,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng
    );
}
