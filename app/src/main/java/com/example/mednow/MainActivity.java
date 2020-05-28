package com.example.mednow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewLogo;
    TextView textViewAppName, textViewTagLine;
    LottieAnimationView lottieNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageViewLogo = findViewById(R.id.main_image_view_logo);
        textViewAppName = findViewById(R.id.main_text_view_app_name);
        textViewTagLine = findViewById(R.id.main_text_view_tag_line);
        lottieNoInternet = findViewById(R.id.main_lottie_no_internet);

        imageViewLogo.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_top));
        textViewAppName.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_bottom));
        textViewTagLine.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_bottom));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isConnected()) {
                    if(isLoggedIn()) {
                        startActivity(new Intent(MainActivity.this,Location.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        startActivity(new Intent(MainActivity.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                } else {
                    imageViewLogo.setVisibility(View.GONE);
                    textViewAppName.setVisibility(View.GONE);
                    textViewTagLine.setVisibility(View.GONE);
                    lottieNoInternet.setVisibility(View.VISIBLE);
                }
            }
        },3000);
    }

    public boolean isConnected() {
        return ((ConnectivityManager) Objects.requireNonNull(getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }

    public boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}