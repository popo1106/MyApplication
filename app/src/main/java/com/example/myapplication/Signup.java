package com.example.myapplication;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText signupEmail, signupPassword, signupId, signupName;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupId = findViewById(R.id.signup_id);
        signupName = findViewById(R.id.signup_Name);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        createSpinner();
    }

    public void createSpinner() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("organization");

        // Assuming you have a Spinner defined in your layout with the id "spinner_school"
        Spinner spinner = findViewById(R.id.spinner_school);
        Spinner spinner2 = findViewById(R.id.spinner_level);

        // Create an ArrayList to store your data
        ArrayList<String> dataList = new ArrayList<>();

        // Attach a listener to read the data from Firebase Database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the existing data
                dataList.clear();

                // Iterate through each child node of "organization"
                for (DataSnapshot organizationSnapshot : dataSnapshot.getChildren()) {
                    // Get the key (which is the organization ID)
                    String organizationId = organizationSnapshot.getKey();
                    // Add the organization ID to your ArrayList
                    dataList.add(organizationId);
                }

                // Create an ArrayAdapter and set it to the Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Signup.this, android.R.layout.simple_spinner_item, dataList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                List<String> dataList = new ArrayList<>();
                dataList.add("מורה");
                dataList.add("מנהל/ת");
                dataList.add("אב-בית");

                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Signup.this, android.R.layout.simple_spinner_item, dataList);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });
    }



    public void signupUser(View view) {
        String email = signupEmail.getText().toString().trim();
        String pass = signupPassword.getText().toString().trim();
        String name = signupName.getText().toString().trim();
        String Id = signupId.getText().toString().trim();
        if (email.isEmpty()){
            signupEmail.setError("האימייל לא יכול להיות ריק");
        }
        if (pass.isEmpty()){
            signupPassword.setError("הסיסמה לא יכולה להיות ריקה");
        }
        if (name.isEmpty()){
            signupName.setError("השם לא יכול להיות ריק");
        }
        if (Id.isEmpty()){
            signupId.setError("התעודת זהות לא יכולה להיות ריקה");
        }
        else if(!name.isEmpty()&&!pass.isEmpty()){
            auth.createUserWithEmailAndPassword(name, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup.this, MainActivity.class));
                    } else {
                        Toast.makeText(Signup.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, MainActivity.class));
            }
        });
    }
}
