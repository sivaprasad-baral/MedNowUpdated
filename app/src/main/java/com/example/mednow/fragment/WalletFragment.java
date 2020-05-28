package com.example.mednow.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mednow.PaymentModerator;
import com.example.mednow.R;
import com.example.mednow.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class WalletFragment extends Fragment {

    private User user;
    private int addBalance;

    private TextView textViewBalance;
    private EditText editTextAddBalance;
    private Button buttonUpi,buttonCard;

    private ViewGroup container;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet,container,false);
        this.container = container;

        textViewBalance = view.findViewById(R.id.wallet_fragment_text_view_balance);
        editTextAddBalance = view.findViewById(R.id.wallet_fragment_edit_text_amount);
        buttonUpi = view.findViewById(R.id.wallet_fragment_upi_btn);
        buttonCard = view.findViewById(R.id.wallet_fragment_card_btn);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(firebaseUser).getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getActivity() != null) {
                    user = dataSnapshot.getValue(User.class);
                    textViewBalance.setText(String.valueOf(Objects.requireNonNull(user).getWalletBalance()));
                } else {
                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Objects.requireNonNull(container).getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        buttonUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    String amount = editTextAddBalance.getText().toString();
                    if(amount.isEmpty() || Integer.parseInt(amount) == 0) {
                        editTextAddBalance.setError("Amount unacceptable");
                        editTextAddBalance.requestFocus();
                        return;
                    }
                    addBalance = Integer.parseInt(amount);
                    buttonUpi.setClickable(false);
                    buttonCard.setClickable(false);
                    payWithUPI(amount);
                } else {
                    Toast.makeText(Objects.requireNonNull(container).getContext(),"No internet connection. Please try again",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    String amount = editTextAddBalance.getText().toString();
                    if(amount.isEmpty() || Integer.parseInt(amount) == 0) {
                        editTextAddBalance.setError("Amount unacceptable");
                        editTextAddBalance.requestFocus();
                        return;
                    }
                    startActivity(new Intent(getActivity(), PaymentModerator.class).putExtra("amount",amount).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Toast.makeText(Objects.requireNonNull(container).getContext(),"No internet connection. Please try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void payWithUPI(String amount) {
        String name = "MedNow",upiId = "bmp.rathpramodkumar@okaxis",note = "Add to MedNow Wallet";
        Uri uri = Uri.parse("upi://pay").buildUpon().appendQueryParameter("pa",upiId).appendQueryParameter("pn",name).appendQueryParameter("tn",note).appendQueryParameter("am",amount).appendQueryParameter("cu","INR").build();

        Intent chooser = Intent.createChooser(new Intent(Intent.ACTION_VIEW).setData(uri),"Pay with");
        if(chooser.resolveActivity(container.getContext().getPackageManager()) != null) {
            startActivityForResult(chooser,0);
        } else {
            Toast.makeText(container.getContext(),"No UPI Applications Found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            ArrayList<String> dataList = new ArrayList<>();
            if (resultCode == RESULT_OK || resultCode == 11) {
                if (data != null) {
                    String text = data.getStringExtra("response");
                    dataList.add(text);
                } else {
                    dataList.add("nothing");
                }
            } else {
                dataList.add("nothing");
            }
            upiPaymentDataOperation(dataList);
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> dataList) {
        String str = dataList.get(0);
        Log.d("upiTag",str);
        String paymentCancel = "",status = "";
        String[] response = str.split("&");
        for (String s : response) {
            String[] equalStr = s.split("=");
            if (equalStr.length >= 2) {
                if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                    status = equalStr[1].toLowerCase();
                }
            } else {
                paymentCancel = "Payment Cancelled";
            }
        }
        if(status.equals("success")) {
            Toast.makeText(container.getContext(),"Transaction Successful",Toast.LENGTH_SHORT).show();
            user.setWalletBalance(user.getWalletBalance() + addBalance);
            databaseReference.setValue(user);
        } else if(paymentCancel.equals("Payment Cancelled")) {
            Toast.makeText(container.getContext(),"Payment Cancelled By User",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(container.getContext(),"Transaction Failed. Please Try Again",Toast.LENGTH_SHORT).show();
        }
        buttonUpi.setClickable(true);
        buttonCard.setClickable(true);
        editTextAddBalance.setText(null);
    }

    private boolean isConnected() {
        return ((ConnectivityManager) Objects.requireNonNull(container.getContext().getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }
}
