package com.example.mednow.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mednow.R;

public class LoaderDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    private TextView textViewMsg;
    private LottieAnimationView loaderProgress,loaderSuccess,loaderFailure;

    public LoaderDialog(Activity activity) {
        this.activity = activity;
    }

    public void startUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loader,null);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

        textViewMsg = view.findViewById(R.id.loader_msg_text_view);
        loaderProgress = view.findViewById(R.id.loader_progress_animation);
        loaderSuccess = view.findViewById(R.id.loader_success_animation);
        loaderFailure = view.findViewById(R.id.loader_failure_animation);
    }

    public void setLoaderProgress(String msg) {
        textViewMsg.setText(msg);
    }

    public void setLoaderSuccess(String msg) {
        loaderProgress.setVisibility(View.GONE);
        loaderSuccess.setVisibility(View.VISIBLE);
        textViewMsg.setText(msg);
    }

    public void setLoaderFailure(String msg) {
        loaderProgress.setVisibility(View.GONE);
        loaderFailure.setVisibility(View.VISIBLE);
        textViewMsg.setText(msg);
    }

    public void dismissDialog() {
        alertDialog.dismiss();
    }
}
