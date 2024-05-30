package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private EditText Code1, Code2, Code3, Code4, Code5, Code6;


    private String verificationId,org,role,phoneNumber;
    private ProgressBar progressBar;
    private TextView textMobile;
    private Button buttonVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        textMobile = findViewById(R.id.textMobile);

        textMobile.setText(String.format("+972-%s",phoneNumber));

        progressBar = findViewById(R.id.progressBar);
        buttonVerify = findViewById(R.id.buttonVerify);

        Code1 = findViewById(R.id.Code1);
        Code2 = findViewById(R.id.Code2);
        Code3 = findViewById(R.id.Code3);
        Code4 = findViewById(R.id.Code4);
        Code5 = findViewById(R.id.Code5);
        Code6 = findViewById(R.id.Code6);

        verificationId = getIntent().getStringExtra("verificationId");
        role = getIntent().getStringExtra("role");
        org = getIntent().getStringExtra("org");
        setupOTPInputs();


    }

    private void setupOTPInputs()
    {
        Code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty())
                {
                    Code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty())
                {
                    Code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty())
                {
                    Code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty())
                {
                    Code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty())
                {
                    Code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void Verify(View view) {
        if( Code1.getText().toString().trim().isEmpty()
            ||  Code2.getText().toString().trim().isEmpty()
            ||  Code3.getText().toString().trim().isEmpty()
            ||  Code4.getText().toString().trim().isEmpty()
            ||  Code5.getText().toString().trim().isEmpty()
            ||  Code6.getText().toString().trim().isEmpty()){
            Toast.makeText(VerifyOTPActivity.this, "please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }
        String code =
                Code1.getText().toString()+
                Code2.getText().toString()+
                Code3.getText().toString()+
                Code4.getText().toString()+
                Code5.getText().toString()+
                Code6.getText().toString();
        if(verificationId != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            buttonVerify.setVisibility(View.INVISIBLE);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,code);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    buttonVerify.setVisibility(View.VISIBLE);
                    if(task.isSuccessful())
                    {

                        checkUserByPhoneNumber(phoneNumber, new UserExistsCallback() {
                            @Override
                            public void onUserExists(User user) {
                                if (user != null) {
                                    // User exists, do something with the user object
                                    Log.d("UserExists", "User found: " + user.getUserName());
                                    Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                } else {
                                    // User not found
                                    Log.d("UserExists", "UserExists");
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                // Handle error
                                Log.e("UserExists", "Error: " + errorMessage);
                            }
                        });
                    }else{
                        Toast.makeText(VerifyOTPActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void resendOTP(View view) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+972" + getIntent().getStringExtra("phoneNumber"),
                60,
                TimeUnit.SECONDS,
                VerifyOTPActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = newVerificationId;
                        Toast.makeText(VerifyOTPActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    public interface UserExistsCallback {
        void onUserExists(User user);
        void onError(String errorMessage);
    }

    public void checkUserByPhoneNumber(String phoneNumber, UserExistsCallback callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("organization")
                .child(org)
                .child(role);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userPhoneNumber = userSnapshot.child("phone number").getValue(String.class);
                    if (userPhoneNumber != null && userPhoneNumber.equals(phoneNumber)) {
                        // User with the given phone number found
                        User user = new User(userSnapshot.child("name").getValue(String.class),userSnapshot.child("email").getValue(String.class),userSnapshot.child("Id").getValue(String.class),role,org);
                        callback.onUserExists(user);
                        return; // Return after invoking the callback
                    }
                }
                // Invoke the callback with null if user not found
                callback.onUserExists(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                String errorMessage = "Database error: " + databaseError.getMessage();
                callback.onError(errorMessage);
            }
        });
    }

}