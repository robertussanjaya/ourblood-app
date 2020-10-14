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
import com.example.donorapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.HolderData> {

    private List<RequestModel> requestList;
    private Context context;
    public String parsedDistance, response;

    String API_KEY = "YOUR_DISTANCE_API_KEY";

    public RequestAdapter (Context context, List<RequestModel> requestList){
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        RequestModel requestModel = requestList.get(position);
        holder.namaPasien.setText(requestModel.getNama_pasien());
        holder.golDarah.setText(requestModel.getGol_dar());
        holder.RH.setText(requestModel.getRhesus());
        holder.jml.setText(requestModel.getJumlah());

        holder.foto = requestModel.getFoto();
        holder.idUser = requestModel.getId_user();
        holder.jumlah = requestModel.getJumlah();
        holder.no_hp = requestModel.getNo_hp();

        String myTanggal = requestModel.getTimestamp(); //Ubah tanggal di recycler
        SimpleDateFormat ubahDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date myDate = ubahDB.parse(myTanggal);

            Locale indo = new Locale("in", "id");
            SimpleDateFormat finalFormat = new SimpleDateFormat("dd MMMM yyyy", indo);

            String tglTampil = finalFormat.format(myDate);
            holder.tampilTanggal = tglTampil;
            holder.tanggal.setText(tglTampil);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        HashMap<String, String> map = holder.sm.getDetailLogin();
        holder.lat1 = Double.parseDouble(map.get(holder.sm.KEY_LAT));
        holder.lng1 = Double.parseDouble(map.get(holder.sm.KEY_LNG));

//        Location origin = new Location("");
//        origin.setLatitude(holder.lat1);
//        origin.setLongitude(holder.lng1);

        holder.lat = requestModel.getLat();
        holder.lng = requestModel.getLng();
        holder.lat2 = Double.parseDouble(holder.lat);
        holder.lng2 = Double.parseDouble(holder.lng);

//        Location destination = new Location("");
//        destination.setLatitude(holder.lat2);
//        destination.setLongitude(holder.lng2);

        String jarak = getDistance(holder.lat1, holder.lng1, holder.lat2, holder.lng2);
        holder.lokasi.setText(requestModel.getLokasi() + " (" + jarak + ")");

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
        return requestList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView namaPasien, golDarah, RH, lokasi, tanggal, jml;
        String foto, lat, lng, idUser, jumlah, tampilTanggal, no_hp, keterangan;
        Double lat1, lng1, lat2, lng2;

        SessionManager sm;

        public HolderData (View v){
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(v.getContext(), DetailDonorActivity.class);
                    intent.putExtra("pasien", requestList.get(getAdapterPosition()).getNama_pasien());
                    intent.putExtra("golDar", requestList.get(getAdapterPosition()).getGol_dar());
                    intent.putExtra("rhesus", requestList.get(getAdapterPosition()).getRhesus());
                    intent.putExtra("foto", requestList.get(getAdapterPosition()).getFoto());
                    intent.putExtra("lokasi", requestList.get(getAdapterPosition()).getLokasi());
                    intent.putExtra("lat", requestList.get(getAdapterPosition()).getLat());
                    intent.putExtra("lng", requestList.get(getAdapterPosition()).getLng());
                    intent.putExtra("id_user", requestList.get(getAdapterPosition()).getId_user());
                    intent.putExtra("jumlah", requestList.get(getAdapterPosition()).getJumlah());
                    intent.putExtra("tanggal", tampilTanggal);
                    intent.putExtra("no_hp", no_hp);
                    intent.putExtra("keterangan", keterangan);
                    view.getContext().startActivity(intent);
                }
            });

            namaPasien = v.findViewById(R.id.namaPasien);
            golDarah = v.findViewById(R.id.golDarah);
            RH = v.findViewById(R.id.RH);
            lokasi = v.findViewById(R.id.lokasi);
            tanggal = v.findViewById(R.id.tanggal);
            jml = v.findViewById(R.id.jmlDonor);

            sm = new SessionManager(v.getContext());

        }

    }

    private String getDistance(final double lat1, final double lng1, final double lat2, final double lng2){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1
                                                + "&destination=" + lat2 + "," + lng2 +
                                                    "&mode=driving&key=" + API_KEY);
                    final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    //                    Log.d("RESPONSE", response);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject dist = legs.getJSONObject(0);
                    JSONObject distance = dist.getJSONObject("distance");
                    parsedDistance = distance.getString("text");

                }  catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return parsedDistance;
    }

}
