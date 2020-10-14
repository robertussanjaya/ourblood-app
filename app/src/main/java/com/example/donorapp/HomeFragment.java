package com.example.donorapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorapp.adapter.RequestAdapter;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.RequestModel;
import com.example.donorapp.model.ResponseModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ProgressDialog pd;

    private List<RequestModel> mItem;
    FloatingActionButton fabDonor;

    Spinner spGolDar, spRH;
    String goldar = "";
    String rh = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_home, container, false);

        fabDonor = view.findViewById(R.id.fabDonor);

        fabDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ReqDonorActivity.class);
                startActivity(i);
            }
        });

        recyclerView = view.findViewById(R.id.recyclerDonor);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();

        ApiUser api = Retroserver.getClient().create(ApiUser.class);
        Call<ResponseModel> call = api.getDonor();
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                mItem = response.body().getData();
                mAdapter = new RequestAdapter(getContext(), mItem);
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

        spGolDar = view.findViewById(R.id.cariGolDar);
        spGolDar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goldar = parent.getSelectedItem().toString();
                if (goldar.equals("-GolDar-")){
                    goldar = "";
                }
                pd.show();
                ApiUser api = Retroserver.getClient().create(ApiUser.class);
                Call<ResponseModel> call =  api.spinner(goldar, rh);
                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.body().getData() != null) {
                            mItem = response.body().getData();
                            mAdapter = new RequestAdapter(getContext(), mItem);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            pd.dismiss();
                        } else {
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        spRH = view.findViewById(R.id.cariRH);
        spRH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rh = parent.getSelectedItem().toString();
                if (rh.equals("-Rhesus-")){
                    rh = "";
                }
                pd.show();
                ApiUser api = Retroserver.getClient().create(ApiUser.class);
                Call<ResponseModel> call = api.spinner(goldar, rh);
                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.body().getData() != null) {
                            mItem = response.body().getData();
                            mAdapter = new RequestAdapter(getContext(), mItem);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        return view;

    }

}
