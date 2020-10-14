package com.example.donorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.donorapp.model.RequestModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    boolean exit = false;

    BottomNavigationView bottomNavigationView;

    private List<RequestModel> mItem = new ArrayList<>();
    FloatingActionButton fabDonor;
    Fragment fragment;
    int halaman, notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle(null);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListenter);

        Intent i = getIntent();
        halaman = i.getIntExtra("halaman", 0);

        if (halaman == 0){

            fragment = new HomeFragment();
            notif = 0;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            bottomNavigationView.getMenu().findItem(R.id.navHome).setChecked(true);

        } else if (halaman == 1){

            fragment = new EventFragment();
            notif = 1;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            bottomNavigationView.getMenu().findItem(R.id.navEvent).setChecked(true);

        } else if (halaman == 2){

            fragment = new ProfileFragment();
            notif = 2;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            bottomNavigationView.getMenu().findItem(R.id.navProfile).setChecked(true);

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListenter =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.navHome:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.navEvent:
                            selectedFragment = new EventFragment();
                            break;

                        case R.id.navCommunity:
                            selectedFragment = new CommunityFragment();
                            break;

                        case R.id.navProfile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.notif:
                Intent i = new Intent(this, NotificationActivity.class);
                i.putExtra("before", notif);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (exit){
            super.onBackPressed();
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
