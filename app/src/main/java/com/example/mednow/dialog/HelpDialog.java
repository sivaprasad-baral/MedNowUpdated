package com.example.mednow.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.mednow.R;

public class HelpDialog {

    private Activity activity;

    public HelpDialog(Activity activity) {
        this.activity = activity;
    }

    public void startHelpDialog(String ques,String ans) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_help,null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView textViewQues = view.findViewById(R.id.help_dialog_text_view_question);
        TextView textViewAns = view.findViewById(R.id.help_dialog_text_view_answer);

        textViewQues.setText(ques);
        textViewAns.setText(ans);
    }
}
