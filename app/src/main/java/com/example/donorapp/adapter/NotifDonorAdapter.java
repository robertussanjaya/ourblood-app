package com.example.donorapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorapp.DetailDonorActivity;
import com.example.donorapp.R;
import com.example.donorapp.model.RequestModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotifDonorAdapter extends RecyclerView.Adapter<NotifDonorAdapter.HolderData> {

    private List<RequestModel> requestList;
    private Context context;

    public NotifDonorAdapter(List<RequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_donor_notif, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        RequestModel requestModel = requestList.get(position);
        holder.namaPasien.setText(requestModel.getNama_pasien());
        holder.golDarah.setText(requestModel.getGol_dar());
        holder.RH.setText(requestModel.getRhesus());
        holder.lokasi.setText(requestModel.getLokasi());
        holder.foto = requestModel.getFoto();
        holder.lat = requestModel.getLat();
        holder.lng = requestModel.getLng();
        holder.waktu = requestModel.getTimestamp();

        String tglDB = requestModel.getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date timestampDB = sdf.parse(tglDB);
            SimpleDateFormat ubahFormat = new SimpleDateFormat("dd/MM/yyyy");
            String tglTampil = ubahFormat.format(timestampDB);
            holder.tglShow = tglTampil;
            holder.tanggal.setText(tglTampil);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.keterangan = requestModel.getKeterangan();
        holder.noHAPE = requestModel.getNo_hp();
        holder.jml.setText(requestModel.getJumlah());

        if (holder.keterangan.trim().length() == 0){
            holder.keterangan = " - ";
            return;
        } else {
            holder.keterangan = requestModel.getKeterangan();
            return;
        }

    }

    @Override
    public int getItemCount() { return requestList.size(); }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView namaPasien, golDarah, RH, lokasi, tanggal, jml;
        String foto, lat, lng, waktu, keterangan, tglShow, noHAPE;

        public HolderData(@NonNull View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(v.getContext(), DetailDonorActivity.class);
                    intent.putExtra("pasien", requestList.get(getAdapterPosition()).getNama_pasien());
                    intent.putExtra("golDar", requestList.get(getAdapterPosition()).getGol_dar());
                    intent.putExtra("rhesus", requestList.get(getAdapterPosition()).getRhesus());
                    intent.putExtra("jumlah", requestList.get(getAdapterPosition()).getJumlah());
                    intent.putExtra("foto", requestList.get(getAdapterPosition()).getFoto());
                    intent.putExtra("lokasi", requestList.get(getAdapterPosition()).getLokasi());
                    intent.putExtra("tanggal", tglShow);
                    intent.putExtra("id_user", requestList.get(getAdapterPosition()).getId_user());
                    intent.putExtra("lat", requestList.get(getAdapterPosition()).getLat());
                    intent.putExtra("lng", requestList.get(getAdapterPosition()).getLng());
                    intent.putExtra("no_hp", noHAPE);
                    intent.putExtra("keterangan", keterangan);
                    v.getContext().startActivity(intent);
                }
            });

            namaPasien = v.findViewById(R.id.namaPasienNotif);
            golDarah = v.findViewById(R.id.golDarahNotif);
            RH = v.findViewById(R.id.rhNotif);
            lokasi = v.findViewById(R.id.lokasiNotif);
            tanggal = v.findViewById(R.id.tanggalNotif);
            jml = v.findViewById(R.id.jmlNotif);
        }
    }
}
