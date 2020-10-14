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

import com.bumptech.glide.Glide;
import com.example.donorapp.DetailEventActivity;
import com.example.donorapp.EditEvent;
import com.example.donorapp.R;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.EventModel;
import com.example.donorapp.model.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventProfileAdapter extends RecyclerView.Adapter<EventProfileAdapter.HolderData> {

    private List<EventModel> mList;
    private Context context;
    String URL = "http://ourblood-admin.my.id/donorAPI/upload/event/";

    public EventProfileAdapter(List<EventModel> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_profile, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventProfileAdapter.HolderData holder, int position) {
        EventModel eventModel = mList.get(position);

        holder.namaEvent.setText(eventModel.getNama_event());
        holder.lokasiEvent.setText(eventModel.getLokasi());
        holder.waktuMulai.setText(eventModel.getWaktu());
        holder.waktuSelesai.setText(eventModel.getWaktu_selesai());
        holder.tanggalEvent.setText(eventModel.getTanggal());

        Glide.with(context)
                .load(URL + eventModel.getFoto())
                .thumbnail(0.5f)
                .into(holder.fotoEvent);

        holder.keterangan = eventModel.getKeterangan();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Hapus Event")
                        .setMessage("Anda ingin menghapus event " + eventModel.getNama_event() + "?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ApiUser api = Retroserver.getClient().create(ApiUser.class);
                                Call<ResponseModel> call = api.hapusEvent(eventModel.getId_event());
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

        if (holder.keterangan.trim().length() == 0){
            holder.keterangan = " - ";
            return;
        } else {
            holder.keterangan = eventModel.getKeterangan();
            return;
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView namaEvent, lokasiEvent, waktuMulai, waktuSelesai, tanggalEvent;
        ImageView fotoEvent, delete, edit;
        String keterangan, judulEvent;

        public HolderData(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(view.getContext(), DetailEventActivity.class);
                    i.putExtra("id_event", mList.get(getAdapterPosition()).getId_event());
                    i.putExtra("id_user", mList.get(getAdapterPosition()).getId_user());
                    i.putExtra("foto", mList.get(getAdapterPosition()).getFoto());
                    i.putExtra("judul", mList.get(getAdapterPosition()).getNama_event());
                    i.putExtra("tanggal", mList.get(getAdapterPosition()).getTanggal());
                    i.putExtra("mulai", mList.get(getAdapterPosition()).getWaktu());
                    i.putExtra("selesai", mList.get(getAdapterPosition()).getWaktu_selesai());
                    i.putExtra("lokasi", mList.get(getAdapterPosition()).getLokasi());
                    i.putExtra("keterangan", keterangan);
                    i.putExtra("lat", mList.get(getAdapterPosition()).getLat());
                    i.putExtra("lng", mList.get(getAdapterPosition()).getLng());
                    view.getContext().startActivity(i);
                }
            });

            edit = view.findViewById(R.id.editEvent);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(view.getContext(), EditEvent.class);
                    i.putExtra("id_event", mList.get(getAdapterPosition()).getId_event());
                    i.putExtra("id_user", mList.get(getAdapterPosition()).getId_user());
                    i.putExtra("foto", mList.get(getAdapterPosition()).getFoto());
                    i.putExtra("judul", mList.get(getAdapterPosition()).getNama_event());
                    i.putExtra("tanggal", mList.get(getAdapterPosition()).getTanggal());
                    i.putExtra("mulai", mList.get(getAdapterPosition()).getWaktu());
                    i.putExtra("selesai", mList.get(getAdapterPosition()).getWaktu_selesai());
                    i.putExtra("lokasi", mList.get(getAdapterPosition()).getLokasi());
                    i.putExtra("keterangan", mList.get(getAdapterPosition()).getKeterangan());
                    i.putExtra("lat", mList.get(getAdapterPosition()).getLat());
                    i.putExtra("lng", mList.get(getAdapterPosition()).getLng());
                    view.getContext().startActivity(i);
                }
            });

            delete = view.findViewById(R.id.deleteEvent);

            namaEvent = view.findViewById(R.id.namaEventProf);
            lokasiEvent = view.findViewById(R.id.lokasiEventProf);
            waktuMulai = view.findViewById(R.id.waktuMulaiProf);
            waktuSelesai = view.findViewById(R.id.waktuSelesaiProf);
            tanggalEvent = view.findViewById(R.id.tanggalEventProf);
            fotoEvent = view.findViewById(R.id.fotoEventProf);
        }
    }
}
