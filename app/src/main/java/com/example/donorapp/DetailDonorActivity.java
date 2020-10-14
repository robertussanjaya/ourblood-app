package com.example.donorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.ResponseModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailDonorActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView nama_pasien, golDar, rhesus, lokasi, username, jumlah, noHP, keterangan;
    ImageView buktiFoto, fotoUser, WA, phone;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_donor);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarDetailDonor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail Pencarian Pendonor");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "YOUR_PLACE_API_KEY");

        nama_pasien = findViewById(R.id.detailNama);
        golDar = findViewById(R.id.detailGoldar);
        rhesus = findViewById(R.id.detailRhesus);
        buktiFoto = findViewById(R.id.buktiFoto);
        lokasi = findViewById(R.id.lokasiDonor);
        jumlah = findViewById(R.id.jumlahDonor);
        noHP = findViewById(R.id.kontakPasien);
        keterangan = findViewById(R.id.keteranganDonor);
        WA = findViewById(R.id.WA);
        phone = findViewById(R.id.phone);

        Intent i = getIntent();
        String pasien = i.getStringExtra("pasien");
        String goldar = i.getStringExtra("golDar");
        String rh = i.getStringExtra("rhesus");
        String foto = "http://ourblood-admin.my.id/donorAPI/upload/donor/" + i.getStringExtra("foto");
        String alamat = i.getStringExtra("lokasi");
        String jml = i.getStringExtra("jumlah");

        String no_hp = i.getStringExtra("no_hp");
        WA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + no_hp;
                try {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(DetailDonorActivity.this, "Whatsapp belum terinstall..", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + no_hp));
                startActivity(i);
            }
        });

        String ket = i.getStringExtra("keterangan");
        String id_user = i.getStringExtra("id_user");
//        Toast.makeText(DetailDonorActivity.this, ket, Toast.LENGTH_SHORT).show();

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading..");
        pd.show();

        ApiUser api = Retroserver.getClient().create(ApiUser.class);
        Call<ResponseModel> call = api.getUserId(id_user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pd.dismiss();
                boolean err = response.body().getError();

                if(!err){
                    username.setText(response.body().getMessage());
                    String foto = "http://ourblood-admin.my.id/donorAPI/upload/user/" + response.body().getPicture();
                    Glide.with(DetailDonorActivity.this)
                            .load(foto)
                            .into(fotoUser);
                } else{
                    Toast.makeText(DetailDonorActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(DetailDonorActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        lokasi.setText(alamat);
        nama_pasien.setText(pasien);
        golDar.setText(goldar);
        rhesus.setText(rh);
        jumlah.setText(jml);
        noHP.setText(no_hp);
        keterangan.setText(ket);

        Glide.with(this)
                .load(foto)
                .override(1600, 1600)
                .into(buktiFoto);

        fotoUser = findViewById(R.id.userPicture);
        username = findViewById(R.id.nama);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        Intent i = getIntent();
        String lat = i.getStringExtra("lat");
        String lng = i.getStringExtra("lng");
        String lokasi = i.getStringExtra("lokasi");

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lng);

        LatLng posisi = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(posisi).title(lokasi));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posisi, 15);
        mMap.animateCamera(update);

    }
}
