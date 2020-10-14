package com.example.donorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.ResponseModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText etUser, etEmail, etPassword, etNumber, etAddress;
    Spinner spGolDarah;
    Button btnRegist, bLogin;
    RadioGroup radioGroup;
    RadioButton radioButton;

    ProgressDialog progressDialog;
    String gol_darah, rh;
    String lat, lng;

    String API_KEY = "YOUR_PLACE_API_KEY";
    int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Locale lokasi = new Locale("in", "id");
        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Places.initialize(getApplicationContext(), API_KEY, lokasi);

        etUser = findViewById(R.id.etUser);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNumber = findViewById(R.id.etNumber);

        bLogin = findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        etAddress = findViewById(R.id.etAddress);
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(RegisterActivity.this);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });

        spGolDarah = findViewById(R.id.spGolDarah);
        spGolDarah.setOnItemSelectedListener(this);

        radioGroup = findViewById(R.id.rdGroup);

        btnRegist = findViewById(R.id.btnRegist);

        progressDialog = new ProgressDialog(this);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Send Data..");
                progressDialog.show();

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()){
                            Log.v("ERROR", "getInstance Failed" + task.getException());
                            return;
                        }

                        String tokenRegis = task.getResult().getToken();
                        Log.d("Token Regis", tokenRegis);

                        String username = etUser.getText().toString();
                        String email = etEmail.getText().toString();
                        String password = etPassword.getText().toString();
                        String no_telp = "+62" + etNumber.getText().toString();
                        String alamat = etAddress.getText().toString();
                        String gol_dar = gol_darah;
                        String rhesus = rh;
                        String latitude = lat;
                        String longitude = lng;

                        ApiUser apiUser = Retroserver.getClient().create(ApiUser.class);
                        Call<ResponseModel> call = apiUser.register(username, email, password, no_telp,
                                alamat, gol_dar, rhesus, latitude, longitude, tokenRegis);

                        call.enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                                progressDialog.dismiss();
                                ResponseModel res = response.body();

                                if (!res.getError()) {
                                    Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Isi Form Secara Lengkap!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } // FIREBASE
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION){
                Place place = Autocomplete.getPlaceFromIntent(data);

                LatLng latlng =  place.getLatLng();

                String latitude = String.valueOf(latlng.latitude);
                String longitude = String.valueOf(latlng.longitude);

                lat = latitude;
                lng = longitude;

                String lokasi = place.getName();
                etAddress.setText(lokasi);
            }
        }
    }

    public void checkedButton(View v){

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        rh = radioButton.getText().toString();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gol_darah = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
