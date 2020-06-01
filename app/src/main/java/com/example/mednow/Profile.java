package com.example.mednow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    FirebaseUser firebaseUser;

    TextView userProfileName,userProfileEmail;
    CircleImageView imageViewUserProfileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userProfileName = findViewById(R.id.profile_activity_user_profile_name);
        userProfileEmail = findViewById(R.id.prifile_activity_user_profile_email);
        imageViewUserProfileImg = findViewById(R.id.profile_activity_user_profile_pic);

        userProfileName.setText(firebaseUser.getDisplayName());
        userProfileEmail.setText(firebaseUser.getEmail());
        if(firebaseUser.getPhotoUrl() != null) {
            Glide.with(Profile.this).load(firebaseUser.getPhotoUrl()).into(imageViewUserProfileImg);
        } else {
            Glide.with(Profile.this).load(R.drawable.image_not_available).into(imageViewUserProfileImg);
        }

    }

    public void profileLogOutBtn(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Profile.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
