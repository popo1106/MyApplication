package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

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
    User user;
    TextView noTasksTextView;

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
        noTasksTextView = view.findViewById(R.id.noTasksTextView); // Add this line
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        Bundle bundle = this.getArguments();
        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("name");
        }
        if(user.getLevel().equals("מנהל-ת"))
        {
            setTopMargin(view, 15);
        }
        dataList = new ArrayList<>();
        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("open-task").child(user.getOrg());
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot3 : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : itemSnapshot3.getChildren()) {
                        for (DataSnapshot itemSnapshot2 : itemSnapshot.getChildren()) {
                            if (itemSnapshot2.getValue() != null) {
                                // Accessing the second element of the array

                                // Extracting values
                                String description = itemSnapshot2.child("Description").getValue(String.class);
                                String imageUrl = itemSnapshot2.child("imageUrl").getValue(String.class);
                                String name = itemSnapshot2.child("name").getValue(String.class);
                                String time = itemSnapshot2.child("time").getValue(String.class);
                                String role = itemSnapshot2.child("role").getValue(String.class);
                                String listObject = itemSnapshot2.child("object").getValue(String.class);

                                // Create a DataClass object
                                Log.e("lol3",itemSnapshot3.getKey().toString());
                                DataClass dataClass = new DataClass(name, description, time, imageUrl, role, itemSnapshot.getKey().toString(), user, listObject,itemSnapshot3.getKey().toString());
                                dataClass.setKey(itemSnapshot.getKey().toString() + "-" + itemSnapshot2.getKey().toString());
                                dataList.add(dataClass);

                                // Now, you can use the dataClass object as needed
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                // Check if dataList is empty and show/hide noTasksTextView
                if (dataList.isEmpty()) {
                    noTasksTextView.setVisibility(View.VISIBLE);
                } else {
                    noTasksTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
        Button btnApplyFilter = view.findViewById(R.id.btnFilter);

        btnApplyFilter.setOnClickListener(v -> {
            // Inflate the custom layout for the AlertDialog
            View dialogView = inflater.inflate(R.layout.filter_dialog, null);

            // Find the CheckBoxes in the dialogView
            CheckBox checkOpen = dialogView.findViewById(R.id.checkOpen);
            CheckBox checkClosed = dialogView.findViewById(R.id.checkClosed);
            CheckBox checkHigh = dialogView.findViewById(R.id.checkHigh);
            CheckBox checkMedium = dialogView.findViewById(R.id.checkMedium);
            CheckBox checkLow = dialogView.findViewById(R.id.checkLow);

            // Add listeners to manage checkbox states
            CompoundButton.OnCheckedChangeListener closedTaskListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Disable urgency checkboxes if "Closed Tasks" is checked
                        checkHigh.setEnabled(false);
                        checkMedium.setEnabled(false);
                        checkLow.setEnabled(false);
                    } else {
                        // Enable urgency checkboxes if "Closed Tasks" is unchecked
                        checkHigh.setEnabled(true);
                        checkMedium.setEnabled(true);
                        checkLow.setEnabled(true);
                    }
                }
            };

            CompoundButton.OnCheckedChangeListener urgencyListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Disable "Closed Tasks" checkbox if any urgency checkbox is checked
                        checkClosed.setEnabled(false);
                    } else {
                        // Enable "Closed Tasks" checkbox if no urgency checkbox is checked
                        if (!checkHigh.isChecked() && !checkMedium.isChecked() && !checkLow.isChecked()) {
                            checkClosed.setEnabled(true);
                        }
                    }
                }
            };

            checkClosed.setOnCheckedChangeListener(closedTaskListener);
            checkHigh.setOnCheckedChangeListener(urgencyListener);
            checkMedium.setOnCheckedChangeListener(urgencyListener);
            checkLow.setOnCheckedChangeListener(urgencyListener);

            // Create the AlertDialog
            AlertDialog.Builder filterDialogBuilder = new AlertDialog.Builder(requireContext());
            filterDialogBuilder.setView(dialogView)
                    .setTitle("Apply Filters")
                    .setPositiveButton("Apply", (dialogInterface, i) -> {
                        // Handle filter application logic here
                        boolean isOpenChecked = checkOpen.isChecked();
                        boolean isClosedChecked = checkClosed.isChecked();
                        boolean isHighChecked = checkHigh.isChecked();
                        boolean isMediumChecked = checkMedium.isChecked();
                        boolean isLowChecked = checkLow.isChecked();

                        // Example logic: Displaying selected filters as a toast
                        StringBuilder filters = new StringBuilder("Selected filters:\n");
                        if (isOpenChecked) {
                            if(!(isHighChecked||isLowChecked||isMediumChecked)) {
                                showOpenTask(dialog);
                            }
                        }
                        if (isClosedChecked) showCloseTask(dialog,isOpenChecked) ;
                        if (isHighChecked) {
                            dataList.clear();
                            showUrgency(dialog, "High");
                        }
                        if (isMediumChecked)
                        {
                            if(!isHighChecked)
                            {
                                dataList.clear();
                            }
                            showUrgency(dialog, "Medium");
                        }
                        if (isLowChecked)
                        {
                            if(!isHighChecked && !isMediumChecked)
                            {
                                dataList.clear();
                            }
                            showUrgency(dialog, "Low");
                        }


                        Toast.makeText(requireContext(), filters.toString(), Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        return view;
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

    private void showOpenTask(Dialog dialog)
    {
        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("open-task").child(user.getOrg());
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot3 : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : itemSnapshot3.getChildren()) {
                        for (DataSnapshot itemSnapshot2 : itemSnapshot.getChildren()) {
                            if (itemSnapshot2.getValue() != null) {
                                // Accessing the second element of the array

                                // Extracting values
                                String description = itemSnapshot2.child("Description").getValue(String.class);
                                String imageUrl = itemSnapshot2.child("imageUrl").getValue(String.class);
                                String name = itemSnapshot2.child("name").getValue(String.class);
                                String time = itemSnapshot2.child("time").getValue(String.class);
                                String role = itemSnapshot2.child("role").getValue(String.class);
                                String listObject = itemSnapshot2.child("object").getValue(String.class);

                                // Create a DataClass object
                                Log.e("lol3",itemSnapshot3.getKey().toString());
                                DataClass dataClass = new DataClass(name, description, time, imageUrl, role, itemSnapshot.getKey().toString(), user, listObject,itemSnapshot3.getKey().toString());
                                dataClass.setKey(itemSnapshot.getKey().toString() + "-" + itemSnapshot2.getKey().toString());
                                dataList.add(dataClass);

                                // Now, you can use the dataClass object as needed
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                // Check if dataList is empty and show/hide noTasksTextView
                if (dataList.isEmpty()) {
                    noTasksTextView.setVisibility(View.VISIBLE);
                } else {
                    noTasksTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    private void showCloseTask(Dialog dialog, boolean open)
    {
        if(!open)
        {
            dataList.clear();
        }
        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        DatabaseReference closeTaskRef = FirebaseDatabase.getInstance().getReference("close-task").child(user.getOrg());

        dialog.show();
        eventListener = closeTaskRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!open)
                {
                    dataList.clear();
                }
                for (DataSnapshot buildingSnapshot : snapshot.getChildren()) {
                    String buildingName = buildingSnapshot.getKey();
                    Log.e("Building Name", buildingName); // Log for debugging
                    for (DataSnapshot taskSnapshot : buildingSnapshot.getChildren()) {
                        String taskId = taskSnapshot.getKey();
                        Log.e("Task ID", taskId); // Log for debugging
                        String description = taskSnapshot.child("Description").getValue(String.class);
                        String object = taskSnapshot.child("object").getValue(String.class);
                        String whenClose = taskSnapshot.child("when close").getValue(String.class);
                        String whenOpen = taskSnapshot.child("when open").getValue(String.class);
                        String whoClose = taskSnapshot.child("who close").getValue(String.class);
                        String whoOpen = taskSnapshot.child("who open").getValue(String.class);
                        String whoOpenEmail = taskSnapshot.child("who open(email)").getValue(String.class);

                        DataClass dataClass = new DataClass(whoClose, description, whenClose, "dont use image", "נסגר", buildingSnapshot.getKey().toString(), user, object,"no");
                        Log.e("description",description);
                        dataClass.setKey(buildingSnapshot.getKey().toString() + "-" + taskSnapshot.getKey().toString());
                        dataList.add(dataClass);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                // Check if dataList is empty and show/hide noTasksTextView
                if (dataList.isEmpty()) {
                    noTasksTextView.setText("There are no closed tasks");
                    noTasksTextView.setVisibility(View.VISIBLE);
                } else {
                    noTasksTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    private void showUrgency(Dialog dialog, String urgency)
    {

        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        DatabaseReference urgencyTaskRef = FirebaseDatabase.getInstance().getReference("open-task").child(user.getOrg()).child(urgency);
        // Add a ValueEventListener to retrieve the data
        dialog.show();
        urgencyTaskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through the dataSnapshot to access each task
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through each task under the "High" category
                    for (DataSnapshot taskSnapshot : buildingSnapshot.getChildren()) {
                        // Access the task details
                        if (taskSnapshot.getValue() != null) {
                            // Accessing the second element of the array

                            // Extracting values
                            String description = taskSnapshot.child("Description").getValue(String.class);
                            String imageUrl = taskSnapshot.child("imageUrl").getValue(String.class);
                            String name = taskSnapshot.child("name").getValue(String.class);
                            String time = taskSnapshot.child("time").getValue(String.class);
                            String role = taskSnapshot.child("role").getValue(String.class);
                            String listObject = taskSnapshot.child("object").getValue(String.class);

                            // Create a DataClass object
                            DataClass dataClass = new DataClass(name, description, time, imageUrl, role, buildingSnapshot.getKey().toString(), user, listObject,urgency);
                            dataClass.setKey(buildingSnapshot.getKey().toString() + "-" + taskSnapshot.getKey().toString());
                            dataList.add(dataClass);

                            // Now, you can use the dataClass object as needed
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                // Check if dataList is empty and show/hide noTasksTextView
                if (dataList.isEmpty()) {
                    noTasksTextView.setText("There are no open urgent tasks of the type:"+urgency);
                    noTasksTextView.setVisibility(View.VISIBLE);
                } else {
                    noTasksTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                dialog.dismiss();
            }
        });

    }
}