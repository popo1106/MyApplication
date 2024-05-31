package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    private EditText inputMobile;
    String phoneNumber;
    String role;
    TextView Error;
    Spinner spinner2,orgSpinner;

    private Button buttonGetOTP;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        inputMobile = findViewById(R.id.phoneNumber);
        spinner2 = findViewById(R.id.spinner_level);
        orgSpinner = findViewById(R.id.spinner_org);
        buttonGetOTP = findViewById(R.id.buttonGetOTP);
        Error  = findViewById(R.id.errorPhone);
    }
    protected void onStart() {
        super.onStart();
        createSpinner();

    }
    public void GetOTP(View view) {
        if(inputMobile.getText().toString().trim().isEmpty())
        {
            Toast.makeText(SendOTPActivity.this, "Enter phone number",Toast.LENGTH_SHORT).show();
            inputMobile.setError("נא להזין מספר טלפון");
            return;
        }
        if (!inputMobile.getText().toString().startsWith("0")) {
            // Prepend "0" to the phone number
            phoneNumber = "0" + inputMobile.getText().toString();
        }
        else {
            phoneNumber = inputMobile.getText().toString();
        }
        checkUserByPhoneNumber(phoneNumber, new UserExistsCallback() {
            @Override
            public void onUserExists(boolean exists) {
                if (exists) {
                    // User exists
                    buttonGetOTP.setVisibility(View.INVISIBLE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+972" + phoneNumber,
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
                                    intent.putExtra("phoneNumber", phoneNumber);
                                    intent.putExtra("verificationId", verificationId);
                                    Log.e("role1",role);
                                    intent.putExtra("org", orgSpinner.getSelectedItem().toString());
                                    intent.putExtra("role", role);
                                    startActivity(intent);
                                }
                            }
                    );
                } else {
                    // User does not exist
                    Log.d("checkUserByPhoneNumber", "User does not exist.");
                    inputMobile.setError("מספר הטלפון לא קיים במערכת");
                }
            }
        });
    }



    public interface UserExistsCallback {
        void onUserExists(boolean exists);
    }

    public void checkUserByPhoneNumber(String phoneNumber, UserExistsCallback callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("organization")
                .child(orgSpinner.getSelectedItem().toString())
                .child(spinner2.getSelectedItem().toString());
        Log.e("role2",spinner2.getSelectedItem().toString());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Assuming each child node represents a user
                    String userPhoneNumber = userSnapshot.child("phone number").getValue(String.class);
                    if (userPhoneNumber != null && userPhoneNumber.equals(phoneNumber)) {
                        // User with the given phone number found
                        role = spinner2.getSelectedItem().toString();
                        userExists = true;
                        break;
                    }
                }
                // Invoke the callback with the result
                callback.onUserExists(userExists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("checkUserByPhoneNumber", "Database error: " + databaseError.getMessage());
                // Invoke the callback with false on error
                callback.onUserExists(false);
            }
        });
    }
    public void createSpinner() {

        List<String> orgList = new ArrayList<>();

        List<String> levelList = new ArrayList<>();
        levelList.add("מורה");
        levelList.add("מנהל-ת");
        levelList.add("אב-בית");

        // Create an ArrayAdapter and set it to spinner2
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(SendOTPActivity.this, android.R.layout.simple_spinner_item, levelList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        ArrayAdapter<String> orgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orgList);
        orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgSpinner.setAdapter(orgAdapter);

        // Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("organization");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orgList.clear(); // Clear the list before adding new data
                for (DataSnapshot orgSnapshot : dataSnapshot.getChildren()) {
                    String orgName = orgSnapshot.getKey();
                    if(!orgName.equals("building")) {
                        orgList.add(orgName);
                    }
                }
                orgAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

    }
}