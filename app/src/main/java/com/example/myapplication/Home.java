package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    Button button,button2,button3,button4,button5,button6,button6B;
    int selectedInt = 1500;
    Spinner numberSpinner;
    String name;
    ArrayAdapter<String> adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=  inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = this.getArguments();
        name = bundle.getString("name");
        button =view.findViewById(R.id.building100);
        button2 =view.findViewById(R.id.building200);
        button3 =view.findViewById(R.id.building300);
        button4 =view.findViewById(R.id.building400);
        button5 =view.findViewById(R.id.building500);
        button6 =view.findViewById(R.id.building600);
        button6B =view.findViewById(R.id.building600Part2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(100);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(2000);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(300);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(400);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(500);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(600);
            }
        });
        button6B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(600);
            }
        });
        return view;
    }

    private void showAlertDialog(int building) {

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_alert_dialog);
        EditText descriptionEt = dialog.findViewById(R.id.description);
        final CheckBox terms = dialog.findViewById(R.id.terms_cb);
        createSpinner(dialog,building);
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the selected number from the Spinner
                String selectedNumber = adapter.getItem(position);
                selectedInt = Integer.parseInt(selectedNumber);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(requireContext(),"you need to chose class",Toast.LENGTH_SHORT ).show();
            }

        });
        Button sumbit = dialog.findViewById(R.id.send);
        sumbit.setOnClickListener(view1 -> {

            String description = descriptionEt.getText().toString();
            if (!description.isEmpty()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("task").child(String.valueOf(selectedInt));
// Get the current date and time
                LocalDateTime now = LocalDateTime.now();

// Format the date and time as per your requirement
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss"); // Customize the format
                String formattedDateTime = now.format(formatter);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long taskCount = dataSnapshot.getChildrenCount(); // Get the count of existing tasks
                        String taskId;

                        if (taskCount == 0) {
                            taskId = "1"; // If no tasks exist, start from 1
                        } else {
                            // Get the last task's key and increment by 1 for the new taskId
                            long nextTaskId = taskCount + 1;
                            taskId = String.valueOf(nextTaskId);
                        }
                        // Create a HashMap to store the task data
                        Map<String, Object> newTask = new HashMap<>();
                        newTask.put("Description", description);
                        newTask.put("time", formattedDateTime);
                        newTask.put("name", name);

                        // Push the new task to the Firebase database under "Number-class"
                        databaseReference.child(taskId).setValue(newTask)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Data saved to Firebase", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }else {
                Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    public void createSpinner(Dialog dialog, int building) {
        numberSpinner = dialog.findViewById(R.id.spinner_class);

        // Create a list of numbers from 0 to 16
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i <= 16; i++) {
            numbers.add(String.valueOf(building+i));
        }

        // Create an ArrayAdapter using the list of numbers
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, numbers);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        numberSpinner.setAdapter(adapter);

    }
}