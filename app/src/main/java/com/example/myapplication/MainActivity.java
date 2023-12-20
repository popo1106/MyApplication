package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ToggleButton togglePassword;

    EditText userName, password;
    TextView tv;
    CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        sp = getSharedPreferences("checkBox",MODE_PRIVATE);
        String checkBox = sp.getString("remember","");
        if(checkBox.equals("true")) {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
        initialization();

    }
    private void initialization()
    {
        userName = findViewById(R.id.userNameET);
        password = findViewById(R.id.passwordET);
        remember = findViewById(R.id.remember);
        tv = findViewById(R.id.error);
        togglePassword = findViewById(R.id.togglePassword);
        togglePassword.setTextOff(null);
        togglePassword.setTextOn(null);
    }

    public void login(View view) {
        String enteredUsername = userName.getText().toString();
        String enteredPassword = password.getText().toString();

        // Query the Firebase database to check if the user exists
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.orderByChild("UserName").equalTo(enteredUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        if (enteredPassword.equals(storedPassword)) {
                            // Password matches, user is authenticated
                            if(remember.isChecked())
                            {
                                sp = getSharedPreferences("checkBox",MODE_PRIVATE);
                                editor = sp.edit();
                                editor.putString("remember","true");
                                editor.putString("name",enteredUsername);
                                editor.putString("password",enteredPassword);
                                editor.apply();
                            }
                            else
                            {
                                sp = getSharedPreferences("checkBox",MODE_PRIVATE);
                                editor = sp.edit();
                                editor.putString("remember","false");
                                editor.apply();
                            }
                            // Start the MainActivity2 activity
                            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("name",enteredUsername);
                            intent.putExtra("password",enteredPassword);
                            startActivity(intent);
                            tv.setText("");
                        } else {
                            // Password does not match
                            tv.setText("Incorrect password");
                        }
                    }
                } else {
                    // User with the entered username doesn't exist
                    tv.setText("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }


    public void TogglePassword(View view) {
        if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // If the password is currently visible, hide it
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePassword.setBackgroundResource(R.drawable.baseline_visibility_off_24); // Change the toggle button icon
        } else {
            // If the password is currently hidden, show it
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePassword.setBackgroundResource(R.drawable.baseline_visibility_24); // Change the toggle button icon
        }
    }

    public void Forgot_password(View view) {
        startActivity(new Intent(getApplicationContext(), SendOTPActivity.class));
    }
}