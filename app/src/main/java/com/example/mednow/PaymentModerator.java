package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mednow.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.Objects;

public class PaymentModerator extends AppCompatActivity implements PaymentResultListener {

    int amt;
    User user;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_moderator);
        Checkout.preload(getApplicationContext());

        String amount = getIntent().getStringExtra("amount");
        amt = Integer.parseInt(Objects.requireNonNull(amount)) * 100;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(firebaseUser).getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                databaseReference.removeEventListener(this);
                startPayment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_UzVBR4PzDTFxwc");
        final Activity activity = PaymentModerator.this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "MedNow");
            options.put("description", "Add to wallet");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(amt));
            checkout.open(activity, options);
        } catch(Exception e) {
            Toast.makeText(PaymentModerator.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        user.setWalletBalance(user.getWalletBalance() + amt/100);
        databaseReference.setValue(user);
        Toast.makeText(PaymentModerator.this,"Transaction Successful",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PaymentModerator.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(PaymentModerator.this,"Transaction Failed",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PaymentModerator.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
