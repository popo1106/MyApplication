package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TaskList extends Fragment {
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    public void onDestroyView() {
        super.onDestroyView();
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("task");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot2 : itemSnapshot.getChildren()) {
                        if (itemSnapshot2.getValue() != null) {
                            // Accessing the second element of the array

                            // Extracting values
                            String description = itemSnapshot2.child("Description").getValue(String.class);
                            String imageUrl = itemSnapshot2.child("imageUrl").getValue(String.class);
                            String name = itemSnapshot2.child("name").getValue(String.class);
                            String time = itemSnapshot2.child("time").getValue(String.class);

                            // Create a DataClass object
                            DataClass dataClass = new DataClass(name, description, time, imageUrl,itemSnapshot.getKey().toString());
                            dataClass.setKey(itemSnapshot.getKey().toString()+"-"+itemSnapshot2.getKey().toString());
                            dataList.add(dataClass);

                            // Now, you can use the dataClass object as needed
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });


        return view;
    }
}