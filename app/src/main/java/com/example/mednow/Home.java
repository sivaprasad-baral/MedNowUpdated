package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView textViewName;
    CircleImageView imageViewProfileImg;
    View viewNavHeader;

    NavController navController;
    AppBarConfiguration appBarConfiguration;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar((Toolbar) findViewById(R.id.home_toolbar));
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        bottomNavigationView = findViewById(R.id.home_bottom_nav_view);
        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.home_navigation_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewNavHeader = navigationView.getHeaderView(0);
        textViewName = viewNavHeader.findViewById(R.id.home_header_text_view_name);
        imageViewProfileImg = viewNavHeader.findViewById(R.id.home_header_image_view_profile_image);
        textViewName.setText(firebaseUser.getDisplayName());
        if(firebaseUser.getPhotoUrl() != null) {
            Glide.with(Home.this).load(firebaseUser.getPhotoUrl()).into(imageViewProfileImg);
        } else {
            Glide.with(Home.this).load(R.drawable.ic_person_black_24dp).into(imageViewProfileImg);
        }

        navController = Navigation.findNavController(Home.this,R.id.home_nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment,R.id.historyFragment,R.id.walletFragment).setDrawerLayout(drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(Home.this,navController,appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView,navController);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.refer_menu:
                        // startActivity(new Intent(Home.this,Referral.class));
                        break;
                    case R.id.profile_menu:
                         startActivity(new Intent(Home.this, Profile.class));
                        break;
                    case R.id.gifts_menu:
                        // startActivity(new Intent(Home.this,Gifts.class));
                        break;
                    case R.id.help_menu:
                        startActivity(new Intent(Home.this,Help.class));
                        break;
                    case R.id.about_menu:
                        // startActivity(new Intent(Home.this,About.class));
                        break;
                    case R.id.rate_menu:
                        break;
                    case R.id.logout_menu:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Home.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,appBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
