package com.example.donorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.ResponseModel;
import com.example.donorapp.model.UserModel;
import com.example.donorapp.util.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin, btnRegis;
    SessionManager sessionManager;

    String tokenBaru;
    boolean exit = false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenBaru = "";

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        progressDialog = new ProgressDialog(LoginActivity.this);
        sessionManager = new SessionManager(LoginActivity.this);

        btnRegis = findViewById(R.id.btnRegis);
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Sending Data..");
                progressDialog.show();

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("ERROR", "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            Log.d("Token Login: ", token);

                            String email = etEmail.getText().toString();
                            String password = etPassword.getText().toString();

                            ApiUser apiUser = Retroserver.getClient().create(ApiUser.class);

                            Call<ResponseModel> call = apiUser.login(email, password, token);
                            call.enqueue(new Callback<ResponseModel>() {
                                @Override
                                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                                    progressDialog.dismiss();
                                    ResponseModel res = response.body();
                                    List<UserModel> userModel = res.getResult();

                                    if (!res.getError()){

                                        sessionManager.storeLogin(userModel.get(0).getId_user(), userModel.get(0).getUsername(),
                                                userModel.get(0).getEmail(), userModel.get(0).getGol_darah(), userModel.get(0).getRhesus(),
                                                userModel.get(0).getFoto(), userModel.get(0).getLat(), userModel.get(0).getLng(),
                                                userModel.get(0).getTelepon(), userModel.get(0).getAlamat());

                                        Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                                        Intent intent;
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<ResponseModel> call, Throwable t) {

                                    Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit){
            finish();
            return;
        }
        this.exit = true;
        Toast.makeText(this, "Tekan Sekali Lagi untuk Keluar..", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }
}
