package com.example.mednow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mednow.dialog.HelpDialog;

public class Help extends AppCompatActivity {

    boolean orderToggle = false,deliveryToggle = false,paymentsToggle = false,returnsToggle = false,walletToggle = false;

    LinearLayout linearLayoutOrderEnquiry,linearLayoutDelivery,linearLayoutPayments,linearLayoutReturns,linearLayoutWallet;
    ImageView buttonOrderEnquiry,buttonDelivery,buttonPayments,buttonReturns,buttonWallet;
    HelpDialog helpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        linearLayoutOrderEnquiry = findViewById(R.id.help_order_enquiry_linear_layout_list);
        linearLayoutDelivery = findViewById(R.id.help_delivery_linear_layout_list);
        linearLayoutPayments = findViewById(R.id.help_payments_linear_layout_list);
        linearLayoutReturns = findViewById(R.id.help_returns_linear_layout_list);
        linearLayoutWallet = findViewById(R.id.help_wallet_linear_layout_list);
        buttonOrderEnquiry = findViewById(R.id.help_order_enquiry_image_view_show_hide);
        buttonDelivery = findViewById(R.id.help_delivery_image_view_show_hide);
        buttonPayments = findViewById(R.id.help_payments_image_view_show_hide);
        buttonReturns = findViewById(R.id.help_returns_image_view_show_hide);
        buttonWallet = findViewById(R.id.help_wallet_image_view_show_hide);

        helpDialog = new HelpDialog(Help.this);
    }

    public void showHideOrderEnquiryHelpBtn(View view) {
        if(orderToggle) {
            linearLayoutOrderEnquiry.setVisibility(View.GONE);
            buttonOrderEnquiry.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            orderToggle = false;
        } else {
            linearLayoutOrderEnquiry.setVisibility(View.VISIBLE);
            buttonOrderEnquiry.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            orderToggle = true;
        }
    }

    public void showHideDeliveryHelpBtn(View view) {
        if(deliveryToggle) {
            linearLayoutDelivery.setVisibility(View.GONE);
            buttonDelivery.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            deliveryToggle = false;
        } else {
            linearLayoutDelivery.setVisibility(View.VISIBLE);
            buttonDelivery.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            deliveryToggle = true;
        }
    }

    public void showHidePaymentHelpBtn(View view) {
        if(paymentsToggle) {
            linearLayoutPayments.setVisibility(View.GONE);
            buttonPayments.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            paymentsToggle = false;
        } else {
            linearLayoutPayments.setVisibility(View.VISIBLE);
            buttonPayments.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            paymentsToggle = true;
        }
    }

    public void showHideReturnHelpBtn(View view) {
        if(returnsToggle) {
            linearLayoutReturns.setVisibility(View.GONE);
            buttonReturns.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            returnsToggle = false;
        } else {
            linearLayoutReturns.setVisibility(View.VISIBLE);
            buttonReturns.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            returnsToggle = true;
        }
    }

    public void showHideWalletBtn(View view) {
        if(walletToggle) {
            linearLayoutWallet.setVisibility(View.GONE);
            buttonWallet.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            walletToggle = false;
        } else {
            linearLayoutWallet.setVisibility(View.VISIBLE);
            buttonWallet.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            walletToggle = true;
        }
    }

    public void mailToCareBtn(View view) {
    }

    public void callToCareBtn(View view) {
    }

    public void orderQueryOneBtn(View view) {
        helpDialog.startUploadDialog("How can I track my order?","Your order has to be setup with the map and your location has to be used for tracking your order from medicine store to your location");
    }

    public void orderQueryTwoBtn(View view) {
        helpDialog.startUploadDialog("How do I cancel my order?","Your order can be cancelled from the history segment in your homepage. To cancel an order you have to do it before to delivery and have to make a proper explanation for the order cancellation");
    }
}