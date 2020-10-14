package com.example.donorapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Retroserver {

    private static final String BASE_URL = "http://ourblood-admin.my.id/donorAPI/";

    private static Retrofit retrofit;

    public static Retrofit getClient(){

        if (retrofit == null){

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

        }

        return retrofit;

    }

}
