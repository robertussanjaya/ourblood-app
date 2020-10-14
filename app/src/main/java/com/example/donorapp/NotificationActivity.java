package com.example.donorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.donorapp.adapter.NotifDonorAdapter;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.RequestModel;
import com.example.donorapp.model.ResponseModel;
import com.example.donorapp.util.SessionManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotifDonorAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ProgressDialog pd;
    SessionManager sm;
    String gol_dar, rhesus;
    TextView notifKosong;

    private List<RequestModel> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarNotif);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Notifikasi");

        notifKosong = findViewById(R.id.notifKosong);

        sm = new SessionManager(this);
        HashMap<String, String> map = sm.getDetailLogin();
        gol_dar = map.get(sm.KEY_GOLDAR);
        rhesus = map.get(sm.KEY_RHESUS);

        recyclerView = findViewById(R.id.recyclerNotif);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();

        ApiUser api = Retroserver.getClient().create(ApiUser.class);
        Call<ResponseModel> call = api.getNotifDonor(gol_dar, rhesus);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    pd.dismiss();
                    if (!response.body().getError()){
                        recyclerView.setVisibility(View.VISIBLE);
                        notifKosong.setVisibility(View.GONE);

                        requestList = response.body().getData();
                        adapter = new NotifDonorAdapter(requestList, NotificationActivity.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else if (response.body().getError()){

                        recyclerView.setVisibility(View.GONE);
                        notifKosong.setVisibility(View.VISIBLE);

                    }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
                recyclerView.setVisibility(View.GONE);
                notifKosong.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        int halaman = intent.getIntExtra("before", 5);

        if (halaman == 0 || halaman == 1 || halaman == 2){
            super.onBackPressed();
        } else {

            Intent i = new Intent(NotificationActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("halaman", 0);
            startActivity(i);

        }
    }
}
