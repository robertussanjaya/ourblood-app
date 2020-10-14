package com.example.donorapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donorapp.DetailEventActivity;
import com.example.donorapp.R;
import com.example.donorapp.model.EventModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.HolderData> {

    private List<EventModel> mModel;
    private Context context;
    String URL = "http://ourblood-admin.my.id/donorAPI/upload/event/";

    public EventAdapter(List<EventModel> mModel, Context context) {
        this.mModel = mModel;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.HolderData holder, int position) {
        EventModel eventModel = mModel.get(position);
        holder.namaEvent.setText(eventModel.getNama_event());
        holder.lokasiEvent.setText(eventModel.getLokasi());
        holder.waktuMulai.setText(eventModel.getWaktu());
        holder.waktuSelesai.setText(eventModel.getWaktu_selesai());

        String tgl = eventModel.getTanggal(); //Bandingin Tanggal

        Calendar c = Calendar.getInstance();
        Locale lokal = new Locale("in", "id");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", lokal);

        try {
            String now = sdf.format(c.getTime());
            Date present = sdf.parse(now);
            Date event = sdf.parse(tgl);

            if (present.compareTo(event) > 0){
                holder.stats.setImageResource(R.drawable.ic_status_after);
                holder.status.setText("Berakhir");
                holder.status.setTextColor(Color.parseColor("#B33A3A"));
            }
            else if (present.compareTo(event) < 0){
                holder.stats.setImageResource(R.drawable.ic_status_before);
                holder.status.setText("Belum Dimulai");
                holder.status.setTextColor(Color.parseColor("#FFCC00"));

            } else if (present.compareTo(event) == 0){
                holder.stats.setImageResource(R.drawable.ic_berlangsung);
                holder.status.setText("Sedang Berlangsung");
                holder.status.setTextColor(Color.BLACK);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String myTanggal = eventModel.getTanggal(); //Ubah tanggal di recycler
        SimpleDateFormat ubahDB = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date myDate = ubahDB.parse(myTanggal);

            Locale indo = new Locale("in", "id");
            SimpleDateFormat finalFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", indo);

            String tglTampil = finalFormat.format(myDate);
            holder.tanggalEvent.setText(tglTampil);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.idEvent = eventModel.getId_event();
        holder.idUser = eventModel.getId_user();

        Glide.with(context)
                .load(URL + eventModel.getFoto())
                .thumbnail(0.5f)
                .into(holder.fotoEvent);

        holder.keterangan = eventModel.getKeterangan();

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
        return mModel.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        TextView namaEvent, lokasiEvent, waktuMulai, waktuSelesai, tanggalEvent, status;
        ImageView fotoEvent, stats;
        String idEvent, idUser, keterangan;

        public HolderData(View view) {
            super(view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(view.getContext(), DetailEventActivity.class);
                    i.putExtra("id_event", mModel.get(getAdapterPosition()).getId_event());
                    i.putExtra("id_user", mModel.get(getAdapterPosition()).getId_user());
                    i.putExtra("foto", mModel.get(getAdapterPosition()).getFoto());
                    i.putExtra("judul", mModel.get(getAdapterPosition()).getNama_event());
                    i.putExtra("tanggal", mModel.get(getAdapterPosition()).getTanggal());
                    i.putExtra("mulai", mModel.get(getAdapterPosition()).getWaktu());
                    i.putExtra("selesai", mModel.get(getAdapterPosition()).getWaktu_selesai());
                    i.putExtra("lokasi", mModel.get(getAdapterPosition()).getLokasi());
                    i.putExtra("keterangan", keterangan);
                    i.putExtra("lat", mModel.get(getAdapterPosition()).getLat());
                    i.putExtra("lng", mModel.get(getAdapterPosition()).getLng());
                    view.getContext().startActivity(i);
                }
            });

            namaEvent = view.findViewById(R.id.namaEvent);
            lokasiEvent = view.findViewById(R.id.lokasiEvent);
            waktuMulai = view.findViewById(R.id.waktuMulai);
            waktuSelesai = view.findViewById(R.id.waktuSelesai);
            tanggalEvent = view.findViewById(R.id.tanggalEvent);
            fotoEvent = view.findViewById(R.id.fotoEvent);
            status = view.findViewById(R.id.status);
            stats = view.findViewById(R.id.ic_status);

        }
    }
}
