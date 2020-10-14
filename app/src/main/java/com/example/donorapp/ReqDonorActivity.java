package com.example.donorapp;

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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReqDonorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
    static final int REQUEST_IMAGE_CAPTURE = 3;
    static final int REQUEST_TAKE_PHOTO = 3;
    String currentPhotoPath;

    EditText etPasien, etHandphone, etLokasi, etJumlah, etKet;
    Spinner spGolDar;
    RadioGroup rGroup;
    RadioButton rButton;
    Button btnKirim;
    ImageButton btnGallery, btnKamera;
    ImageView imageView;

    Bitmap bitmap, rotatedBitmap;
    String gol_darah;
    String rh;

    String id_user;
    String latitude, longitude;

    ProgressDialog progressDialog;
    int REQUEST_GALLERY = 1;
    int AUTOCOMPLETE_REQUEST_CODE = 2;

    String API_KEY = "YOUR_PLACE_API_KEY";

    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_donor);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pencarian Pendonor");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitleTextColor(Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sm = new SessionManager(ReqDonorActivity.this);
        HashMap<String, String> map = sm.getDetailLogin();
        id_user = map.get(sm.KEY_IDUSER);

        etPasien = findViewById(R.id.etPasien);
        etHandphone = findViewById(R.id.etHandphone);
        etKet = findViewById(R.id.etKet);

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Places.initialize(getApplicationContext(), API_KEY);
        etLokasi = findViewById(R.id.etLokasi);
        etLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(ReqDonorActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        etJumlah = findViewById(R.id.etJumlah);
        spGolDar = findViewById(R.id.spGolDar);
        spGolDar.setOnItemSelectedListener(this);

        rGroup = findViewById(R.id.rGroup);

        btnGallery = findViewById(R.id.btnGallery);
        btnKamera = findViewById(R.id.btnKamera);

        btnKirim = findViewById(R.id.btnKirim);

        imageView = findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (bitmap != null && gol_darah != null && rh != null){
                    RequestBody id = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(id_user));

                    RequestBody nama_pasien = RequestBody.create(MediaType.parse("multipart/form-file"), etPasien.getText().toString());
                    RequestBody no_hp = RequestBody.create(MediaType.parse("multipart/form-file"), "+62" + etHandphone.getText().toString());
                    RequestBody lokasi = RequestBody.create(MediaType.parse("multipart/form-file"), etLokasi.getText().toString());
                    RequestBody jumlah = RequestBody.create(MediaType.parse("multipart/form-file"), etJumlah.getText().toString());

                    File file = tempFile(rotatedBitmap);
                    RequestBody reqImage = RequestBody.create(MediaType.parse("multipart/form-file"), file);
                    MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageUpload", file.getName(), reqImage);

                    String golDar = gol_darah;
                    String Rhesus = rh;
                    RequestBody gol_dar = RequestBody.create(MediaType.parse("multipart/form-file"), golDar);
                    RequestBody rhesus = RequestBody.create(MediaType.parse("multipart/form-file"), Rhesus);

                    RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(latitude));
                    RequestBody lng = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(longitude));

                    String keterangan = "";
                    if (etKet.getText().toString().equals("")){
                        keterangan = "";
                    }
                    if (etKet.getText().toString() != null){
                        keterangan = etKet.getText().toString();
                    }

                    RequestBody ket = RequestBody.create(MediaType.parse("multipart/form-file"), keterangan);

                    ApiUser apiUser = Retroserver.getClient().create(ApiUser.class);

                    Call<ResponseModel> call = apiUser.requestDonor(id, nama_pasien, no_hp, lokasi, jumlah,
                            gol_dar, rhesus, partImage, ket, lat, lng);

                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            progressDialog.dismiss();

                            if(response.body() != null) {
                                Boolean error = response.body().getError();

                                if (!error) {
                                    Toast.makeText(ReqDonorActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(ReqDonorActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(ReqDonorActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ReqDonorActivity.this, "Response Body Null", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            progressDialog.dismiss();

                            Toast.makeText(ReqDonorActivity.this, "Terjadi Kesalahan Koneksi..", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ReqDonorActivity.this, "Isi Secara Lengkap..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(ReqDonorActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ReqDonorActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(ReqDonorActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(ReqDonorActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        } else {
            // Permission has already been granted
//            Toast.makeText(ReqDonorActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ReqDonorActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

//    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//        imageView.setImageBitmap(bitmap);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GALLERY) {
                Uri path = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    rotatedBitmap = bitmap;
                    imageView.setImageBitmap(rotatedBitmap);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
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
                imageView.setImageBitmap(rotatedBitmap);
            }

            if (requestCode == AUTOCOMPLETE_REQUEST_CODE){
                Place place = Autocomplete.getPlaceFromIntent(data);

                LatLng latlng = place.getLatLng();

                String lat = String.valueOf(latlng.latitude);
                String lng = String.valueOf(latlng.longitude);

                latitude = lat;
                longitude = lng;

                String lokasi = place.getName().toString();
                etLokasi.setText(lokasi);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void checkedButton(View view) {
        int radioId = rGroup.getCheckedRadioButtonId();
        rButton = findViewById(radioId);
        rh = rButton.getText().toString();

        // Toast.makeText(ReqDonorActivity.this, rh, Toast.LENGTH_SHORT).show();
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
        gol_darah = parent.getSelectedItem().toString();
//        Log.w("GOLDAR", gol_darah);
        // Toast.makeText(ReqDonorActivity.this, gol_darah, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

