package com.example.mednow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mednow.dialog.HelpDialog;

public class Help extends AppCompatActivity {

    boolean orderToggle = false,deliveryToggle = false,paymentsToggle = false,returnsToggle = false,walletToggle = false,prescriptionToggle = false,generalQueriesToggle = false;

    LinearLayout linearLayoutOrderEnquiry,linearLayoutDelivery,linearLayoutPayments,linearLayoutReturns,linearLayoutWallet,linearLayoutPrescription,linearLayoutGeneralQueries;
    ImageView buttonOrderEnquiry,buttonDelivery,buttonPayments,buttonReturns,buttonWallet,buttonPrescription,buttonGeneralQueries;
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
        linearLayoutPrescription = findViewById(R.id.help_prescription_linear_layout_list);
        linearLayoutGeneralQueries = findViewById(R.id.help_general_queries_linear_layout_list);

        buttonOrderEnquiry = findViewById(R.id.help_order_enquiry_image_view_show_hide);
        buttonDelivery = findViewById(R.id.help_delivery_image_view_show_hide);
        buttonPayments = findViewById(R.id.help_payments_image_view_show_hide);
        buttonReturns = findViewById(R.id.help_returns_image_view_show_hide);
        buttonWallet = findViewById(R.id.help_wallet_image_view_show_hide);
        buttonPrescription = findViewById(R.id.help_prescription_image_view_show_hide);
        buttonGeneralQueries = findViewById(R.id.help_general_queries_image_view_show_hide);


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

    public void orderQueryThreeBtn(View view) {
        helpDialog.startUploadDialog("Items are missing from my order?","");
    }

    public void orderQueryFourBtn(View view) {
        helpDialog.startUploadDialog("I want to modify my order?","");
    }

    public void orderQueryFiveBtn(View view) {
        helpDialog.startUploadDialog("Items are different from what I ordered?","");
    }


    public void orderQuerySixBtn(View view) {
        helpDialog.startUploadDialog("When will i receive my order?","");
    }


    public void orderQuerySevenBtn(View view) {
        helpDialog.startUploadDialog("I am received damaged product?","");
    }

    public void deliveryQueryOneBtn(View view) {
        helpDialog.startUploadDialog("Order status shows 'Delivered' but i have not received my order?","");
    }

    public void deliveryQueryTwoBtn(View view) {
        helpDialog.startUploadDialog("Which cities do we operate in?","");
    }

    public void deliveryQueryThreeBtn(View view) {
        helpDialog.startUploadDialog("Can I modify my address after I have placed my order?","");
    }

    public void paymentQueryOneBtn(View view) {
        helpDialog.startUploadDialog("What are the payment modes?","");
    }

    public void paymentQueryTwoBtn(View view) {
        helpDialog.startUploadDialog("When will I get refund?","");
    }


    public void returnQueryOneBtn(View view) {
        helpDialog.startUploadDialog("How do I return my order?","");
    }

    public void returnQueryTwoBtn(View view) {
        helpDialog.startUploadDialog("Which medicines are eligible for return?","");
    }

    public void returnQueryThreeBtn(View view) {
        helpDialog.startUploadDialog("I am unable to initiate a return request?","");
    }

    public void returnQueryFourBtn(View view) {
        helpDialog.startUploadDialog("Does MedNow pick up the products I want to return from my location?","");
    }

    public void returnQueryFiveBtn(View view) {
        helpDialog.startUploadDialog("When will I get refund?","");
    }

    public void walletQueryOneBtn(View view) {
        helpDialog.startUploadDialog("What is MNO cash?","");
    }

    public void walletQueryTwoBtn(View view) {
        helpDialog.startUploadDialog("Can I add money to MedNow wallet?","");
    }

    public void walletQueryThreeBtn(View view) {
        helpDialog.startUploadDialog("When will my cash expire?","");
    }

    public void walletQueryFourBtn(View view) {
        helpDialog.startUploadDialog("Can I transfer MNO cash to bank account?","");
    }

    public void walletQueryFiveBtn(View view) {
        helpDialog.startUploadDialog("How do I check my MNO cash?","");
    }

    public void showHidePrescriptionHelpBtn(View view) {
        if(prescriptionToggle) {
            linearLayoutPrescription.setVisibility(View.GONE);
            buttonPrescription.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            prescriptionToggle = false;
        } else {
            linearLayoutPrescription.setVisibility(View.VISIBLE);
            buttonPrescription.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            prescriptionToggle = true;
        }
    }

    public void prescriptionQueryOneBtn(View view) {
        helpDialog.startUploadDialog("Where do i send my prescription?","");
    }

    public void prescriptionQuerySevenBtn(View view) {
        helpDialog.startUploadDialog("What is a valid prescription?","");
    }

    public void showHideGeneralQueriesHelpBtn(View view) {
        if(generalQueriesToggle) {
            linearLayoutGeneralQueries.setVisibility(View.GONE);
            buttonGeneralQueries.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            generalQueriesToggle = false;
        } else {
            linearLayoutGeneralQueries.setVisibility(View.VISIBLE);
            buttonGeneralQueries.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            generalQueriesToggle = true;
        }
    }

    public void generalQueriesOneBtn(View view) {
        helpDialog.startUploadDialog("Are there any shipping charges for order?","");
    }

    public void generalQueriesTwoBtn(View view) {
        helpDialog.startUploadDialog("What is MedNow?","");
    }

    public void generalQueriesThreeBtn(View view) {
        helpDialog.startUploadDialog("Why did I not receive OTP on SMS?","");
    }

    public void generalQueriesFourBtn(View view) {
        helpDialog.startUploadDialog("How can I change my mail address linkd to my account?","");
    }

    public void generalQueriesFiveBtn(View view) {
        helpDialog.startUploadDialog("How do I become a partner?","");
    }

    public void generalQueriesSixBtn(View view) {
        helpDialog.startUploadDialog("What is th procedure to place an order?","");
    }

    public void showHidePromotionHelpBtn(View view) {
    }

    public void showHideReferralsHelpBtn(View view) {
    }

    public void showHideTransactionIssuesHelpBtn(View view) {
    }
}