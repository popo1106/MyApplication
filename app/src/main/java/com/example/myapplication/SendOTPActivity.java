package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    private EditText inputMobile;

    TextView Error;

    boolean flagePhoone;
    private Button buttonGetOTP;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        inputMobile = findViewById(R.id.phoneNumber);
        buttonGetOTP = findViewById(R.id.buttonGetOTP);
        Error  = findViewById(R.id.errorPhone);
        flagePhoone = false;
    }

    public void GetOTP(View view) {
        if(inputMobile.getText().toString().trim().isEmpty())
        {
            Toast.makeText(SendOTPActivity.this, "Enter phone number",Toast.LENGTH_SHORT).show();
            return;
        }

        if(getPhoneNumber()) {
            buttonGetOTP.setVisibility(View.INVISIBLE);


            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+972" + inputMobile.getText().toString(),
                    60,
                    TimeUnit.SECONDS,
                    SendOTPActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            buttonGetOTP.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            buttonGetOTP.setVisibility(View.VISIBLE);
                            Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            buttonGetOTP.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                            intent.putExtra("phoneNumber", inputMobile.getText().toString());
                            intent.putExtra("verificationId", verificationId);
                            startActivity(intent);
                        }
                    }
            );
        }

    }


    public boolean getPhoneNumber() {
        String inputPhoneNumber = inputMobile.getText().toString().trim();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.orderByChild("phoneNumber").equalTo(inputPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    flagePhoone = true;
                    //Error.setText("true");
                    Toast.makeText(SendOTPActivity.this, "Phone number is correct!", Toast.LENGTH_SHORT).show();
                    Error.setText("Phone number is correct!");


                }else{
                    Toast.makeText(SendOTPActivity.this, "Phone number is incorrect!", Toast.LENGTH_SHORT).show();
                    Error.setText("Phone number is incorrect!");
                    flagePhoone = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
       return flagePhoone;
    }
}