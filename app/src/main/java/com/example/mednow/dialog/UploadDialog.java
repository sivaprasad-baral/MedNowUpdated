package com.example.mednow.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mednow.R;

public class UploadDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    private TextView textViewMsg;
    private LottieAnimationView uploadProgress,uploadSuccess,uploadFailure;

    public UploadDialog(Activity activity) {
        this.activity = activity;
    }

    public void startUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_upload,null);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

        textViewMsg = view.findViewById(R.id.upload_msg_text_view);
        uploadProgress = view.findViewById(R.id.upload_progress_animation);
        uploadSuccess = view.findViewById(R.id.upload_success_animation);
        uploadFailure = view.findViewById(R.id.upload_failure_animation);
    }

    public void setUploadProgress(int progress) {
        String msg = "Uploaded : "+progress+" %";
        textViewMsg.setText(msg);
    }

    public void setUploadSuccess(String msg) {
        uploadProgress.setVisibility(View.GONE);
        uploadSuccess.setVisibility(View.VISIBLE);
        textViewMsg.setText(msg);
    }

    public void setUploadFailure(String msg) {
        uploadProgress.setVisibility(View.GONE);
        uploadFailure.setVisibility(View.VISIBLE);
        textViewMsg.setText(msg);
    }

    public void dismissDialog() {
        alertDialog.dismiss();
    }
}
