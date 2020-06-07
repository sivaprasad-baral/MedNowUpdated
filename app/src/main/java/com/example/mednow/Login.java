package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mednow.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;

public class Login extends AppCompatActivity {

    boolean activityOpenStatus = true;

    EditText editTextPhoneNo,editTextCode;

    CallbackManager mCallbackManager;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String defCode = "+91";
        editTextPhoneNo = findViewById(R.id.login_edit_text_phone_no);
        editTextCode = findViewById(R.id.login_edit_text_code);
        editTextCode.setText(defCode);

        mCallbackManager = CallbackManager.Factory.create();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loginPhoneBtn(View view) {
        String phoneNo = editTextPhoneNo.getText().toString();
        String code = editTextCode.getText().toString();
        if(phoneNo.length() != 10) {
            editTextPhoneNo.setError("Invalid phone no");
            editTextPhoneNo.requestFocus();
            return;
        } else if(code.isEmpty() || code.charAt(0) != '+' || !Character.isDigit(code.charAt(1))) {
            editTextCode.setError("Invalid ISO code");
            editTextCode.requestFocus();
            return;
        }
        activityOpenStatus = false;
        startActivity(new Intent(Login.this,OtpVerify.class).putExtra("phoneNo",phoneNo).putExtra("code",code));
    }

    public void loginFbBtn(View view) {
        LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this,"Login cancelled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        firebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken())).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    databaseReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(activityOpenStatus) {
                                User user = dataSnapshot.getValue(User.class);
                                if(user == null) {
                                    addNewUserToDatabase();
                                } else {
                                    startActivity(new Intent(Login.this,Location.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                }
                            } else {
                                databaseReference.child("Users").child(firebaseUser.getUid()).removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Login.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    });
                } else {
                    Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addNewUserToDatabase() {
        User user = new User(firebaseUser.getEmail(),firebaseUser.getDisplayName(),firebaseUser.getUid());
        user.setProfileImg(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());
        user.setWalletBalance(0);
        databaseReference.child("Users").child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    activityOpenStatus = false;
                    startActivity(new Intent(Login.this,Location.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                    firebaseUser.delete();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        activityOpenStatus = false;
        super.onBackPressed();
    }
}