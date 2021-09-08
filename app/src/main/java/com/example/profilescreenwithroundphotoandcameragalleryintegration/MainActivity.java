package com.example.profilescreenwithroundphotoandcameragalleryintegration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageviewProfile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            chooseProfilePicture();
            }
        });
    }
    private void chooseProfilePicture(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.alert_profile_picture,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        ImageView imageViewAddCamera=dialogView.findViewById(R.id.imageviewAlertPic);
        ImageView imageViewAddGallery=dialogView.findViewById(R.id.imageviewAlertPicGallery);

        final AlertDialog alertDialog =builder.create();
        alertDialog.show();

        imageViewAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    takePictureFromCamera();
                    alertDialog.cancel();
                }
            }
        });
        imageViewAddGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            takePictureFromGallery();
            alertDialog.cancel();
            }
        });
    }
    private void takePictureFromGallery(){
        Intent pickPhoto= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto,1);
    }
    private void takePictureFromCamera(){
        Intent capturePhoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (capturePhoto.resolveActivity(getPackageManager())!=null);
        startActivityForResult(capturePhoto,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    Uri selectImageUri=data.getData();
                    imageView.setImageURI(selectImageUri);
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    Bitmap bitmapImage= (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmapImage);

                }
        }
    }
    private boolean checkPermissions(){
    if (Build.VERSION.SDK_INT>=23){
        int cameraPermission= ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if (cameraPermission== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},20);
            return false;
        }
    }
    return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==20 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            takePictureFromCamera();
        }
        else
            Toast.makeText(MainActivity.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
    }
}