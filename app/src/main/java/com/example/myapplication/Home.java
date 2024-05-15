package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    Button button,button2,button3,button4,button5,button6,button6B;
    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_PERMISSION = 200;
    int selectedInt = 1500;
    Spinner numberSpinner;
    ImageView myimage;
    String name;
    ArrayAdapter<String> adapter;
    Uri selectedImageUri,stImage;
    String imageUrl;
    String description;
    String formattedDateTime;
    User user;
    int flagImage;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=  inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = this.getArguments();
        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("name");
        }
        if(user.getLevel().equals("מנהל-ת"))
        {
            setTopMargin(view, 40);
        }
        name = user.getUserName();
        flagImage=0;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
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
                showAlertDialog(200);
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
        Button uploadImage = dialog.findViewById(R.id.upLoadImage);
        myimage = dialog.findViewById(R.id.myImage);
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
        uploadImage.setOnClickListener(view2 ->{
            checkPermission();
        } );
        Button sumbit = dialog.findViewById(R.id.send);

        sumbit.setOnClickListener(view1 -> {
            description = descriptionEt.getText().toString();
            if (!description.isEmpty()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("task").child(String.valueOf(selectedInt));
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
                formattedDateTime = now.format(formatter);

                // Create a HashMap to store the task data
                Map<String, Object> newTask = new HashMap<>();
                newTask.put("Description", description);
                newTask.put("time", formattedDateTime);
                newTask.put("name", name);

                // Check if an image was selected
                if (flagImage == 1) {
                    uploadImageToFirebase(selectedImageUri, newTask, databaseReference, dialog);
                } else {
                    newTask.put("imageUrl", "dont use image");
                    saveTaskToFirebase(newTask, databaseReference, dialog);
                }
                flagImage=0;
            }
            else {
                Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        uploadData();
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
    private void uploadImageToFirebase(Uri imageUri, Map<String, Object> newTask, DatabaseReference databaseReference, Dialog dialog) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        newTask.put("imageUrl", uri.toString());
                        imageUrl = uri.toString();
                        saveTaskToFirebase(newTask, databaseReference, dialog);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveTaskToFirebase(Map<String, Object> newTask, DatabaseReference databaseReference, Dialog dialog) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long taskCount = dataSnapshot.getChildrenCount();
                String taskId = (taskCount == 0) ? "1" : String.valueOf(taskCount + 1);

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
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            flagImage = 1;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                myimage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            // Permission is already granted, proceed with gallery access
            selectImage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted by the user, access the gallery
                selectImage();
            } else {
                // Permission denied by the user
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void uploadData()
    {

        DataClass dataClass = new DataClass(name,description,formattedDateTime,imageUrl,String.valueOf(selectedInt));
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