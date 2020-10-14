package com.example.donorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommunityUTDJakarta extends AppCompatActivity {
    TextView deskripsi;
    ImageView twtUTD, igUTD, fbUTD, webUTD;
    Button cekDarah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_utdjakarta);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarUTDJakarta);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("UTD PMI Jakarta");

        cekDarah = findViewById(R.id.cekDarah);
        cekDarah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.infoutdpmidki.com/webinfo/LoginStockDarah.aspx"));
                startActivity(i);
            }
        });

        twtUTD = findViewById(R.id.twtUTD);
        twtUTD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                try {
                    getApplicationContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=889458150264381441"));
                } catch (PackageManager.NameNotFoundException e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/dondar_jakarta"));
                }
                startActivity(i);
            }
        });

        fbUTD = findViewById(R.id.fbUTD);
        fbUTD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                try {
                    getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/173173686638746"));
                } catch (PackageManager.NameNotFoundException e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/utdpmidkijakarta/"));
                }
                startActivity(i);
            }
        });

        igUTD = findViewById(R.id.igUTD);
        igUTD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                try {
                    getApplicationContext().getPackageManager().getPackageInfo("com.instagram.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/donordarahjakarta"));
                } catch (PackageManager.NameNotFoundException e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/donordarahjakarta"));
                }
                startActivity(i);
            }
        });

        webUTD = findViewById(R.id.webUTD);
        webUTD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://utdpmidkijakarta.or.id/"));
                startActivity(i);
            }
        });

        deskripsi = findViewById(R.id.deskripsiUTDJakarta);
        deskripsi.setText("Unit Transfusi Darah merupakan salah satu unit kerja yang ada di PMI Provinsi DKI Jakarta. Tugas dan fungsi utamanya ialah meningkatkan derajat kesehatan"
                + " melalui pengelolaan darah yang berkualitas, mewujudkan pelayanan penyediaan darah yang aman, tepat waktu, terjangkau dan berkesinambungan."
                + "\n\nDalam pemenuhan kebutuhan darah, UTD PMI Provinsi DKI Jakarta telah bekerja sama dengan 4000 kelompok donor darah di Provinsi DKI Jakarta."
                + " Setiap harinya sebanyak 800-1000 kantong darah pendonor masuk dan diolah menjadi komponen darah sesuai dengan persyaratan CPOB."
                + " Berbagai komponen darah yang diperlukan oleh pasien dapat disediakan oleh UTD PMI Provinsi DKI Jakarta mulai dari komponen darah yang rutin dipakai oleh pasien"
                + " (Packed Red Cell/PRC, Thrombocyte Concentrate/TC, Fresh Frozen Plasma/FFP) sampai komponen darah yang special seperti leukodepleted (komponen darah yang dikurangi jumlah"
                + " sel darah putihnya untuk mengurangi reaksi transfusi), cryoprecipitate-AHF (komponen darah yang hanya mengandung factor pembekuan factor 8), TC pooled dan TC Apheresis."
                + "\n\nUTD PMI Provinsi DKI Jakarta dengan 456 pegawai memberikan pelayanan donor darah dan pelayanan darah bagi pasien selama 24 jam tanpa henti ke 152 Rumah Sakit yang ada"
                + " di Jakarta dan 229 Rumah Sakit di luar Jakarta (Data tahun 2018).  Untuk mendekatkan pelayanan kepada masyarakat, telah dibuka UTD Cabang di PMI Kota Jakarta Barat, PMI Kota"
                + " Jakarta Timur dan PMI Kota Jakarta Selatan yang beroperasional setiap harinya.  Sedangkan di PMI Kota Jakarta Pusat telah ada kegiatan donor darah walaupun belum setiap hari."
                + " UTD PMI Provinsi DKI Jakarta selain menerima donor darah di gedung PMI DKI Jakarta juga memiliki armada Mobil Unit untuk melayani kegiatan donor darah yang diselenggarakan di instansi.");
    }
}
