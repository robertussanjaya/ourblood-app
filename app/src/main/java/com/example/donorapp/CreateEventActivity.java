package com.example.donorapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.ResponseModel;
import com.example.donorapp.util.SessionManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;
    EditText namaEvent, etLokasi, tanggal, waktu, waktuSelesai, ket;

    ImageView fotoEvent;
    Bitmap bitmap, rotatedBitmap;

    String tglDB, idUser, latitude, longitude;

    Button btnTambah;
    ImageButton btnUpload;

    String API_KEY = "YOUR_PLACE_API_KEY";
    int REQUEST_UPLOAD = 1;
    int REQUEST_LOKASI = 2;
    int REQUEST_TAKE_PHOTO = 3;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 4;

    String currentPhotoPath;

    SessionManager sm;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarEvent);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Buat Event Baru");

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Places.initialize(getApplicationContext(), API_KEY);

        sm = new SessionManager(CreateEventActivity.this);
        HashMap<String, String> map = sm.getDetailLogin();
        idUser = map.get(sm.KEY_IDUSER);

        namaEvent = findViewById(R.id.etEvent);

        etLokasi = findViewById(R.id.etLokasi);
        etLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(CreateEventActivity.this);
                startActivityForResult(intent, REQUEST_LOKASI);
            }
        });

        ket = findViewById(R.id.ket);
        fotoEvent = findViewById(R.id.imageEvent);

        btnUpload = findViewById(R.id.btUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { selectImage(); }
        });

        tanggal = findViewById(R.id.tgl);
        calendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                ubahTanggal();
            }
        };
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateEventActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        waktu = findViewById(R.id.wktMulai);
        waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int jam = currentTime.get(Calendar.HOUR_OF_DAY);
                int menit = currentTime.get(Calendar.MINUTE);

                TimePickerDialog dialog;
                dialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        waktu.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, jam, menit, true);
                dialog.setTitle("Waktu Mulai");
                dialog.show();
            }
        });
        waktuSelesai = findViewById(R.id.wktSelesai);
        waktuSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int jam = currentTime.get(Calendar.HOUR_OF_DAY);
                int menit = currentTime.get(Calendar.MINUTE);

                TimePickerDialog dialog;
                dialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        waktuSelesai.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, jam, menit, true);
                dialog.setTitle("Waktu Mulai");
                dialog.show();
            }
        });

        pd = new ProgressDialog(CreateEventActivity.this);
        pd.setMessage("Loading..");

        btnTambah = findViewById(R.id.addEvent);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                if (bitmap != null && tglDB != null){
                    RequestBody id_user = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(idUser));
                    RequestBody nama_event = RequestBody.create(MediaType.parse("multipart/form-file"), namaEvent.getText().toString());
                    RequestBody lokasi = RequestBody.create(MediaType.parse("multipart/form-file"), etLokasi.getText().toString());
                    RequestBody tgl = RequestBody.create(MediaType.parse("multipart/form-file"), tglDB);
                    RequestBody waktuMulai = RequestBody.create(MediaType.parse("multipart/form-file"), waktu.getText().toString());
                    RequestBody waktu_selesai = RequestBody.create(MediaType.parse("multipart/form-file"), waktuSelesai.getText().toString());

                    File file = tempFile(rotatedBitmap);
                    RequestBody upload = RequestBody.create(MediaType.parse("multipart/form-file"), file);
                    MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageUpload", file.getName(), upload);

                    RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(latitude));
                    RequestBody lng = RequestBody.create(MediaType.parse("multipart.form-file"), String.valueOf(longitude));

                    String info_tambah = "";
                    if (ket.getText().toString().equals("")){
                        info_tambah = "";
                    } else if (ket.getText().toString() != null){
                        info_tambah = ket.getText().toString();
                    }

                    RequestBody keterangan = RequestBody.create(MediaType.parse("multipart/form-file"), info_tambah);

                    ApiUser api = Retroserver.getClient().create(ApiUser.class);
                    Call<ResponseModel> call = api.createEvent(id_user, nama_event, lokasi, tgl, waktuMulai, waktu_selesai
                            ,keterangan, partImage, lat, lng);
                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            pd.dismiss();

                            if (response.body() != null){
                                Boolean err = response.body().getError();
                                if (!err){
                                    Toast.makeText(CreateEventActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(CreateEventActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.putExtra("halaman", 1);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(CreateEventActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CreateEventActivity.this, "Respose Body Kosong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            Toast.makeText(CreateEventActivity.this, "Terjadi Kesalahan Koneksi..", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(CreateEventActivity.this, "Isi Secara Lengkap..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_UPLOAD){
                Uri path = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    rotatedBitmap = bitmap;
                    fotoEvent.setImageBitmap(rotatedBitmap);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            if (requestCode == REQUEST_LOKASI){
                Place place = Autocomplete.getPlaceFromIntent(data);

                LatLng latLng = place.getLatLng();

                String lat = String.valueOf(latLng.latitude);
                String lng = String.valueOf(latLng.longitude);

                latitude = lat;
                longitude = lng;

                String lokasi = place.getName();
                etLokasi.setText(lokasi);
            }

            if (requestCode == REQUEST_TAKE_PHOTO){
                File file = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(file);
                bitmap = BitmapFactory.decodeFile(currentPhotoPath);

                try {
                    ExifInterface ei = new ExifInterface(currentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                            ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;
                         case ExifInterface.ORIENTATION_NORMAL:
                             default:
                                 rotatedBitmap = bitmap;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fotoEvent.setImageBitmap(rotatedBitmap);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void ubahTanggal(){
        SimpleDateFormat formatDB = new SimpleDateFormat("dd/MM/yyyy");
        tglDB = formatDB.format(calendar.getTime());
        Locale locale = new Locale("in", "ID");
        String formatTanggal = "EEEE, dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatTanggal, locale);
        tanggal.setText(simpleDateFormat.format(calendar.getTime()));
    }


    private void selectImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, REQUEST_UPLOAD);
    }

    private File tempFile(Bitmap bitmap){
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            System.currentTimeMillis() + "_image.webp");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return file;
    }

    public void bukaKamera(View view) {
        checkPermissionStorage();
    }

    private void checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(CreateEventActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateEventActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(CreateEventActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(CreateEventActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        } else {
            // Permission has already been granted
            //Toast.makeText(CreateEventActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            openCameraToCapture();
        }
    }

    private void openCameraToCapture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ourblood",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
