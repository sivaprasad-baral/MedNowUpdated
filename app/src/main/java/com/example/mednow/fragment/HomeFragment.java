package com.example.mednow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.mednow.ChemistInfo;
import com.example.mednow.R;
import com.example.mednow.adapter.ChemistAdapter;
import com.example.mednow.adapter.ViewPagerAdapter;
import com.example.mednow.model.Partner;
import com.example.mednow.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements ChemistAdapter.PharmacyClickListener {

    private ArrayList<Partner> partners;
    private Double distance;
    private User user;
    private static DecimalFormat distanceFormat = new DecimalFormat("#.##");

    private RecyclerView recyclerView;
    private TextView textViewStores;
    private EditText editTextSearch;
    private ChemistAdapter chemistAdapter;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceUsers,databaseReferencePartners;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        textViewStores = view.findViewById(R.id.home_fragment_text_view_stores);
        editTextSearch = view.findViewById(R.id.home_fragment_edit_text_search);
        recyclerView = view.findViewById(R.id.home_fragment_recycler_view);
        ViewPager viewPager = view.findViewById(R.id.home_fragment_view_pager);

        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(container).getContext()));
        chemistAdapter = new ChemistAdapter(container.getContext(),new ArrayList<Partner>(),HomeFragment.this);
        recyclerView.setAdapter(chemistAdapter);

        viewPager.setAdapter(new ViewPagerAdapter(container.getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        databaseReferencePartners = FirebaseDatabase.getInstance().getReference().child("Partners");
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                databaseReferenceUsers.removeEventListener(this);
                getClosestPharmacy(container);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(container.getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chemistAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void getClosestPharmacy(final ViewGroup container) {
        partners = new ArrayList<>();
        databaseReferencePartners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Partner partner = snapshot.getValue(Partner.class);
                    if(!firebaseUser.getUid().equals(Objects.requireNonNull(partner).getUserId())) {
                        calculateDistance(user.getLatitude(),user.getLongitude(),partner.getLatitude(),partner.getLongitude());
                        if(distance <= 1.0) {
                            int c = 0;
                            for(Partner p : partners) {
                                if(p.getUserId().equals(partner.getUserId())) {
                                    c++;
                                }
                            }
                            if(c == 0) {
                                partners.add(partner);
                            }
                        }
                    }
                }
                if(!partners.isEmpty()) {
                    chemistAdapter = new ChemistAdapter(container.getContext(),partners,HomeFragment.this);
                    recyclerView.setAdapter(chemistAdapter);
                    String storeText = "Stores near you";
                    textViewStores.setText(storeText);
                } else {
                    String storeText = "No stores available near to you";
                    textViewStores.setText(storeText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(container.getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDistance(double userLatitude, double userLongitude, double chemistLatitude, double chemistLongitude) {
        double theta = userLongitude - chemistLongitude;
        distance = Math.sin(deg2rad(userLatitude))
                * Math.sin(deg2rad(chemistLatitude))
                + Math.cos(deg2rad(userLatitude))
                * Math.cos(deg2rad(chemistLatitude))
                * Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = (distance * 60 * 1.1515) / 0.62137;
        distance = Double.parseDouble(distanceFormat.format(distance));
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onPharmacyClick(int position) {
        startActivity(new Intent(getActivity(), ChemistInfo.class).putExtra("partnerUserId",partners.get(position).getUserId()).putExtra("distance",distance).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}