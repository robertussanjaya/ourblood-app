package com.example.donorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDonor extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText editPasien, editHandphone, editLokasi, editJumlah, editKet;
    Spinner spinnerEdit;
    RadioGroup radioEdit;
    RadioButton rButton, rPositif, rNegatif;
    Button bEdit;
    ImageButton bGallery, bKamera;
    ImageView imageEdit;
    Bitmap bitmap, rotatedBitmap;

    String golDarah, rh, latitude, longitude;
    String id_request, id_user;
    String currentPhotoPath;
    String API_KEY = "YOUR_PLACE_API_KEY";

    ProgressDialog progressDialog;

    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 5;
    final int REQUEST_TAKE_PHOTO = 1;
    final int REQUEST_GALLERY = 2;
    final int AUTOCOMPLETE_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditRequest);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitleTextColor(Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Edit Permintaan Pendonor");

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Places.initialize(getApplicationContext(), API_KEY);

        editPasien = findViewById(R.id.editPasien);
        editHandphone = findViewById(R.id.editHandphone);
        editKet = findViewById(R.id.editKet);

        editLokasi = findViewById(R.id.editLokasi);
        editLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(EditDonor.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        editJumlah = findViewById(R.id.editJumlah);
        imageEdit = findViewById(R.id.imageEdit);

        spinnerEdit = findViewById(R.id.spinnerEdit);
        spinnerEdit.setOnItemSelectedListener(this);

        radioEdit = findViewById(R.id.radioEdit);
        rPositif = findViewById(R.id.positifEdit);
        rNegatif = findViewById(R.id.negatifEdit);

        bGallery = findViewById(R.id.bGallery);
        bGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        bKamera = findViewById(R.id.bKamera);
        bEdit = findViewById(R.id.bEdit);

        Intent i = getIntent();
        id_request = i.getStringExtra("id_request");
        id_user = i.getStringExtra("id_user");
        latitude = i.getStringExtra("lat");
        longitude = i.getStringExtra("lng");

        editPasien.setText(i.getStringExtra("pasien"));

        String hp = i.getStringExtra("no_hp");
        String phone = hp.replace("+62", "");
        editHandphone.setText(phone);

        editJumlah.setText(i.getStringExtra("jumlah"));
        editLokasi.setText(i.getStringExtra("lokasi"));
        editKet.setText(i.getStringExtra("keterangan"));

        String url = "http://ourblood-admin.my.id/donorAPI/upload/donor/";
        String foto = i.getStringExtra("foto");
        String imageAddress = url + foto;

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
                        imageEdit.setImageBitmap(rotatedBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        golDarah = i.getStringExtra("golDar");
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinnerEdit.getAdapter();
        int spinnerPosition = arrayAdapter.getPosition(golDarah);
        spinnerEdit.setSelection(spinnerPosition);

        rh = i.getStringExtra("rhesus");
        if (rh.equals("Negatif")) {
            rNegatif.setChecked(true);
            rPositif.setChecked(false);
        } else if (rh.equals("Positif")) {
            rNegatif.setChecked(false);
            rPositif.setChecked(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (bitmap != null && golDarah != null && rh != null) {
                    RequestBody idUser = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(id_user));
                    RequestBody idRequest = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(id_request));

                    RequestBody nama_pasien = RequestBody.create(MediaType.parse("multipart/form-file"), editPasien.getText().toString());
                    RequestBody no_hp = RequestBody.create(MediaType.parse("multipart/form-file"), "+62" + editHandphone.getText().toString());
                    RequestBody lokasi = RequestBody.create(MediaType.parse("multipart/form-file"), editLokasi.getText().toString());
                    RequestBody jumlah = RequestBody.create(MediaType.parse("multipart/form-file"), editJumlah.getText().toString());

                    File file = tempFile(rotatedBitmap);
                    RequestBody reqImage = RequestBody.create(MediaType.parse("multipart/form-file"), file);
                    MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageUpload", file.getName(), reqImage);

                    String golDar = golDarah;
                    String Rhesus = rh;
                    RequestBody gol_dar = RequestBody.create(MediaType.parse("multipart/form-file"), golDar);
                    RequestBody rhesus = RequestBody.create(MediaType.parse("multipart/form-file"), Rhesus);

                    RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(latitude));
                    RequestBody lng = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(longitude));

                    String keterangan = "";
                    if (editKet.getText().toString().equals("") || editKet.getText().toString().equals(" ")){
                        keterangan = "";
                    } else if (editKet.getText().toString() != null){
                        keterangan = editKet.getText().toString();
                    }

                    RequestBody ket = RequestBody.create(MediaType.parse("multipart/form-file"), keterangan);

                    ApiUser api = Retroserver.getClient().create(ApiUser.class);
                    Call<ResponseModel> call = api.editDonor(idRequest, idUser, nama_pasien, no_hp, lokasi, jumlah, gol_dar, rhesus,
                                                partImage, ket, lat, lng);
                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.body() != null){
                                Boolean error = response.body().getError();

                                if(!error){
                                    Toast.makeText(EditDonor.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(EditDonor.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.putExtra("halaman", 2);
                                    startActivity(i);
                                } else if(error){
                                    Toast.makeText(EditDonor.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(EditDonor.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EditDonor.this, "Isi Secara Lengkap", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GALLERY){
                Uri path = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    rotatedBitmap = bitmap;
                    imageEdit.setImageBitmap(rotatedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == REQUEST_TAKE_PHOTO){
                //Get file URI
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
                imageEdit.setImageBitmap(rotatedBitmap);
            }

            if (requestCode == AUTOCOMPLETE_REQUEST_CODE){
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
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(EditDonor.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(EditDonor.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(EditDonor.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(EditDonor.this,
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
                    Toast.makeText(EditDonor.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void openCamera(View view){
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        golDarah = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void checkRadio(View view) {
        int radioId = radioEdit.getCheckedRadioButtonId();
        rButton= findViewById(radioId);
        rh = rButton.getText().toString();
    }

}
