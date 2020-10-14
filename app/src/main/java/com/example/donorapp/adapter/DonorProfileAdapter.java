package com.example.donorapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorapp.DetailDonorActivity;
import com.example.donorapp.EditDonor;
import com.example.donorapp.R;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.RequestModel;
import com.example.donorapp.model.ResponseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonorProfileAdapter extends RecyclerView.Adapter<DonorProfileAdapter.HolderData> {

    private List<RequestModel> mList;
    private Context context;

    public DonorProfileAdapter(List<RequestModel> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_donor_profile, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DonorProfileAdapter.HolderData holder, int position) {
        RequestModel requestModel = mList.get(position);
        holder.namaPasien.setText(requestModel.getNama_pasien());
        holder.golDarah.setText(requestModel.getGol_dar());
        holder.RH.setText(requestModel.getRhesus());
        holder.lokasi.setText(requestModel.getLokasi());
        holder.foto = requestModel.getFoto();
        holder.lat = requestModel.getLat();
        holder.lng = requestModel.getLng();
        holder.noHAPE =  requestModel.getNo_hp();

        String dbTanggal = requestModel.getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date tglDB = sdf.parse(dbTanggal);
            SimpleDateFormat ubahTanggal = new SimpleDateFormat("dd/MM/yyyy");
            holder.tglTampil = ubahTanggal.format(tglDB);
            holder.tanggal.setText(ubahTanggal.format(tglDB));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Hapus Permintaan Pendonor?")
                        .setMessage("Pastikan kebutuhan darah telah terpenuhi")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id_req = requestModel.getId_request();
                                ApiUser api = Retroserver.getClient().create(ApiUser.class);
                                Call<ResponseModel> call = api.terpenuhi(id_req);
                                call.enqueue(new Callback<ResponseModel>() {
                                    @Override
                                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                        mList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, mList.size());
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            }
        });

        holder.keterangan = requestModel.getKeterangan();

        if (holder.keterangan.trim().length() == 0){
            holder.keterangan = " - ";
            return;
        } else {
            holder.keterangan = requestModel.getKeterangan();
            return;
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView namaPasien, golDarah, RH, lokasi, tanggal;
        String foto, lat, lng, tglTampil, noHAPE, keterangan;
        ImageView delete, edit;

        public HolderData(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailDonorActivity.class);
                    intent.putExtra("pasien", mList.get(getAdapterPosition()).getNama_pasien());
                    intent.putExtra("golDar", mList.get(getAdapterPosition()).getGol_dar());
                    intent.putExtra("rhesus", mList.get(getAdapterPosition()).getRhesus());
                    intent.putExtra("jumlah", mList.get(getAdapterPosition()).getJumlah());
                    intent.putExtra("foto", mList.get(getAdapterPosition()).getFoto());
                    intent.putExtra("lokasi", mList.get(getAdapterPosition()).getLokasi());
                    intent.putExtra("tanggal", tglTampil);
                    intent.putExtra("id_user", mList.get(getAdapterPosition()).getId_user());
                    intent.putExtra("lat", mList.get(getAdapterPosition()).getLat());
                    intent.putExtra("lng", mList.get(getAdapterPosition()).getLng());
                    intent.putExtra("no_hp", noHAPE);
                    intent.putExtra("keterangan", keterangan);
                    view.getContext().startActivity(intent);
                }
            });

            delete = view.findViewById(R.id.deleteDonor);

            edit = view.findViewById(R.id.editDonor);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), EditDonor.class);
                    i.putExtra("id_request", mList.get(getAdapterPosition()).getId_request());
                    i.putExtra("id_user", mList.get(getAdapterPosition()).getId_request());
                    i.putExtra("pasien", mList.get(getAdapterPosition()).getNama_pasien());
                    i.putExtra("golDar", mList.get(getAdapterPosition()).getGol_dar());
                    i.putExtra("rhesus", mList.get(getAdapterPosition()).getRhesus());
                    i.putExtra("jumlah", mList.get(getAdapterPosition()).getJumlah());
                    i.putExtra("foto", mList.get(getAdapterPosition()).getFoto());
                    i.putExtra("lokasi", mList.get(getAdapterPosition()).getLokasi());
                    i.putExtra("tanggal", tglTampil);
                    i.putExtra("id_user", mList.get(getAdapterPosition()).getId_user());
                    i.putExtra("lat", mList.get(getAdapterPosition()).getLat());
                    i.putExtra("lng", mList.get(getAdapterPosition()).getLng());
                    i.putExtra("no_hp", noHAPE);
                    i.putExtra("keterangan", keterangan);
                    view.getContext().startActivity(i);
                }
            });

            namaPasien = view.findViewById(R.id.namaPasienProf);
            golDarah = view.findViewById(R.id.golDarahProf);
            RH = view.findViewById(R.id.rhProf);
            lokasi = view.findViewById(R.id.lokasiProf);
            tanggal = view.findViewById(R.id.tanggalProf);
        }
    }
}
