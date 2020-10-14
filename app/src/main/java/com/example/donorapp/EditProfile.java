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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.donorapp.api.ApiUser;
import com.example.donorapp.api.Retroserver;
import com.example.donorapp.model.ResponseModel;
import com.example.donorapp.model.UserModel;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {
    EditText editUsername, editEmail, editTelepon, editAlamat, editOldPass, editPass;
    CircleImageView fotoProfil;
    ImageButton bukaKamera, bukaGallery;

    Button btnSimpan;

    Bitmap bitmap, rotatedBitmap;
    String currentPhotoPath;
    String id_user, latitude, longitude;

    private final int REQUEST_LOKASI = 1, REQUEST_TAKE_PHOTO = 2, REQUEST_GALLERY = 3;
    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 4;
    String API_GOOGLE = "YOUR_PLACE_API";

    SessionManager sm;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditProfil);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Edit Profil");

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Places.initialize(getApplicationContext(), API_GOOGLE);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editTelepon = findViewById(R.id.editTelepon);

        editAlamat = findViewById(R.id.editAlamat);
        editAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                                .build(EditProfile.this);
                startActivityForResult(i, REQUEST_LOKASI);
            }
        });

        editOldPass = findViewById(R.id.editOldPassword);
        editPass = findViewById(R.id.editPassword);
        fotoProfil = findViewById(R.id.fotoProfil);

        btnSimpan = findViewById(R.id.btnSimpan);

        bukaKamera = findViewById(R.id.bukaKamera);
        bukaGallery = findViewById(R.id.bukaGallery);
        bukaGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        sm = new SessionManager(EditProfile.this);
        HashMap<String, String> map = sm.getDetailLogin();

        editUsername.setText(map.get(sm.KEY_USERNAME));
        editEmail.setText(map.get(sm.KEY_EMAIL));
        String telepon = map.get(sm.KEY_TELEPON);
        String phone = telepon.replace("+62", "");
        editTelepon.setText(phone);
        editAlamat.setText(map.get(sm.KEY_ALAMAT));

        String url = "http://ourblood-admin.my.id/donorAPI/upload/user/";
        String image = map.get(sm.KEY_FOTO);
        String imagePath = url + image;

        Glide.with(this)
                .asBitmap()
                .load(imagePath)
                .override(1600, 1600)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        File file = tempFile(resource);
                        currentPhotoPath = file.getAbsolutePath();
                        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        rotatedBitmap = bitmap;
                        fotoProfil.setImageBitmap(rotatedBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        id_user = map.get(sm.KEY_IDUSER);
        latitude = map.get(sm.KEY_LAT);
        longitude = map.get(sm.KEY_LNG);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading..");

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                RequestBody id = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(id_user));
                RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(latitude));
                RequestBody lng = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(longitude));

                RequestBody username = RequestBody.create(MediaType.parse("multipart/form-file"), editUsername.getText().toString());
                RequestBody email = RequestBody.create(MediaType.parse("multipart/form-file"), editEmail.getText().toString());
                RequestBody noHP = RequestBody.create(MediaType.parse("multipart/form-file"), "+62" + editTelepon.getText().toString());
                RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-file"), editAlamat.getText().toString());

                RequestBody passwordLama = RequestBody.create(MediaType.parse("multipart/form-file"), editOldPass.getText().toString());
                RequestBody passwordBaru = RequestBody.create(MediaType.parse("multipart/form-file"), editPass.getText().toString());

                File file = tempFile(rotatedBitmap);
                RequestBody upload = RequestBody.create(MediaType.parse("multipart/form-file"), file);
                MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageUpload", file.getName(), upload);

                ApiUser api = Retroserver.getClient().create(ApiUser.class);
                Call<ResponseModel> call = api.editProfil(id, username, email, passwordLama,
                                                passwordBaru, noHP, alamat, partImage, lat, lng);
                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        pd.dismiss();

                        if (!response.body().getError()){

                            SessionManager sm = new SessionManager(EditProfile.this);
                            List<UserModel> userModel = response.body().getResult();
                            sm.storeEdit(userModel.get(0).getId_user(), userModel.get(0).getUsername(), userModel.get(0).getEmail(),
                                    userModel.get(0).getFoto(), userModel.get(0).getLat(), userModel.get(0).getLng(),
                                    userModel.get(0).getTelepon(), userModel.get(0).getAlamat());

                            Toast.makeText(EditProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(EditProfile.this, MainActivity.class);
                            i.putExtra("halaman", 2);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        } else if (response.body().getError()){
                            Toast.makeText(EditProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_LOKASI){

                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng = place.getLatLng();

                String lat = String.valueOf(latLng.latitude);
                String lng = String.valueOf(latLng.longitude);

                latitude = lat;
                longitude = lng;
                String lokasi = place.getName().toString();
                editAlamat.setText(lokasi);

            }

            if (requestCode == REQUEST_TAKE_PHOTO) {

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
                fotoProfil.setImageBitmap(rotatedBitmap);

            }

            if (requestCode == REQUEST_GALLERY) {

                Uri path = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    rotatedBitmap = bitmap;
                    fotoProfil.setImageBitmap(rotatedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        if (ContextCompat.checkSelfPermission(EditProfile.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfile.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(EditProfile.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(EditProfile.this,
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
                    Toast.makeText(EditProfile.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void btnKamera(View view){
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
}
