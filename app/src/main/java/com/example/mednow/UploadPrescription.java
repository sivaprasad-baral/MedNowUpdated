package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mednow.dialog.UploadDialog;
import com.example.mednow.model.Prescription;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;
import java.util.UUID;

public class UploadPrescription extends AppCompatActivity {

    String chemistUserId;
    Uri filePath;

    ImageView imageViewPrescription;
    UploadDialog uploadDialog;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);

        imageViewPrescription = findViewById(R.id.upload_image_view_prescription);
        uploadDialog = new UploadDialog(UploadPrescription.this);

        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(UploadPrescription.this);

        chemistUserId = getIntent().getStringExtra("partnerUserId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescriptions");
        storageReference = FirebaseStorage.getInstance().getReference("Prescriptions/" + UUID.randomUUID().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filePath = result.getUri();
            Glide.with(UploadPrescription.this).load(filePath).into(imageViewPrescription);
        } else {
            finish();
            Toast.makeText(UploadPrescription.this,"No image detected",Toast.LENGTH_SHORT).show();
        }
    }

    public void sendBtn(View view) {
        if(filePath != null) {
            uploadDialog.startUploadDialog();
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String prescriptionId = databaseReference.push().getKey();
                            Prescription prescription = new Prescription(firebaseUser.getUid(),chemistUserId,uri.toString(),prescriptionId);
                            databaseReference.child(Objects.requireNonNull(prescriptionId)).setValue(prescription);
                            uploadDialog.setUploadSuccess("Prescription Upload Successful");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadDialog.dismissDialog();
                                }
                            },4000);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    uploadDialog.setUploadProgress(progress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadDialog.setUploadFailure("Prescription Upload Failed");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadDialog.dismissDialog();
                        }
                    },4000);
                }
            });
        } else {
            finish();
            Toast.makeText(UploadPrescription.this,"No image detected",Toast.LENGTH_SHORT).show();
        }
    }
}
