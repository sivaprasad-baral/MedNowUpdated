package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mednow.model.Partner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChemistInfo extends AppCompatActivity {

    String partnerUserId;
    Double distance;
    boolean displayInfo = false,activityOpenStatus = true;
    Partner partner;

    TextView textViewPharmacyName,textViewOwnerName,textViewOwnerMobile,textViewOwnerEmail;
    TextView textViewArrivalTime,textViewRating,textViewPharmacyAddress,textViewDistance;
    LinearLayout linearLayoutPartnerInfo;
    ImageView imageViewShowHide,imageViewPharmacyImg;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferencePartners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemist_info);

        partnerUserId = getIntent().getStringExtra("partnerUserId");
        distance = getIntent().getDoubleExtra("distance",0.0);

        textViewPharmacyName = findViewById(R.id.chemist_info_text_view_pharmacy_name);
        textViewOwnerName = findViewById(R.id.chemist_info_text_view_partner_name);
        textViewOwnerMobile = findViewById(R.id.chemist_info_text_view_partner_phone);
        textViewOwnerEmail = findViewById(R.id.chemist_info_text_view_partner_email);
        textViewPharmacyAddress = findViewById(R.id.chemist_info_text_view_pharmacy_address);
        textViewDistance = findViewById(R.id.chemist_info_text_view_pharmacy_distance);
        textViewArrivalTime = findViewById(R.id.chemist_info_text_view_pharmacy_expected_time);
        textViewRating = findViewById(R.id.chemist_info_text_view_pharmacy_rating);
        linearLayoutPartnerInfo = findViewById(R.id.chemist_info_linear_layout_partner_info);
        imageViewPharmacyImg = findViewById(R.id.chemist_info_image_view_pharmacy_img);
        imageViewShowHide = findViewById(R.id.chemist_info_image_view_show_hide);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferencePartners = FirebaseDatabase.getInstance().getReference().child("Partners").child(partnerUserId);
        databaseReferencePartners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(activityOpenStatus) {
                    partner = dataSnapshot.getValue(Partner.class);
                    if(partner.getPharmacyImg() == null) {
                        Glide.with(ChemistInfo.this).load(R.drawable.image_not_available).into(imageViewPharmacyImg);
                    } else {
                        Glide.with(ChemistInfo.this).load(Uri.parse(partner.getPharmacyImg())).into(imageViewPharmacyImg);
                    }
                    textViewPharmacyName.setText(Objects.requireNonNull(partner).getPharmacyName());
                    String label = "Name : ";
                    textViewOwnerName.setText(label.concat(partner.getName()));
                    if(partner.getPhone() == null) {
                        label = "Mobile No : N/A";
                        textViewOwnerMobile.setText(label);
                    } else {
                        label = "Mobile No : ";
                        textViewOwnerMobile.setText(label.concat(partner.getPhone()));
                    }
                    label = "Email : ";
                    textViewOwnerEmail.setText(label.concat(partner.getEmail()));
                    try {
                        Geocoder geocoder = new Geocoder(ChemistInfo.this, Locale.getDefault());
                        List<Address> addresses;
                        addresses = geocoder.getFromLocation(partner.getLatitude(),partner.getLongitude(),1);
                        String address = addresses.get(0).getAddressLine(0);
                        textViewPharmacyAddress.setText(address);
                    } catch (IOException e) {
                        Toast.makeText(ChemistInfo.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    label = "Distance : "+distance+" km";
                    textViewDistance.setText(label);
                    int time = (int) (distance*1000/1.39);
                    int min = time/60;
                    int sec = time%60;
                    label = "Expected Time : "+min+" min "+sec+" sec";
                    textViewArrivalTime.setText(label);
                    label = "Rating : "+partner.getRating();
                    textViewRating.setText(label);
                } else {
                    databaseReferencePartners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChemistInfo.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chatBtn(View view) {
        if(partner.isAvailable()) {
            startActivity(new Intent(ChemistInfo.this,ChemistChat.class).putExtra("partnerUserId",partnerUserId));
        } else {
            Toast.makeText(ChemistInfo.this,"Currently not receiving orders",Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadBtn(View view) {
        if(partner.isAvailable()) {
            startActivity(new Intent(ChemistInfo.this,UploadPrescription.class).putExtra("partnerUserId",partnerUserId));
        } else {
            Toast.makeText(ChemistInfo.this,"Currently not receiving orders",Toast.LENGTH_SHORT).show();
        }
    }

    public void showHideBtn(View view) {
        if(displayInfo) {
            linearLayoutPartnerInfo.setVisibility(View.GONE);
            imageViewShowHide.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            displayInfo = false;
        } else {
            linearLayoutPartnerInfo.setVisibility(View.VISIBLE);
            imageViewShowHide.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            displayInfo = true;
        }
    }

    @Override
    public void onBackPressed() {
        activityOpenStatus = false;
        startActivity(new Intent(ChemistInfo.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
