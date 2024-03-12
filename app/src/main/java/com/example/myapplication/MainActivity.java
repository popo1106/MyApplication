package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private FirebaseAuth auth;
    ToggleButton togglePassword;

    EditText userName, password;
    TextView tv,signup;
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
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });
        auth = FirebaseAuth.getInstance();
    }
    private void initialization()
    {
        userName = findViewById(R.id.userNameET);
        signup = findViewById(R.id.signUpRedirectText);
        password = findViewById(R.id.passwordET);
        remember = findViewById(R.id.remember);
        tv = findViewById(R.id.error);
        togglePassword = findViewById(R.id.togglePassword);
        togglePassword.setTextOff(null);
        togglePassword.setTextOn(null);
    }


    public void login(View view) {
        String email = userName.getText().toString();
        String pass = password.getText().toString();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, MainActivity2.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                password.setError("Empty fields are not allowed");
            }
        } else if (email.isEmpty()) {
            userName.setError("Empty fields are not allowed");
        } else {
            userName.setError("Please enter correct email");
        }

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