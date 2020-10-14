package com.example.donorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CommunityBlood4Life extends AppCompatActivity {
    TextView deskripsi;
    ImageView twtBlood, igBlood, fbBlood, webBlood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_blood4_life);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarBlood4Life);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Blood4Life Indonesia");

        twtBlood = findViewById(R.id.twtBlood4Life);
        twtBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                try {
                    getApplicationContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=124466389"));
                } catch (PackageManager.NameNotFoundException e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Blood4LifeID"));
                }
                startActivity(i);
            }
        });

        fbBlood = findViewById(R.id.fbBlood4Life);
        fbBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                try {
                    getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/131497304012"));
                } catch (PackageManager.NameNotFoundException e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Blood4LifeId"));
                }
                startActivity(i);
            }
        });

        igBlood = findViewById(R.id.igBlood4Life);
        igBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                try {
                    getApplicationContext().getPackageManager().getPackageInfo("com.instagram.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/blood4lifeid"));
                } catch (PackageManager.NameNotFoundException e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/blood4lifeid"));
                }
                startActivity(i);
            }
        });

        webBlood = findViewById(R.id.webBlood4Life);
        webBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blood4life.id/"));
                startActivity(i);
            }
        });

        deskripsi = findViewById(R.id.deskripsiBlood4Life);
        deskripsi.setText("Blood for Life Indonesia (@Blood4LifeID) adalah komunitas sosial yang dengan sukarela membantu orang lain yang membutuhkan darah." +
                    " \n\nKomunitas Blood4LifeID didirikan oleh Valencia Mieke Randa (Kak Valen) pada tanggal 19 Maret 2009 sebagai penghubung antara orang yang membutuhkan" +
                    " darah dan orang yang bersedia untuk mendonorkan darahnya secara sukarela, serta memberi edukasi mengenai pentingnya donor darah." +
                    " \n\nAwal movement Blood for Life Indonesia dimulai dari Mailing List (milis) dengan 44 orang anggota dari pembaca blog Valencia Mieke Randa." +
                    " Saat ini menggunakan social media Facebook, IG, dan Twitter @Blood4LifeID dan sedang mengembangkan website www.Blood4LifeID.com");
    }
}
