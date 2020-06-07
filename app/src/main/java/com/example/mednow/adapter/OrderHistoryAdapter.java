package com.example.mednow.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mednow.ManageOrder;
import com.example.mednow.R;
import com.example.mednow.model.Medicine;
import com.example.mednow.model.Order;
import com.example.mednow.model.Partner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Order order = orders.get(position);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Partners").child(order.getPartnerId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Partner partner = dataSnapshot.getValue(Partner.class);
                databaseReference.removeEventListener(this);
                holder.textViewPharmacyName.setText(Objects.requireNonNull(partner).getPharmacyName());
                if(partner.getPharmacyImg() != null) {
                    Glide.with(context).load(Uri.parse(partner.getPharmacyImg())).into(holder.imageViewPharmacyImg);
                } else {
                    Glide.with(context).load(R.drawable.image_not_available).into(holder.imageViewPharmacyImg);
                }
                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(partner.getLatitude(),partner.getLongitude(),1);
                    String fullAddress = addresses.get(0).getAddressLine(0);
                    String[] addressList = fullAddress.split(",");
                    int l = addressList.length;
                    String address = addressList[l-4].trim() + "," + addressList[l-3] + "," + addressList[l-2];
                    holder.textViewPharmacyAddress.setText(address);
                } catch (IOException e) {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        String medNames = "",medPrices = "";
        for (String key : order.getMedicines().keySet()) {
            Medicine medicine = order.getMedicines().get(key);
            medNames = medNames.concat(Objects.requireNonNull(medicine).getMedName().concat("\n"));
            medPrices = medPrices.concat(medicine.getMedPrice()).concat("\n");
        }
        holder.textViewMedNames.setText(medNames.trim());
        holder.textViewMedPrices.setText(medPrices.trim());
        String label = "Rs. " + order.getCost();
        holder.textViewTotalAmt.setText(label);
        switch(order.getOrderStatus()) {
            case Order.ORDER_TIMED_OUT:
                label = "Expired";
                break;
            case Order.ORDER_REJECTED_BY_PARTNER:
                label = "Rejected";
                break;
            case Order.ORDER_REJECTED_BY_CUSTOMER | Order.ORDER_CANCELLED :
                label = "Cancelled";
                break;
            case Order.ORDER_CONFIRMED_BY_CUSTOMER:
                label = "Confirmed";
                break;
            case Order.ORDER_OUT_FOR_DELIVERY:
                label = "To be Delivered";
                break;
            case Order.ORDER_DELIVERED:
                label = "Delivered";
                break;
            case Order.ORDER_RETURNED:
                label = "Returned";
                break;
            default:
                label = "No Response";
        }
        holder.textViewOrderStatus.setText(label);
        holder.textViewOrderDateTime.setText(getDateTime(order.getOrderTimeStamp()));
        holder.manageOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ManageOrder.class).putExtra("orderId",order.getOrderId()));
            }
        });
    }

    private String getDateTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timeStamp*1000);
        return DateFormat.format("dd/MM/yyyy   hh:mm:ss",calendar).toString();
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPharmacyName,textViewPharmacyAddress;
        TextView textViewMedNames,textViewMedPrices;
        TextView textViewTotalAmt,textViewOrderDateTime;
        TextView textViewOrderStatus;
        Button manageOrderBtn;
        ImageView imageViewPharmacyImg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPharmacyImg = itemView.findViewById(R.id.history_image_view_pharmacy_img);
            textViewPharmacyName = itemView.findViewById(R.id.history_text_view_pharmacy_name);
            textViewPharmacyAddress = itemView.findViewById(R.id.history_text_view_pharmacy_address);
            textViewMedNames = itemView.findViewById(R.id.history_text_view_med_names);
            textViewMedPrices = itemView.findViewById(R.id.history_text_view_med_price);
            textViewTotalAmt = itemView.findViewById(R.id.history_text_view_total_price);
            textViewOrderDateTime = itemView.findViewById(R.id.history_text_view_order_date);
            textViewOrderStatus = itemView.findViewById(R.id.history_text_view_order_status);
            manageOrderBtn = itemView.findViewById(R.id.history_btn_manage);
        }
    }
}
