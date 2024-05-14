package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner spinner2;
    ArrayList<String> Role = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ToggleButton togglePassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    EditText userId, UserPassword;
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
    }
    private void initialization()
    {
        userId = findViewById(R.id.Id);
        signup = findViewById(R.id.signUpRedirectText);
        UserPassword = findViewById(R.id.passwordET);
        remember = findViewById(R.id.remember);
        tv = findViewById(R.id.error);
        togglePassword = findViewById(R.id.togglePassword);
        togglePassword.setTextOff(null);
        togglePassword.setTextOn(null);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("organization").child("640037");
        Role.add("מנהל/ת");
        Role.add("אב-בית");
        Role.add("מורה");

    }
    @Override
    protected void onStart() {
        super.onStart();
        createSpinner();
    }
    public void createSpinner() {
        spinner2 = findViewById(R.id.spinner_level);
        List<String> levelList = new ArrayList<>();
            levelList.add("מורה");
            levelList.add("מנהל/ת");
            levelList.add("אב-בית");

        // Create an ArrayAdapter and set it to spinner2
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, levelList);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);
    }


    private void getEmailForUsername(final String Id, final String password) {
        String role = spinner2.getSelectedItem().toString();
        // Query the Realtime Database to find the user with the provided username
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("organization").child("640037").child(role);
        usersRef.orderByChild("Id").equalTo(Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the username exists, retrieve the email
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        // Authenticate user with retrieved email and password
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success
                                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            User userD = new User(userSnapshot.child("name").getValue(String.class), "", "", Role.indexOf(role), "640037");
                                            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                                            intent.putExtra("user", userD);
                                            startActivity(intent);

                                            //startActivity(new Intent(MainActivity.this, MainActivity2.class));
                                            // Proceed to next activity or perform desired action
                                        } else {
                                            // Other authentication failures
                                            //Toast.makeText(MainActivity.this, "pasword not found.", Toast.LENGTH_SHORT).show();
                                            UserPassword.setError("סיסמה לא נכונה");

                                        }
                                    }
                                });

                    }
                } else {
                    // If the username does not exist in the database
                    userId.setError("תעודת זהות לא נמצאה");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkInWaitingList(final String Id, final String password) {
        String role = spinner2.getSelectedItem().toString();
        // Query the Realtime Database to find the user with the provided username
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Waiting-list").child("640037").child(role);
        usersRef.orderByChild("Id").equalTo(Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the username exists, retrieve the email
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        // Authenticate user with retrieved email and password
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success
                                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, WaitingScreen.class));
                                            // Proceed to next activity or perform desired action
                                        } else {
                                            // Other authentication failures
                                            //Toast.makeText(MainActivity.this, "pasword not found.", Toast.LENGTH_SHORT).show();
                                            //UserPassword.setError("סיסמה לא נכונה");

                                        }
                                    }
                                });

                    }
                } else {
                    // If the username does not exist in the database
                    //userId.setError("תעודת זהות לא נמצאה");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void TogglePassword(View view) {
        if (UserPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // If the password is currently visible, hide it
            UserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePassword.setBackgroundResource(R.drawable.baseline_visibility_off_24); // Change the toggle button icon
        } else {
            // If the password is currently hidden, show it
            UserPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePassword.setBackgroundResource(R.drawable.baseline_visibility_24); // Change the toggle button icon
        }
    }

    public void Forgot_password(View view) {
        startActivity(new Intent(getApplicationContext(), SendOTPActivity.class));
    }


    public void loginUser(View view) {
        String id = userId.getText().toString().trim();
        String pass = UserPassword.getText().toString().trim(); // Moved the password retrieval here

        // Check if username and password fields are not empty
        if (pass.isEmpty()) {
            UserPassword.setError("הסיסמה לא יכולה להיות ריקה");
        }
        if(id.isEmpty())
        {
            userId.setError("התעודת זהות לא יכולה להיות ריקה");
        }
        if(!(pass.isEmpty()||id.isEmpty())) {
            checkInWaitingList(id, pass);
            getEmailForUsername(id, pass);
        }
    }
}