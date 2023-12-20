package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private EditText Code1, Code2, Code3, Code4, Code5, Code6;
    private String verificationId;
    private ProgressBar progressBar;
    private TextView textMobile;
    private Button buttonVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format("+972-%s", getIntent().getStringExtra("phoneNumber")));

        progressBar = findViewById(R.id.progressBar);
        buttonVerify = findViewById(R.id.buttonVerify);

        Code1 = findViewById(R.id.Code1);
        Code2 = findViewById(R.id.Code2);
        Code3 = findViewById(R.id.Code3);
        Code4 = findViewById(R.id.Code4);
        Code5 = findViewById(R.id.Code5);
        Code6 = findViewById(R.id.Code6);

        verificationId = getIntent().getStringExtra("verificationId");
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
                        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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
}