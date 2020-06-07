package com.example.mednow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    FirebaseUser firebaseUser;

    TextView textViewName,textViewEmail,textViewEdit,textViewSave;
    CircleImageView imageViewUserProfileImg;
    EditText editTextName;
    ImageView imageViewCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        textViewName = findViewById(R.id.profile_activity_user_profile_name);
        textViewEmail = findViewById(R.id.profile_activity_user_profile_email);
        imageViewUserProfileImg = findViewById(R.id.profile_activity_user_profile_pic);
        editTextName = findViewById(R.id.profile_edit_text_user_name);
        imageViewCamera = findViewById(R.id.profile_image_upload);
        textViewEdit = findViewById(R.id.profile_text_edit_btn);
        textViewSave = findViewById(R.id.profile_text_save_btn);

        textViewName.setText(firebaseUser.getDisplayName());
        editTextName.setText(firebaseUser.getDisplayName());
        textViewEmail.setText(firebaseUser.getEmail());
        if(firebaseUser.getPhotoUrl() != null) {
            Glide.with(Profile.this).load(firebaseUser.getPhotoUrl()).into(imageViewUserProfileImg);
        } else {
            Glide.with(Profile.this).load(R.drawable.ic_person_black_24dp).into(imageViewUserProfileImg);
        }
    }

    public void profileLogOutBtn(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Profile.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void editProfileBtn(View view) {
        textViewName.setVisibility(View.GONE);
        editTextName.setVisibility(View.VISIBLE);
        imageViewCamera.setVisibility(View.VISIBLE);
        textViewEdit.setVisibility(View.GONE);
        textViewSave.setVisibility(View.VISIBLE);
    }

    public void saveProfileBtn(View view) {
        textViewName.setVisibility(View.VISIBLE);
        editTextName.setVisibility(View.GONE);
        imageViewCamera.setVisibility(View.GONE);
        textViewEdit.setVisibility(View.VISIBLE);
        textViewSave.setVisibility(View.GONE);
    }
}
