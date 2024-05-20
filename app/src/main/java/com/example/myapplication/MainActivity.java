package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    Spinner spinner2,orgSpinner;
    SharedPreferences.Editor editor;
    ToggleButton togglePassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    EditText userId, UserPassword;
    TextView signup;
    CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        initialization();
        SharedPreferences preferences  = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true"))
        {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                editor = preferences.edit();
                if (isChecked) {
                    editor.putString("remember", "true");
                    editor.apply();
                } else {
                    editor.putString("remember", "false");
                    editor.remove("userID");
                    editor.apply();
                }

            }
        });
    }

    private void saveUserPreferences(String userID) {
        if (remember.isChecked()) {
            editor.putString("userID",userID);
            editor.apply();
        }
    }
    private void initialization()
    {
        userId = findViewById(R.id.Id);
        signup = findViewById(R.id.signUpRedirectText);
        UserPassword = findViewById(R.id.passwordET);
        togglePassword = findViewById(R.id.togglePassword);
        togglePassword.setTextOff(null);
        togglePassword.setTextOn(null);
        firebaseAuth = FirebaseAuth.getInstance();
        remember = findViewById(R.id.remember);

    }
    @Override
    protected void onStart() {
        super.onStart();
        createSpinner();
    }
    public void createSpinner() {
        spinner2 = findViewById(R.id.spinner_level);
        orgSpinner = findViewById(R.id.spinner_org);
        List<String> orgList = new ArrayList<>();

        List<String> levelList = new ArrayList<>();
            levelList.add("מורה");
            levelList.add("מנהל-ת");
            levelList.add("אב-בית");

        // Create an ArrayAdapter and set it to spinner2
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, levelList);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);

        ArrayAdapter<String> orgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orgList);
        orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgSpinner.setAdapter(orgAdapter);

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("organization");

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


    private void getEmailForUsername(final String Id, final String password) {
        String role = spinner2.getSelectedItem().toString();
        String org = orgSpinner.getSelectedItem().toString();
        // Query the Realtime Database to find the user with the provided username
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("organization").child(org).child(role);
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
                                            User userD = new User(userSnapshot.child("name").getValue(String.class), email, userId.getText().toString().trim(), role, org);
                                            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                                            intent.putExtra("user", userD);
                                            saveUserPreferences(userId.getText().toString().trim());
                                            startActivity(intent);

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
                    // If the ID does not exist in the database
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


    private void checkInWaitingList(final String Id, final String password, final OnCheckListener listener) {
        String role = spinner2.getSelectedItem().toString();
        String org = orgSpinner.getSelectedItem().toString();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Waiting-list").child(org).child(role);
        usersRef.orderByChild("Id").equalTo(Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, WaitingScreen.class));
                                            listener.onCheck(true);
                                        } else {
                                            UserPassword.setError("סיסמה לא נכונה");
                                            listener.onCheck(true);
                                        }
                                    }
                                });
                    }
                } else {
                    listener.onCheck(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                listener.onCheck(false);
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

    public interface OnCheckListener {
        void onCheck(boolean success);
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
            checkInWaitingList(id, pass, new OnCheckListener() {
                @Override
                public void onCheck(boolean success) {
                    if (!success) {
                        getEmailForUsername(id, pass);
                    }
                }
            });
        }
    }

}