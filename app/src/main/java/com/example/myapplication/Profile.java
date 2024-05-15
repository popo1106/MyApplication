package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends Fragment {
    BottomNavigationView BNV;
    SharedPreferences.Editor editor;
    SharedPreferences sp;
    String name;
    User user;
    String password;
    TextView userName,passwordTv,phoneNum;
    ImageView seePassword,logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Bundle bundle = this.getArguments();
        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("name");
        }
        if(user.getLevel().equals("מנהל-ת"))
        {
            setTopMargin(view, 35);
        }
        name = user.getUserName();
        userName =  view.findViewById(R.id.userName);
        if(name == null)
        {
            SharedPreferences sp = requireActivity().getSharedPreferences("checkBox", Context.MODE_PRIVATE);
            name = sp.getString("name","");
            password = sp.getString("password","");
        }
        userName.setText(name);
        seePassword = view.findViewById(R.id.seePassword);
        passwordTv = view.findViewById(R.id.passwordTv);
        phoneNum = view.findViewById(R.id.phoneNumber);
        logout = view.findViewById(R.id.logoutIv);
        usersRef.orderByChild("UserName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the user data
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    if (phoneNumber != null) {
                        // Phone number found for the user
                        phoneNum.setText(phoneNumber);
                    } else {
                        // No phone number found for the user
                        phoneNum.setText("No phone number has been set");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
        seePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }


    public void togglePasswordVisibility() {
        if (!passwordTv.getText().toString().equals("******")) {
            // If the password is currently visible, hide it
            passwordTv.setText("******");
            seePassword.setImageResource(R.drawable.baseline_visibility_off_24); // Change the toggle button icon
        } else {
            // If the password is currently hidden, show it
            passwordTv.setText(password);
            seePassword.setImageResource(R.drawable.baseline_visibility_24); // Change the toggle button icon
        }
    }

    private void logout() {
        SharedPreferences sp = requireActivity().getSharedPreferences("checkBox", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("remember", "false");
        editor.putString("name", "false");
        editor.putString("password", "false");
        editor.apply();

        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the back stack
        startActivity(intent);
        requireActivity().finish(); // Finish the current activity or fragment
    }
    private void setTopMargin(View view, int topMarginDp) {
        // Convert dp to pixels
        int topMarginPx = dpToPx(topMarginDp);

        // Get LayoutParams
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        if (params != null) {
            // Set the top margin
            params.topMargin = topMarginPx;

            // Apply the LayoutParams back to the view
            view.setLayoutParams(params);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}