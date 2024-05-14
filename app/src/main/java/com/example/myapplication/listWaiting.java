package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class listWaiting extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapterWaiting adapter;
    private List<waitingCardAp> dataList;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private AlertDialog dialog;
    private ImageView deleteButton;
    private TextView emptyView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_waiting);
        recyclerView = findViewById(R.id.recyclerViewWaiting);
        emptyView = findViewById(R.id.emptyView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();
        dataList = new ArrayList<>();
        adapter = new MyAdapterWaiting(this, dataList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Waiting-list").child("640037");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();// Clear the list before adding new data

                // Iterate through each role
                for (DataSnapshot roleSnapshot : dataSnapshot.getChildren()) {
                    String role = roleSnapshot.getKey(); // Get the role name
                    // Iterate through each id under the role
                    for (DataSnapshot idSnapshot : roleSnapshot.getChildren()) {
                        String id = idSnapshot.getKey(); // Get the id
                        // Iterate through the user attributes under the id
                        Map<String, String> userData = new HashMap<>();
                        for (DataSnapshot attributeSnapshot : idSnapshot.getChildren()) {
                            String attributeName = attributeSnapshot.getKey();
                            String attributeValue = attributeSnapshot.getValue(String.class);
                            if (attributeValue != null) {
                                userData.put(attributeName, attributeValue); // Store attribute name-value pair
                            }
                        }

                        // Extract desired data (name, idUser, email, time)
                        String name = userData.get("name");
                        String idUser = userData.get("Id");
                        String email = userData.get("email");
                        String time = userData.get("time");
                        waitingCardAp waitingCard = new waitingCardAp("שם: "+name, "תפקיד: "+role, "תאריך: "+time,"תז: "+idUser);
                        waitingCard.setKey(role + "@" + id);

                        dataList.add(waitingCard);
                        // Print or use the extracted data as needed
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
//                if (dataList.isEmpty()) {
//                    emptyView.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                } else {
//                    emptyView.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                dialog.dismiss();
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener when the activity is destroyed
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}