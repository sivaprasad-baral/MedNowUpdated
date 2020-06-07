package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednow.model.Order;
import com.example.mednow.model.Partner;
import com.example.mednow.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;

public class OrderPayment extends AppCompatActivity {

    String orderId;
    String amount;
    Order order;
    User user;
    Partner partner;

    TextView textViewBillAmt,textViewTotalAmt;

    DatabaseReference databaseReferenceOrder,databaseReferenceUser,databaseReferencePartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        orderId = getIntent().getStringExtra("orderId");

        textViewBillAmt = findViewById(R.id.order_payment_text_view_bill_amt);
        textViewTotalAmt = findViewById(R.id.order_payment_text_view_total_amt);

        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        databaseReferenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order = dataSnapshot.getValue(Order.class);
                databaseReferenceOrder.removeEventListener(this);
                databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(order.getCustomerId());
                databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        databaseReferenceUser.removeEventListener(this);
                        databaseReferencePartner = FirebaseDatabase.getInstance().getReference("Partners").child(order.getPartnerId());
                        databaseReferencePartner.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                partner = dataSnapshot.getValue(Partner.class);
                                databaseReferencePartner.removeEventListener(this);
                                displayBill();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(OrderPayment.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(OrderPayment.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderPayment.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBill() {
        String label = "Rs. ".concat(String.valueOf(order.getCost()));
        textViewBillAmt.setText(label);
        label = "Rs. ".concat(String.valueOf(order.getCost() + 10));
        textViewTotalAmt.setText(label);
    }

    public void walletBtn(View view) {
        if(isConnected()) {
            if(user.getWalletBalance() >=  (int) (order.getCost() + 10)) {
                BiometricManager biometricManager = BiometricManager.from(OrderPayment.this);
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(OrderPayment.this, "Transaction Failed. No biometric setup to the device", Toast.LENGTH_SHORT).show();
                        return;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(OrderPayment.this, "Transaction Failed. Hardware Unavailable currently", Toast.LENGTH_SHORT).show();
                        return;
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(OrderPayment.this, "Transaction Failed. No Hardware found", Toast.LENGTH_SHORT).show();
                        return;
                    default:
                        Toast.makeText(OrderPayment.this, "Transaction Failed. Please Try Again", Toast.LENGTH_SHORT).show();
                        return;
                }
                Executor executor = ContextCompat.getMainExecutor(OrderPayment.this);
                BiometricPrompt biometricPrompt = new BiometricPrompt(OrderPayment.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(OrderPayment.this, errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        int amt = (int) order.getCost() + 10;
                        user.setWalletBalance(user.getWalletBalance() - amt);
                        databaseReferenceUser.setValue(user);
                        partner.setWalletBalance(partner.getWalletBalance() + amt);
                        databaseReferencePartner.setValue(partner);
                        Toast.makeText(OrderPayment.this,"Transaction Successful. Order Has Been Confirmed",Toast.LENGTH_SHORT).show();
                        setOrderConfirmationStatus();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(OrderPayment.this, "Authentication Failure.", Toast.LENGTH_SHORT).show();
                    }
                });
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Scan your finger to authenticate").setConfirmationRequired(true).setNegativeButtonText("Cancel").build();
                biometricPrompt.authenticate(promptInfo);
            } else {
                Toast.makeText(OrderPayment.this,"Not enough balance in your wallet",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(OrderPayment.this,"No internet connection. Please try again",Toast.LENGTH_SHORT).show();
        }
    }

    public void upiBtn(View view) {
        if(isConnected()) {
            amount = textViewTotalAmt.getText().toString();
            String name = "MedNow",upiId = "bmp.rathpramodkumar@okaxis",note = "Add to MedNow Wallet";
            Uri uri = Uri.parse("upi://pay").buildUpon().appendQueryParameter("pa",upiId).appendQueryParameter("pn",name).appendQueryParameter("tn",note).appendQueryParameter("am",amount).appendQueryParameter("cu","INR").build();
            Intent chooser = Intent.createChooser(new Intent(Intent.ACTION_VIEW).setData(uri),"Pay with");
            if(chooser.resolveActivity(OrderPayment.this.getPackageManager()) != null) {
                startActivityForResult(chooser,0);
            } else {
                Toast.makeText(OrderPayment.this,"No UPI Applications Found",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(OrderPayment.this,"No internet connection. Please try again",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(OrderPayment.this,"Transaction Successful. Order Has Been Confirmed",Toast.LENGTH_SHORT).show();
            partner.setWalletBalance(partner.getWalletBalance() + Integer.parseInt(amount));
            databaseReferencePartner.setValue(partner);
            setOrderConfirmationStatus();
        } else if(paymentCancel.equals("Payment Cancelled")) {
            Toast.makeText(OrderPayment.this,"Payment Cancelled By User",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(OrderPayment.this,"Transaction Failed. Please Try Again",Toast.LENGTH_SHORT).show();
        }
    }

    public void cardBtn(View view) {
    }

    public void codBtn(View view) {
        Toast.makeText(OrderPayment.this,"Order Has Been Confirmed.",Toast.LENGTH_SHORT).show();
        setOrderConfirmationStatus();
    }

    private void setOrderConfirmationStatus() {
        databaseReferenceOrder.child("cost").setValue(order.getCost() + 10);
        databaseReferenceOrder.child("orderStatus").setValue(Order.ORDER_CONFIRMED_BY_CUSTOMER);
        databaseReferenceOrder.child("orderTimeStamp").setValue(System.currentTimeMillis()/1000);
        databaseReferenceOrder.child("otpDelivery").setValue(String.valueOf(new Random().nextInt(8999) + 1000));
        startActivity(new Intent(OrderPayment.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void discountOptionBtn(View view) {
    }

    private boolean isConnected() {
        return ((ConnectivityManager) Objects.requireNonNull(OrderPayment.this.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }
}
