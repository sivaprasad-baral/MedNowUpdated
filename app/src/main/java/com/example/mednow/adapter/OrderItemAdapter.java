package com.example.mednow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednow.Home;
import com.example.mednow.R;
import com.example.mednow.ViewOrder;
import com.example.mednow.model.Medicine;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private Context context;
    private List<Medicine> medicines;
    private String orderId;

    public OrderItemAdapter(Context context, List<Medicine> medicines, String orderId) {
        this.context = context;
        this.medicines = medicines;
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_order_med_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Medicine medicine = medicines.get(position);
        holder.textViewMedName.setText(medicine.getMedName());
        holder.textViewMedPrice.setText(medicine.getMedPrice());
        holder.textViewQty.setText(String.valueOf(medicine.getMedQty()));
        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("medicines").child(medicine.getMedId()).removeValue();
                ViewOrder.cost -= Double.parseDouble(medicine.getMedPrice());
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("cost").setValue(ViewOrder.cost);
                String costTag = "Rs. ".concat(String.valueOf(ViewOrder.cost));
                ViewOrder.textViewCost.setText(costTag);
                medicines.remove(medicine);
                if(medicines.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("Orders").child(orderId).removeValue();
                    Toast.makeText(context,"No items to send",Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,medicines.size());
                }
            }
        });
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicine.setMedQty(medicine.getMedQty() + 1);
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("medicines").child(medicine.getMedId()).child("medQty").setValue(medicine.getMedQty());
                holder.textViewQty.setText(String.valueOf(medicine.getMedQty()));
                ViewOrder.cost += Double.parseDouble(medicine.getMedPrice());
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("cost").setValue(ViewOrder.cost);
                String costTag = "Rs. ".concat(String.valueOf(ViewOrder.cost));
                ViewOrder.textViewCost.setText(costTag);
            }
        });
        holder.btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(medicine.getMedQty() > 1) {
                    medicine.setMedQty(medicine.getMedQty() - 1);
                    FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("medicines").child(medicine.getMedId()).child("medQty").setValue(medicine.getMedQty());
                    holder.textViewQty.setText(String.valueOf(medicine.getMedQty()));
                    ViewOrder.cost -= Double.parseDouble(medicine.getMedPrice());
                    FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("cost").setValue(ViewOrder.cost);
                    String costTag = "Rs. ".concat(String.valueOf(ViewOrder.cost));
                    ViewOrder.textViewCost.setText(costTag);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMedName,textViewMedPrice,textViewQty;
        ImageView btnClose,btnAdd,btnSubtract;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.view_order_text_view_med_name);
            textViewMedPrice = itemView.findViewById(R.id.view_order_text_view_med_price);
            textViewQty = itemView.findViewById(R.id.view_order_text_view_qty);
            btnClose = itemView.findViewById(R.id.view_order_image_close_btn);
            btnAdd = itemView.findViewById(R.id.view_order_image_add_btn);
            btnSubtract = itemView.findViewById(R.id.view_order_image_subtract_btn);
        }
    }
}
