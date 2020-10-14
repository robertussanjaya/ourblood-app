package com.example.donorapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorapp.adapter.EventAdapter;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.EventModel;
import com.example.donorapp.model.ResponseModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ProgressDialog pd;

    private List<EventModel> mList = new ArrayList<>();
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        fab = view.findViewById(R.id.fab);

        recyclerView = view.findViewById(R.id.recyclerEvent);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading..");
        pd.setCancelable(true);
        pd.show();

        ApiUser api = Retroserver.getClient().create(ApiUser.class);
        Call<ResponseModel> call = api.getEvent();
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                mList = response.body().getHasil();
                mAdapter = new EventAdapter(mList, getContext());
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(getContext(), "Terjadi Kesalahan Koneksi", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateEventActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}
