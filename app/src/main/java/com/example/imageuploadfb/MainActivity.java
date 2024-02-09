package com.example.imageuploadfb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    Button browse, upload;
    Uri filepath;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView)findViewById(R.id.imageView);
        browse = (Button)findViewById(R.id.browse);
        upload = (Button)findViewById(R.id.upload);


             browse.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Dexter.withActivity(MainActivity.this)
                             .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                             .withListener(new PermissionListener() {
                                 @Override
                                 public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                        Intent intent = new Intent(Intent.ACTION_PICK);
                                        intent.setType("image/*");
                                        startActivityForResult(Intent.createChooser(intent, "plsese slect the image"), 1);
                                 }

                                 @Override
                                 public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                 }

                                 @Override
                                 public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                        permissionToken.continuePermissionRequest();
                                 }
                             }).check();

                 }
             });

             upload.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     uploadFirebase();
                 }
             });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == 1 && resultCode == RESULT_OK){
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);

            } catch (Exception ignored){

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadFirebase() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("File Uploder");
        dialog.show();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploder = storage.getReference().child("image1");

        uploder.putFile(filepath)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         dialog.dismiss();
                         Toast.makeText(MainActivity.this, "File Uploded SuccessFully", Toast.LENGTH_SHORT).show();

                     }
                 })
                 .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                         float percent = (float) (100 * snapshot.getBytesTransferred()) /snapshot.getTotalByteCount();
                         dialog.setMessage("Uplodig " + (int)percent + " %");
                     }
                 });
    }

}