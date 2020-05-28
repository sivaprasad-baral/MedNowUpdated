package com.example.mednow.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.mednow.R;

public class HelpDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    private TextView textViewQues,textViewAns;

    public HelpDialog(Activity activity) {
        this.activity = activity;
    }

    public void startUploadDialog(String ques,String ans) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_help,null);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();

        textViewQues = view.findViewById(R.id.help_dialog_text_view_question);
        textViewAns = view.findViewById(R.id.help_dialog_text_view_answer);

        textViewQues.setText(ques);
        textViewAns.setText(ans);
    }
}
