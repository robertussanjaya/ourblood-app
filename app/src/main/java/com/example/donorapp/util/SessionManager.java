package com.example.donorapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.donorapp.LoginActivity;
import com.example.donorapp.MainActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IDUSER = "id_user";
    public static final String KEY_GOLDAR = "gol_darah";
    public static final String KEY_RHESUS = "rhesus";
    public static final String KEY_FOTO = "foto";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_TELEPON = "telepon";
    public static final String KEY_ALAMAT = "alamat";

    private static final String IS_LOGIN = "loginstatus";
    private final String SHARE_NAME = "loginsession";
    private final int MODE_PRIVATE = 0;
    private Context _context;

    public SessionManager (Context context){

        this._context = context;
        sharedPreferences = _context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void storeLogin (String id_user, String username, String email,
                            String gol_darah, String rhesus, String foto, String lat,
                            String lng, String telepon, String alamat){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_IDUSER, id_user);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_GOLDAR, gol_darah);
        editor.putString(KEY_RHESUS, rhesus);
        editor.putString(KEY_FOTO, foto);
        editor.putString(KEY_LAT, lat);
        editor.putString(KEY_LNG, lng);
        editor.putString(KEY_TELEPON, telepon);
        editor.putString(KEY_ALAMAT, alamat);
        editor.commit();

    }

    public void storeEdit (String id_user, String username, String email, String foto, String lat,
                            String lng, String telepon, String alamat){

        editor.putString(KEY_IDUSER, id_user);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FOTO, foto);
        editor.putString(KEY_LAT, lat);
        editor.putString(KEY_LNG, lng);
        editor.putString(KEY_TELEPON, telepon);
        editor.putString(KEY_ALAMAT, alamat);
        editor.commit();

    }

    public HashMap getDetailLogin (){

        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, sharedPreferences.getString(KEY_USERNAME, null));
        map.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        map.put(KEY_IDUSER, sharedPreferences.getString(KEY_IDUSER, null));
        map.put(KEY_GOLDAR, sharedPreferences.getString(KEY_GOLDAR, null));
        map.put(KEY_RHESUS, sharedPreferences.getString(KEY_RHESUS, null));
        map.put(KEY_FOTO, sharedPreferences.getString(KEY_FOTO, null));
        map.put(KEY_LAT, sharedPreferences.getString(KEY_LAT, null));
        map.put(KEY_LNG, sharedPreferences.getString(KEY_LNG, null));
        map.put(KEY_TELEPON, sharedPreferences.getString(KEY_TELEPON, null));
        map.put(KEY_ALAMAT, sharedPreferences.getString(KEY_ALAMAT, null));
        return map;

    }

    public void checkLogin (){

        if (!this.Login()){

            Intent login = new Intent(_context, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(login);

        } else {

            Intent login_true = new Intent(_context, MainActivity.class);
            login_true.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login_true.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            login_true.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(login_true);

        }

    }

    public void logout(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public Boolean Login (){

        return sharedPreferences.getBoolean(IS_LOGIN, false);

    }

}
