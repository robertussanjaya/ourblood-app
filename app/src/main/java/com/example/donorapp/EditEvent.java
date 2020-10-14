package com.example.donorapp;

import androidx.annotation.NonNull;
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
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.ResponseModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEvent extends AppCompatActivity {
    TextView editJudul, editLokasi, editTanggal, editWaktuMulai,
            editWaktuSelesai, editKet;
    ImageView imageEditEvent;
    ImageButton btGallery;
    Button btnEdit;
    String idUser, idEvent, latitude, longitude;

    String tglBaru, currentPhotoPath;
    Bitmap bitmap, rotatedBitmap;

    String API_KEY = "YOUR_PLACE_API_KEY";
    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 5;
    final int REQUEST_LOKASI = 1, REQUEST_TAKE_PHOTO = 2, REQUEST_GALLERY = 3;

    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarEditEvent);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Edit Event");

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Places.initialize(getApplicationContext(), API_KEY);

        Intent i = getIntent();

        editJudul = findViewById(R.id.editJudulEvent);
        editJudul.setText(i.getStringExtra("judul"));

        editLokasi = findViewById(R.id.editLokasiEvent);
        editLokasi.setText(i.getStringExtra("lokasi"));
        editLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                                .build(EditEvent.this);
                startActivityForResult(i, REQUEST_LOKASI);
            }
        });

        editTanggal = findViewById(R.id.editTgl);
        String tglDB = i.getStringExtra("tanggal");
        tglBaru = tglDB;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf.parse(tglDB);
            Locale lokal = new Locale("in", "ID");
            SimpleDateFormat tglTampil = new SimpleDateFormat("EEEE, dd MMM yyyy", lokal);
            String tgl = tglTampil.format(date);
            editTanggal.setText(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                ubahFormatTanggal();
            }
        };
        editTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditEvent.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editWaktuMulai = findViewById(R.id.editWaktuMulai);
        editWaktuMulai.setText(i.getStringExtra("mulai"));
        editWaktuMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar waktuMulai = Calendar.getInstance();
                int jam = waktuMulai.get(Calendar.HOUR_OF_DAY);
                int menit = waktuMulai.get(Calendar.MINUTE);

                TimePickerDialog dialog;
                dialog = new TimePickerDialog(EditEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editWaktuMulai.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, jam, menit, true);
                dialog.setTitle("Waktu Mulai");
                dialog.show();
            }
        });

        editWaktuSelesai = findViewById(R.id.editWaktuSelesai);
        editWaktuSelesai.setText(i.getStringExtra("selesai"));
        editWaktuSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar waktuSelesai = Calendar.getInstance();
                int jam = waktuSelesai.get(Calendar.HOUR_OF_DAY);
                int menit = waktuSelesai.get(Calendar.MINUTE);

                TimePickerDialog dialog;
                dialog = new TimePickerDialog(EditEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editWaktuSelesai.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, jam, menit, true);
                dialog.setTitle("Waktu Selesai");
                dialog.show();
            }
        });

        editKet = findViewById(R.id.editKetEvent);
        editKet.setText(i.getStringExtra("keterangan"));

        imageEditEvent = findViewById(R.id.imageEditEvent);
        String url = "http://ourblood-admin.my.id/donorAPI/upload/event/";
        String image = i.getStringExtra("foto");
        String imageAddress = url + image;
        Glide.with(this)
                .asBitmap()
                .load(imageAddress)
                .override(1600, 1600)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        File file = tempFile(resource);
                        currentPhotoPath = file.getAbsolutePath();
                        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        rotatedBitmap = bitmap;
                        imageEditEvent.setImageBitmap(rotatedBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        btGallery = findViewById(R.id.btGalleryEdit);
        btGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnEdit = findViewById(R.id.btnEditEvent);

        idUser = i.getStringExtra("id_user");
        idEvent = i.getStringExtra("id_event");
        latitude = i.getStringExtra("lat");
        longitude = i.getStringExtra("lng");

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading..");

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                if (bitmap != null && tglDB != null) {
                    RequestBody id_event = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(idEvent));
                    RequestBody id_user = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(idUser));
                    RequestBody nama_event = RequestBody.create(MediaType.parse("multipart/form-file"), editJudul.getText().toString());
                    RequestBody lokasi = RequestBody.create(MediaType.parse("multipart/form-file"), editLokasi.getText().toString());
                    RequestBody tgl = RequestBody.create(MediaType.parse("multipart/form-file"), tglBaru);
                    RequestBody waktuMulai = RequestBody.create(MediaType.parse("multipart/form-file"), editWaktuMulai.getText().toString());
                    RequestBody waktu_selesai = RequestBody.create(MediaType.parse("multipart/form-file"), editWaktuSelesai.getText().toString());

                    File file = tempFile(rotatedBitmap);
                    RequestBody upload = RequestBody.create(MediaType.parse("multipart/form-file"), file);
                    MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageUpload", file.getName(), upload);

                    RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(latitude));
                    RequestBody lng = RequestBody.create(MediaType.parse("multipart.form-file"), String.valueOf(longitude));

                    String info_tambah = "";
                    if (editKet.getText().toString().equals("")) {
                        info_tambah = "";
                    } else if (editKet.getText().toString() != null) {
                        info_tambah = editKet.getText().toString();
                    }

                    RequestBody keterangan = RequestBody.create(MediaType.parse("multipart/form-file"), info_tambah);

                    ApiUser api = Retroserver.getClient().create(ApiUser.class);
                    Call<ResponseModel> call = api.editEvent(id_event, id_user, nama_event, lokasi, tgl, waktuMulai, waktu_selesai,
                                                    keterangan, partImage, lat, lng);
                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                            pd.dismiss();
                            if (response.body() != null){
                                Boolean error = response.body().getError();

                                if(!error){
                                    Toast.makeText(EditEvent.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(EditEvent.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.putExtra("halaman", 2);
                                    startActivity(i);
                                } else if(error){
                                    Toast.makeText(EditEvent.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            pd.dismiss();
                            Toast.makeText(EditEvent.this, "Terjadi Kesalahan..", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    pd.dismiss();
                    Toast.makeText(EditEvent.this, "Isi Secara Lengkap", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                rotatedBitmap = bitmap;
                imageEditEvent.setImageBitmap(rotatedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            imageEditEvent.setImageBitmap(rotatedBitmap);
        }

        if (requestCode == REQUEST_LOKASI){
            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng latLng = place.getLatLng();

            String lat = String.valueOf(latLng.latitude);
            String lng = String.valueOf(latLng.longitude);

            latitude = lat;
            longitude = lng;
            String lokasi = place.getName().toString();
            editLokasi.setText(lokasi);
        }

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private File tempFile(Bitmap bitmap){
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + "_image.webp");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return file;
    }

    public void ubahFormatTanggal(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tglBaru = sdf.format(calendar.getTime());
        Locale lokal = new Locale("in", "ID");
        String formatTampil = "EEEE, dd MMM yyyy";
        SimpleDateFormat tampil = new SimpleDateFormat(formatTampil, lokal);
        editTanggal.setText(tampil.format(calendar.getTime()));
    }

    private void checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(EditEvent.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(EditEvent.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(EditEvent.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(EditEvent.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        } else {
            // Permission has already been granted
//            Toast.makeText(EditDonor.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            openCameraToCapture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    openCameraToCapture();
                } else {
                    Toast.makeText(EditEvent.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void bukaKameraEdit(View view){
        //Check permission
        checkPermissionStorage();
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

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }
}
