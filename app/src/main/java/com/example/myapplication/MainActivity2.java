package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {

    Profile profile = new Profile();
    TaskList TL = new TaskList();
    User user;
    Home home = new Home();
    BottomNavigationView BNV;

    Toolbar toolbar;

    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(getIntent().getExtras() != null) {
            user = (User) getIntent().getSerializableExtra("user");
        }
        else{
            SharedPreferences preferences  = getSharedPreferences("checkbox",MODE_PRIVATE);
            String userID = preferences.getString("userID","");
            user = new User("", "", "", "", ""); // Initialize the User object
            // Fetch and update user data
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.progress_layout);
            builder.setCancelable(false);
            loadingDialog = builder.create();
            getUserById(userID);
        }
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if(!(user.getLevel().equals("מנהל-ת")))
        {
            getSupportActionBar().hide();

        }
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("name",user);
        home.setArguments(bundle2);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
        BNV = findViewById(R.id.bottomNavigation);
        BNV.setSelectedItemId(R.id.bottom_home);

        BNV.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("name",user);
                home.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;

            }else if (id == R.id.bottom_profile) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("name",user);
                profile.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profile).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if (id == R.id.bottom_list) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("name",user);
                TL.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, TL).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            }
            return false;
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.waitingList)
        {

            Intent intent = new Intent(this, listWaiting.class);
            intent.putExtra("user", user);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserById(String idUser) {
        loadingDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("organization");

        // Adding a listener to read the data at the specified path
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userFound = false;

                // Iterate through organizations
                for (DataSnapshot orgSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through levels within each organization
                    for (DataSnapshot levelSnapshot : orgSnapshot.getChildren()) {
                        // Iterate through user IDs within each level
                        for (DataSnapshot userSnapshot : levelSnapshot.getChildren()) {
                            String fetchedIdUser = userSnapshot.child("Id").getValue(String.class);
                            if (fetchedIdUser != null && fetchedIdUser.equals(idUser)) {
                                // Get user data
                                user.setUserName(userSnapshot.child("name").getValue(String.class));
                                user.setEmail(userSnapshot.child("email").getValue(String.class));
                                String level = levelSnapshot.getKey();
                                String org = orgSnapshot.getKey();

                                // Update the User object
                                user.setIdUser(idUser);
                                user.setLevel(level);
                                user.setOrg(org);

                                userFound = true;
                                break;
                            }

                        }
                        if (userFound) break;
                    }
                    if (userFound) break;
                }
                if (userFound) {
                    Log.d("MainActivity", "User: " + user.getUserName());
                    Toast.makeText(MainActivity2.this, "User: " + user.getUserName(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "User not found");
                    Toast.makeText(MainActivity2.this, "User not found", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("MainActivity", "Error retrieving user", databaseError.toException());
                Toast.makeText(MainActivity2.this, "Error retrieving user", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

}