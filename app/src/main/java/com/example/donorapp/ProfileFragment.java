package com.example.donorapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donorapp.adapter.DonorProfileAdapter;
import com.example.donorapp.adapter.EventProfileAdapter;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.EventModel;
import com.example.donorapp.model.RequestModel;
import com.example.donorapp.model.ResponseModel;
import com.example.donorapp.util.SessionManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    TextView txtUsername, goldar, rhesus, user1, user2;
    Button btnLogout, btnEdit;
    String id_user;

    ImageView profilePic;

    RelativeLayout eventKosong, donorKosong;

    SessionManager sm;
    ProgressDialog pd;

    private RecyclerView recyclerDonorProfile, recyclerEventProfile;
    private EventProfileAdapter eventAdapter;
    private DonorProfileAdapter donorAdapter;
    private RecyclerView.LayoutManager layoutManager, layoutManager1;


    private List<EventModel> eventList;
    private List<RequestModel> requestList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        eventKosong = view.findViewById(R.id.eventKosong);
        donorKosong = view.findViewById(R.id.requestKosong);

        txtUsername = view.findViewById(R.id.txtUsername);
        goldar = view.findViewById(R.id.txtGoldar);
        rhesus = view.findViewById(R.id.txtRH);
        user1 = view.findViewById(R.id.namaUser);
        user2 = view.findViewById(R.id.namaUser2);
        profilePic = view.findViewById(R.id.profile_image);

        btnLogout = view.findViewById(R.id.logout);
        btnEdit = view.findViewById(R.id.btnEditProfile);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), EditProfile.class);
                view.getContext().startActivity(i);
            }
        });

        sm = new SessionManager(getContext());

        HashMap<String, String> map = sm.getDetailLogin();
        txtUsername.setText(map.get(sm.KEY_USERNAME));
        user1.setText(map.get(sm.KEY_USERNAME));
        user2.setText(map.get(sm.KEY_USERNAME));
        goldar.setText(map.get(sm.KEY_GOLDAR));
        rhesus.setText(map.get(sm.KEY_RHESUS));

        String foto = "http://ourblood-admin.my.id/donorAPI/upload/user/" + map.get(sm.KEY_FOTO);
        Glide.with(view)
                .load(foto)
                .into(profilePic);

        id_user = map.get(sm.KEY_IDUSER);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerDonorProfile = view.findViewById(R.id.recyclerDonorProfile);
        recyclerEventProfile = view.findViewById(R.id.recyclerEventProfile);

        recyclerDonorProfile.setLayoutManager(layoutManager);
        recyclerEventProfile.setLayoutManager(layoutManager1);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.setCancelable(true);
        pd.show();

        ApiUser api = Retroserver.getClient().create(ApiUser.class);
        Call<ResponseModel> call = api.getDonorById(id_user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                requestList = response.body().getData();
                if (requestList != null){
                    recyclerDonorProfile.setVisibility(View.VISIBLE);
                    donorKosong.setVisibility(View.GONE);
                    donorAdapter = new DonorProfileAdapter(requestList, getContext());
                    recyclerDonorProfile.setAdapter(donorAdapter);
                    donorAdapter.notifyDataSetChanged();

                    pd.dismiss();
                } else {
                    recyclerDonorProfile.setVisibility(View.GONE);
                    donorKosong.setVisibility(View.VISIBLE);
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                pd.dismiss();
//                Toast.makeText(getContext(), "Terjadi Kesalahan Koneksi", Toast.LENGTH_SHORT).show();

            }
        });

        Call<ResponseModel> callEvent = api.getEventById(id_user);
        callEvent.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                eventList = response.body().getHasil();

                if (eventList != null){
                    recyclerEventProfile.setVisibility(View.VISIBLE);
                    eventKosong.setVisibility(View.GONE);
                    eventAdapter = new EventProfileAdapter(eventList, getContext());
                    recyclerEventProfile.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();

                    pd.dismiss();
                } else {
                    recyclerEventProfile.setVisibility(View.GONE);
                    eventKosong.setVisibility(View.VISIBLE);
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
//                Toast.makeText(getContext(), , Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.logout();
            }
        });

        return view;
    }
}
