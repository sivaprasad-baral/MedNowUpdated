package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mednow.adapter.OrderItemAdapter;
import com.example.mednow.dialog.AcceptRejectDialog;
import com.example.mednow.model.Medicine;
import com.example.mednow.model.Order;
import com.example.mednow.model.Partner;
import com.example.mednow.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class ViewOrder extends AppCompatActivity {

    public static double cost = 0;
    List<Medicine> medicines;
    String orderId;
    User user;
    Partner partner;
    Order order;
    OrderItemAdapter itemAdapter;

    TextView textViewCustomerName,textViewCustomerAddress,textViewPharmacyName,textViewPharmacyAddress;
    public static TextView textViewCost;
    TextView textViewLoaderMsg;
    LottieAnimationView lottieFetchBill;
    RecyclerView recyclerView;
    RelativeLayout layoutOrder;
    LinearLayout layoutLoader;
    CircularView circularViewTimer;
    CircularView.OptionsBuilder builderWithTimer;
    AcceptRejectDialog acceptRejectDialog;

    DatabaseReference databaseReferenceUser,databaseReferencePartner,databaseReferenceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        orderId = getIntent().getStringExtra("orderId");

        acceptRejectDialog = new AcceptRejectDialog(ViewOrder.this);
        textViewCustomerName = findViewById(R.id.view_order_text_view_customer_name);
        textViewCustomerAddress = findViewById(R.id.view_order_text_view_customer_address);
        textViewPharmacyName = findViewById(R.id.view_order_text_view_pharmacy_name);
        textViewPharmacyAddress = findViewById(R.id.view_order_text_view_pharmacy_address);
        textViewCost = findViewById(R.id.view_order_text_view_cost);
        textViewLoaderMsg = findViewById(R.id.view_order_text_view_loader_msg);
        lottieFetchBill = findViewById(R.id.view_order_lottie_fetch_bill);
        circularViewTimer = findViewById(R.id.view_order_circular_view_timer);
        layoutOrder = findViewById(R.id.view_order_layout);
        layoutLoader = findViewById(R.id.view_order_layout_loader);
        recyclerView = findViewById(R.id.view_order_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewOrder.this));
        itemAdapter = new OrderItemAdapter(ViewOrder.this, new ArrayList<Medicine>(), orderId);
        recyclerView.setAdapter(itemAdapter);

        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        databaseReferencePartner = FirebaseDatabase.getInstance().getReference("Partners");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users");

        builderWithTimer = new CircularView.OptionsBuilder().shouldDisplayText(true).setCounterInSeconds(299).setCircularViewCallback(new CircularViewCallback() {
            @Override
            public void onTimerFinish() {
                databaseReferenceOrder.child("orderStatus").setValue(Order.ORDER_TIMED_OUT);
                Toast.makeText(ViewOrder.this,"No response for the request",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewOrder.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

            @Override
            public void onTimerCancelled() {
            }
        });
        circularViewTimer.setOptions(builderWithTimer);
        circularViewTimer.startTimer();

        databaseReferenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order = dataSnapshot.getValue(Order.class);
                if(Objects.requireNonNull(order).getOrderStatus() == Order.ORDER_SENT_FOR_CONFIRMATION) {
                    databaseReferenceOrder.removeEventListener(this);
                    databaseReferenceUser.child(order.getCustomerId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            databaseReferenceUser.child(order.getCustomerId()).removeEventListener(this);
                            databaseReferencePartner.child(order.getPartnerId()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    partner = dataSnapshot.getValue(Partner.class);
                                    databaseReferenceUser.child(order.getPartnerId()).removeEventListener(this);
                                    layoutLoader.setVisibility(View.GONE);
                                    layoutOrder.setVisibility(View.VISIBLE);
                                    cost = order.getCost();
                                    displayDetails();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(ViewOrder.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ViewOrder.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if(order.getOrderStatus() == Order.ORDER_REJECTED_BY_PARTNER) {
                    databaseReferenceOrder.removeEventListener(this);
                    Toast.makeText(ViewOrder.this,"Order can not be accepted currently",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ViewOrder.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else if(order.getOrderStatus() == Order.ORDER_PROCESSING) {
                    circularViewTimer.stopTimer();
                    circularViewTimer.setVisibility(View.GONE);
                    String msg = "Your order is being processed by partner";
                    textViewLoaderMsg.setText(msg);
                    lottieFetchBill.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewOrder.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDetails() {
        medicines = new ArrayList<>();
        for (String key : order.getMedicines().keySet()) {
            medicines.add(order.getMedicines().get(key));
        }
        if(!medicines.isEmpty()) {
            textViewCustomerName.setText(user.getName());
            try {
                Geocoder geocoder = new Geocoder(ViewOrder.this, Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(user.getLatitude(),user.getLongitude(),1);
                String address = addresses.get(0).getAddressLine(0);
                textViewCustomerAddress.setText(address);
            } catch (IOException e) {
                Toast.makeText(ViewOrder.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            textViewPharmacyName.setText(partner.getPharmacyName());
            try {
                Geocoder geocoder = new Geocoder(ViewOrder.this, Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(partner.getLatitude(),partner.getLongitude(),1);
                String address = addresses.get(0).getAddressLine(0);
                textViewPharmacyAddress.setText(address);
            } catch (IOException e) {
                Toast.makeText(ViewOrder.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            itemAdapter = new OrderItemAdapter(ViewOrder.this, medicines, orderId);
            recyclerView.setAdapter(itemAdapter);
            String costTag = "Rs. ".concat(String.valueOf(cost));
            textViewCost.setText(costTag);
        }
    }

    public void confirmOrderBtn(View view) {
        startActivity(new Intent(ViewOrder.this, OrderPayment.class).putExtra("orderId",orderId));
    }

    public void rejectOrderBtn(View view) {
        acceptRejectDialog.startDialogForOrder("Are you sure to reject the order?",orderId);
    }

    @Override
    public void onBackPressed() {
        acceptRejectDialog.startDialogForOrder("Going back will cancel the order.\nAre you sure to Go Back?",orderId);
    }
}
