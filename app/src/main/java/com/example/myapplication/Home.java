package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    Button button;
    int selectedInt = 1500;
    Spinner numberSpinner;
    ArrayAdapter<String> adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=  inflater.inflate(R.layout.fragment_home, container, false);
        button =view.findViewById(R.id.building500);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
        return view;
    }

    private void showAlertDialog() {

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_alert_dialog);
        EditText nameEt = dialog.findViewById(R.id.description);
        final CheckBox terms = dialog.findViewById(R.id.terms_cb);
        createSpinner(dialog);
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

            String userName = nameEt.getText().toString();

            if (!userName.isEmpty()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("task").child(String.valueOf(selectedInt));

                Map<String, Object> childData = new HashMap<>();
                childData.put("Description", userName);
                databaseReference.setValue(childData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Data saved to Firebase", Toast.LENGTH_SHORT).show();
                            // Handle success, if needed
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            // Handle failure, if needed
                        });
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    public void createSpinner(Dialog dialog) {
        numberSpinner = dialog.findViewById(R.id.spinner_class);

        // Create a list of numbers from 0 to 16
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i <= 16; i++) {
            numbers.add(String.valueOf(i));
        }

        // Create an ArrayAdapter using the list of numbers
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, numbers);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        numberSpinner.setAdapter(adapter);

    }
}