package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends Fragment {

    User user;
    TextView userName,
            idUser,
            email;
    ImageView logout;
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
        else
        {

        }
        if(user.getLevel().equals("מנהל-ת"))
        {
            setTopMargin(view, 35);
        }
        String name = user.getUserName();
        userName =  view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        idUser = view.findViewById(R.id.idUser);
        userName.setText(name);
        email.setText(user.getEmail());
        idUser.setText(user.getIdUser());
        logout = view.findViewById(R.id.logoutIv);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }

    private void logout() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.remove("userID"); // remove another specific entry if needed
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