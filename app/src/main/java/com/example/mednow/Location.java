package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mednow.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;

public class Location extends AppCompatActivity implements GoogleMap.OnMapClickListener {

    Double latitude,longitude;
    String labelText;
    GoogleMap gMap;

    SupportMapFragment supportMapFragment;
    TextView textViewMsg;
    Button buttonRetry,buttonConfirm;
    LottieAnimationView lottieAnimationViewGpsFailure;
    // CustomLoadingDialog loadingDialog;

    FusedLocationProviderClient client;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_fragment_map);
        textViewMsg = findViewById(R.id.location_text_view_msg);
        buttonRetry = findViewById(R.id.location_button_retry);
        buttonConfirm = findViewById(R.id.location_button_confirm);
        lottieAnimationViewGpsFailure = findViewById(R.id.location_lottie_gps_failure);
        // loadingDialog = new CustomLoadingDialog(Location.this);

        client = LocationServices.getFusedLocationProviderClient(Location.this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        checkPermission();
    }

    private void getCurrentLocation() {
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(final android.location.Location location) {
                if(location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.clear();
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                            gMap = googleMap;
                            gMap.setOnMapClickListener(Location.this);
                        }
                    });
                } else {
                    labelText = "Reconfigure settings and try again";
                    textViewMsg.setText(labelText);
                    supportMapFragment.requireView().setVisibility(View.GONE);
                    lottieAnimationViewGpsFailure.setVisibility(View.VISIBLE);
                    buttonConfirm.setVisibility(View.GONE);
                    labelText = "Retry";
                    buttonRetry.setText(labelText);
                }
            }
        });
    }

    public void checkPermission() {
        Dexter.withContext(Location.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                labelText = "Confirm your location";
                textViewMsg.setText(labelText);
                supportMapFragment.requireView().setVisibility(View.VISIBLE);
                lottieAnimationViewGpsFailure.setVisibility(View.GONE);
                buttonConfirm.setVisibility(View.VISIBLE);
                labelText = "Relocate";
                buttonRetry.setText(labelText);
                getCurrentLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                labelText = "Allow location permissions and try again";
                textViewMsg.setText(labelText);
                supportMapFragment.requireView().setVisibility(View.GONE);
                lottieAnimationViewGpsFailure.setVisibility(View.VISIBLE);
                buttonConfirm.setVisibility(View.GONE);
                labelText = "Retry";
                buttonRetry.setText(labelText);
                firebaseUser.delete();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public void confirmBtn(View view) {
        // loadingDialog.startLoadingDialog();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                databaseReference.removeEventListener(this);
                Objects.requireNonNull(user).setLatitude(latitude);
                user.setLongitude(longitude);
                databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // loadingDialog.dismissDialog();
                        if(task.isSuccessful()) {
                            startActivity(new Intent(Location.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        } else {
                            Toast.makeText(Location.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // loadingDialog.dismissDialog();
                Toast.makeText(Location.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retryBtn(View view) {
        checkPermission();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        gMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        gMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
        Toast.makeText(Location.this,"Location changed",Toast.LENGTH_SHORT).show();
    }
}
