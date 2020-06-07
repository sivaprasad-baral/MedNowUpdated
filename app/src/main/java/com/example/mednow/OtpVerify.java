package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class OtpVerify extends AppCompatActivity {

    String verificationCode; // Verification code sent from firebase
    boolean activityOpenStatus = true;

    EditText editTextOTP;
    CircularView circularViewTimer;
    TextView textViewResend;
    CircularView.OptionsBuilder builderWithTimer;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        requestOTP(getIntent().getStringExtra("code"),getIntent().getStringExtra("phoneNo"));

        editTextOTP = findViewById(R.id.otp_verify_edit_text_otp);
        textViewResend = findViewById(R.id.otp_verify_text_view_resend);
        circularViewTimer = findViewById(R.id.otp_verify_circular_view_timer);
        builderWithTimer = new CircularView.OptionsBuilder().shouldDisplayText(true).setCounterInSeconds(59).setCircularViewCallback(new CircularViewCallback() {
                    @Override
                    public void onTimerFinish() {
                        textViewResend.setVisibility(View.VISIBLE);
                        circularViewTimer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTimerCancelled() {
                    }
                });
        circularViewTimer.setOptions(builderWithTimer);
        circularViewTimer.startTimer();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void requestOTP(String code, String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                code+phoneNo,               // Phone number to verify
                59,                         // Timeout duration
                TimeUnit.SECONDS,              // Unit of timeout
                TaskExecutors.MAIN_THREAD,     // Activity (for callback binding)
                mCallbacks);                   // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String receivedCode = phoneAuthCredential.getSmsCode();
            if(receivedCode != null) {
                signInUser(receivedCode);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OtpVerify.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };

    public void verifyOTPBtn(View view) {
        String enteredCode = editTextOTP.getText().toString();
        if(enteredCode.length() != 6) {
            editTextOTP.setError("Invalid OTP");
            editTextOTP.requestFocus();
            return;
        }
        signInUser(enteredCode);
    }

    private void signInUser(String userCode) {
        firebaseAuth.signInWithCredential(PhoneAuthProvider.getCredential(verificationCode,userCode)).addOnCompleteListener(OtpVerify.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    activityOpenStatus = false;
                    if(Objects.requireNonNull(firebaseUser).getDisplayName() == null) {
                        startActivity(new Intent(OtpVerify.this,UserInfo.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        startActivity(new Intent(OtpVerify.this,Location.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                } else {
                    Toast.makeText(OtpVerify.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void resendOTPBtn(View view) {
        requestOTP(getIntent().getStringExtra("code"),getIntent().getStringExtra("phoneNo"));
        textViewResend.setVisibility(View.GONE);
        circularViewTimer.setVisibility(View.VISIBLE);
        circularViewTimer.setOptions(builderWithTimer);
        circularViewTimer.startTimer();
    }

    @Override
    public void onBackPressed() {
        activityOpenStatus = false;
        circularViewTimer.stopTimer();
        firebaseAuth.signOut();
        super.onBackPressed();
    }
}
