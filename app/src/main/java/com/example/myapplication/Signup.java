package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Signup extends AppCompatActivity {
    String selectedValue, selectedValue2;
    private FirebaseAuth auth;
    Spinner spinner, spinner2;
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
//        signupButton = findViewById(R.id.signup_button);
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
        spinner = findViewById(R.id.spinner_school);
        spinner2 = findViewById(R.id.spinner_level);

        // Create an ArrayList to store spinner1 data
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        // Create a list for spinner2 data
        List<String> levelList = new ArrayList<>();
        levelList.add("מורה");
        levelList.add("מנהל-ת");
        levelList.add("אב-בית");

        // Create an ArrayAdapter and set it to spinner2
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Signup.this, android.R.layout.simple_spinner_item, levelList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        // Set an item selected listener for the school spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Update selectedValue when an item is selected
                selectedValue = dataList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case when nothing is selected
            }
        });

        // Set an item selected listener for the level spinner
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Update selectedValue2 when an item is selected
                selectedValue2 = levelList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case when nothing is selected
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
            return; // Return early if email is empty
        }
        if (pass.isEmpty()){
            signupPassword.setError("הסיסמה לא יכולה להיות ריקה");
            return; // Return early if password is empty
        }
        if (name.isEmpty()){
            signupName.setError("השם לא יכול להיות ריק");
            return; // Return early if name is empty
        }
        if (Id.isEmpty()){
            signupId.setError("התעודת זהות לא יכולה להיות ריקה");
            return; // Return early if ID is empty
        }
        if (Id.length()!=9||(!(isIsraeliIdNumber(Id)))){
            signupId.setError("התעודת זהות לא תקינה");
            return; // Return early if ID is empty
        }

        DatabaseReference waitingListRef = FirebaseDatabase.getInstance().getReference("Waiting-list");

        // Check in "Waiting-list" database
        checkIfIdExists(waitingListRef, Id,"Id", new IdCheckCallback() {
            @Override
            public void onIdChecked(boolean exists) {
                if (exists) {
                    // ID exists, show error
                    signupId.setError("תעודת הזהות כבר קיימת במערכת");
                } else {
                    // ID doesn't exist in "Waiting-list", check the other database
                    DatabaseReference otherDbRef = FirebaseDatabase.getInstance().getReference("organization");

                    // Check in the other database
                    checkIfIdExists(otherDbRef, Id,"Id", new IdCheckCallback() {
                        @Override
                        public void onIdChecked(boolean exists) {
                            if (exists) {
                                // ID exists, show error
                                signupId.setError("תעודת הזהות כבר קיימת במערכת");
                            } else {

                                // ID doesn't exist in both databases, proceed with sign-up
                                createUser(email, pass, name, Id);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkIfIdExists(DatabaseReference ref, String id,String whatCheck, IdCheckCallback callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orgSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot roleSnapshot : orgSnapshot.getChildren()) {
                        for (DataSnapshot data : roleSnapshot.getChildren()) {
                            String existingId = data.child("Id").getValue(String.class);
                            if (existingId != null && existingId.equals(id)) {
                                // ID exists, invoke callback with true
                                callback.onIdChecked(true);
                                return;
                            }
                        }
                    }
                }
                // ID doesn't exist, invoke callback with false
                callback.onIdChecked(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    interface IdCheckCallback {
        void onIdChecked(boolean exists);
    }


    private void createUser(String email, String pass, String name, String Id) {
        // Proceed with creating the user
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign-up successful, continue with adding user data to database
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Waiting-list")
                            .child(selectedValue) // Add user under selected organization
                            .child(selectedValue2) // Add user under selected level
                            .push(); // Push user data with a unique ID
                    Calendar calendar = Calendar.getInstance();

                    // Get the current date in Date object
                    Date date = calendar.getTime();

                    // Format the date into string
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    // Create a HashMap to store user data
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("Id", Id);
                    userData.put("email", email);
                    userData.put("time", dateFormat.format(date));
                    // Add other user data as needed

                    // Push user data to the database
                    usersRef.setValue(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // User data added to database successfully
                                        Toast.makeText(Signup.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Signup.this, MainActivity.class));
                                    } else {
                                        // Failed to add user data to database
                                        Toast.makeText(Signup.this, "Failed to add user data to database: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Sign-up failed
                    Toast.makeText(Signup.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static boolean isIsraeliIdNumber(String id) {
        id = id.trim();
        if (id.length() > 9 || !id.matches("\\d+")) return false;
        id = id.length() < 9 ? ("00000000" + id).substring(id.length()) : id;

        int sum = 0;
        for (int i = 0; i < id.length(); i++) {
            int digit = Character.getNumericValue(id.charAt(i));
            int step = digit * ((i % 2) + 1);
            sum += (step > 9) ? step - 9 : step;
        }

        return sum % 10 == 0;
    }
}
