package com.example.mednow.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednow.Home;
import com.example.mednow.R;
import com.example.mednow.model.Order;
import com.google.firebase.database.FirebaseDatabase;

public class AcceptRejectDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    public AcceptRejectDialog(Activity activity) {
        this.activity = activity;
    }

    public void startDialogForOrder(String msg, final String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_accept_reject,null);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

        TextView textViewMsg = view.findViewById(R.id.dialog_accept_reject_text_view_msg);
        Button btnCancel = view.findViewById(R.id.dialog_accept_reject_btn_cancel);
        Button btnContinue = view.findViewById(R.id.dialog_accept_reject_btn_continue);

        textViewMsg.setText(msg);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("orderStatus").setValue(Order.ORDER_REJECTED_BY_CUSTOMER);
                Toast.makeText(activity,"This order has been cancelled",Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }
}
