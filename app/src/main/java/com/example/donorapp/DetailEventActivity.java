package com.example.donorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView username, judul, tanggal, mulai, selesai, lokasi, keterangan;
    ImageView fotoEvent, fotoUser, eventStatus;
    String id_user, lat, lng, namaTempat;

    private GoogleMap mMap;
    ProgressDialog pd;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "YOUR_PLACE_API_KEY");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarDetailEvent);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Detail Event");

        eventStatus = findViewById(R.id.imgTanggal);

        username = findViewById(R.id.username);
        judul = findViewById(R.id.judulEvent);
        tanggal = findViewById(R.id.tglEvent);
        mulai = findViewById(R.id.mulaiEvent);
        selesai = findViewById(R.id.selesaiEvent);
        lokasi = findViewById(R.id.namaLokasi);
        keterangan = findViewById(R.id.keteranganEvent);

        fotoEvent = findViewById(R.id.imgEvent);
        fotoUser = findViewById(R.id.fotoUser);

        Intent i = getIntent();

        id_user = i.getStringExtra("id_user");

        pd = new ProgressDialog(DetailEventActivity.this);
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();

        ApiUser api = Retroserver.getClient().create(ApiUser.class);
        Call<ResponseModel> call = api.getUserId(id_user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pd.dismiss();
                boolean err = response.body().getError();

                if(err){
                    Toast.makeText(DetailEventActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    username.setText(response.body().getMessage());
                    String foto = "http://ourblood-admin.my.id/donorAPI/upload/user/" + response.body().getPicture();
                    Glide.with(DetailEventActivity.this)
                            .load(foto)
                            .into(fotoUser);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });

        String foto = "http://ourblood-admin.my.id/donorAPI/upload/event/" + i.getStringExtra("foto");
        Glide.with(this)
                .load(foto)
                .override(1600, 1600)
                .into(fotoEvent);

        judul.setText(i.getStringExtra("judul"));

        // Bandingin 2 tanggal
        String tgl = i.getStringExtra("tanggal"); // Pake format dd/mm/yyyy aja, ubah database
        String status = "";

        Calendar c = Calendar.getInstance();
        Locale lokal = new Locale("in", "id");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String now = sdf.format(c.getTime());
            Date present = sdf.parse(now);
            Date eventDate = sdf.parse(tgl);

            if (present.compareTo(eventDate) > 0) {
                eventStatus.setImageResource(R.drawable.ic_event_after);
                SimpleDateFormat formatTampil = new SimpleDateFormat("EEEE, dd MMM yyyy", lokal);
                tanggal.setText(formatTampil.format(eventDate) + " - (Berakhir)");

            }
            else if (present.compareTo(eventDate) < 0) {
                eventStatus.setImageResource(R.drawable.ic_event_before);
                SimpleDateFormat formatTampil = new SimpleDateFormat("EEEE, dd MMM yyyy", lokal);
                tanggal.setText(formatTampil.format(eventDate) + " - (Belum Mulai)");
            }
            else if (present.compareTo(eventDate) == 0) {
                eventStatus.setImageResource(R.drawable.ic_berlangsung);
                SimpleDateFormat formatTampil = new SimpleDateFormat("EEEE, dd MMM yyyy", lokal);
                tanggal.setText(formatTampil.format(eventDate));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mulai.setText(i.getStringExtra("mulai"));
        selesai.setText(i.getStringExtra("selesai"));
        lokasi.setText(i.getStringExtra("lokasi"));
        keterangan.setText(i.getStringExtra("keterangan"));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");
        namaTempat = intent.getStringExtra("lokasi");

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lng);

        LatLng posisi = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(posisi).title(namaTempat));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posisi, 15);
        mMap.animateCamera(update);
    }
}
