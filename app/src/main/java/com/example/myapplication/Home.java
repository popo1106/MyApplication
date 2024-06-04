package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    private FrameLayout frameLayout;
    private Bitmap cachedBackgroundBitmap;

    User user;
    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_PERMISSION = 200;
    Uri selectedImageUri;
    private AlertDialog noInternetDialog;
    String imageUrl,
            urgencyLevel,
            buildingNameString,
            formattedDateTime,
            description,
            descriptionPla,
            selectedInt = "1500";
    StringBuilder selectedOptions ;
    boolean[] checkedItems;
    String[] listItems;
    ArrayList<Integer> selectedItems = new ArrayList<>();
    int flagImage;

    Spinner numberSpinner,urgencySpinner;
    ImageView selectOptionsButton,myimage;
    TextView selectedOptionsTextView;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        frameLayout = view.findViewById(R.id.frameLayout);
        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("name");
        }

        if (user != null && user.getLevel().equals("מנהל-ת")) {
            setTopMargin(view, 40);
        }

        flagImage = 0;
        selectedOptions = new StringBuilder();

        // Load background image if not cached
        if (cachedBackgroundBitmap == null) {
            loadBackgroundImageFromFirebase();
        } else {
            // Set cached background image
            frameLayout.setBackground(new BitmapDrawable(getResources(), cachedBackgroundBitmap));
        }

        frameLayout.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                // Get the width and height of the screen
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                // Calculate scaling factors
                float scaleX = (float) screenWidth / 568; // assuming your original width is 568px
                float scaleY = (float) screenHeight / 694; // assuming your original height is 694px

                // Scale the touch coordinates
                float scaledX = touchX / scaleX;
                float scaledY = touchY / scaleY;

                Log.d("TouchTest", "Scaled Touch coordinates: X = " + scaledX + ", Y = " + scaledY);
                getBuildingData(scaledX, scaledY);
            }
            return false;
        });
        monitorInternetConnection();

        return view;
    }

    private void getBuildingData(float x, float y)
    {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();


        mDatabase.getReference().child("organization").child(user.getOrg()).child("building").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Assuming there's only one child node under "building"

                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {


                    // Get building name
                    String buildingName = buildingSnapshot.getKey();
                    // Get coordinates array [l,t,r,b]
                    String coordinatesString = buildingSnapshot.child("cord").getValue(String.class);

                    // Remove brackets "[" and "]" and split the string to extract individual coordinates
                    String[] coordinatesArray = coordinatesString.substring(1, coordinatesString.length() - 1).split(",");

                    // Parse coordinates to integers
                    int left = Integer.parseInt(coordinatesArray[0]);
                    int top = Integer.parseInt(coordinatesArray[1]);
                    int right = Integer.parseInt(coordinatesArray[2]);
                    int bottom = Integer.parseInt(coordinatesArray[3]);

                    if(buildingName.equals("600B"))
                    {
                        buildingName = "600";
                    }
                    if (isWithinBounds(x, y, left, right, top, bottom)) {
                        if(isNumeric(buildingName)) {
                            showAlertDialog(Integer.valueOf(buildingName));
                        }
                        else{
                            showAlertDialogString(buildingName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.w("DatabaseError", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    public void uploadData()
    {
        if(selectedInt.equals("1500"))
        {
            DataClass dataClass = new DataClass(user.getUserName(),description,formattedDateTime,imageUrl, user.getLevel(),buildingNameString,user,"לא נבחרה אופציה",urgencySpinner.getSelectedItem().toString(),descriptionPla, "עדיין פתוח" , "עדיין פתוח");
        }
        else {
            DataClass dataClass = new DataClass(user.getUserName(), description, formattedDateTime, imageUrl, user.getLevel(), String.valueOf(selectedInt), user, selectedOptions.toString(), urgencySpinner.getSelectedItem().toString(), "לא נבחרה אופציה","עדיין פתוח" , "עדיין פתוח");
        }
    }
    private void setupUrgencySpinner(Dialog dialog) {
        urgencySpinner = dialog.findViewById(R.id.spinner_urgency);

        List<String> urgencyLevels = new ArrayList<>();
        urgencyLevels.add("High");
        urgencyLevels.add("Medium");
        urgencyLevels.add("Low");

        ArrayAdapter<String> urgencyAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, urgencyLevels) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                setUrgencyTextColor(view, position);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                setUrgencyTextColor(view, position);
                return view;
            }

            private void setUrgencyTextColor(View view, int position) {
                TextView textView = (TextView) view;
                switch (position) {
                    case 0:
                        textView.setTextColor(Color.RED);
                        break;
                    case 1:
                        textView.setTextColor(Color.parseColor("#FFA500")); // Orange color
                        break;
                    case 2:
                        textView.setTextColor(Color.GREEN);
                        break;
                }
            }
        };

        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urgencySpinner.setAdapter(urgencyAdapter);

        urgencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                urgencyLevel = urgencyLevels.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                urgencyLevel = "Low"; // Default to low urgency if nothing is selected
            }
        });
    }

    /**
     *
     * @param building
     */

    private  void showAlertDialogString(String building)
    {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_dialog_string);
        EditText descriptionEt = dialog.findViewById(R.id.description);
        EditText descriptionPlace = dialog.findViewById(R.id.descriptionPlace);
        Button uploadImage = dialog.findViewById(R.id.upLoadImage);
        myimage = dialog.findViewById(R.id.myImage);
        setupUrgencySpinner(dialog);


        uploadImage.setOnClickListener(view2 ->{
            checkPermission();
        } );
        Button sumbit = dialog.findViewById(R.id.send);
        sumbit.setOnClickListener(view1 -> {
            description = descriptionEt.getText().toString();
            descriptionPla = descriptionPlace.getText().toString();
            if (!description.isEmpty()) {
                if(!descriptionPla.isEmpty()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("open-task").child(user.getOrg()).child(urgencySpinner.getSelectedItem().toString()).child(building);
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
                    formattedDateTime = now.format(formatter);
                    buildingNameString  = building;
                    // Create a HashMap to store the task data
                    Map<String, Object> newTask = new HashMap<>();
                    newTask.put("Description", description);
                    newTask.put("time", formattedDateTime);
                    newTask.put("name", user.getUserName());
                    newTask.put("email", user.getEmail());
                    newTask.put("role", user.getLevel());
                    newTask.put("Description of place", descriptionPla);
                    newTask.put("object", "לא נבחרה אופציה");

                    // Check if an image was selected
                    if (flagImage == 1) {
                        uploadImageToFirebase(selectedImageUri, newTask, databaseReference, dialog);
                    } else {
                        newTask.put("imageUrl", "dont use image");
                        saveTaskToFirebase(newTask, databaseReference, dialog);
                    }
                    flagImage = 0;
                }
                else {
                    Toast.makeText(requireContext(), "Please enter a description for the place", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        uploadData();
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
        setupUrgencySpinner(dialog);
        selectOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Select Options");
                builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which, boolean isChecked) {
                        if (isChecked) {
                            if (!selectedItems.contains(which)) {
                                selectedItems.add(which);
                            }
                        } else {
                            selectedItems.remove(Integer.valueOf(which));
                        }
                    }
                });

                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectedItems.size(); i++) {
                            selectedOptions.append(listItems[selectedItems.get(i)]);
                            if (i != selectedItems.size() - 1) {
                                selectedOptions.append(", ");
                            }
                        }
                        selectedOptionsTextView.setText("Selected Options: " + selectedOptions.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog3, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog4, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            selectedItems.clear();
                            selectedOptionsTextView.setText("Selected Options: ");
                        }
                    }
                });

                AlertDialog dialog5 = builder.create();
                dialog5.show();
            }
        });
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                selectedInt = numberSpinner.getSelectedItem().toString();
                if (selectedInt.equals("שירותים א") || selectedInt.equals("שירותים ב"))
                {
                    selectedInt =String.valueOf(building)+"-"+selectedInt;
                }
                if(selectedInt.equals("אחר"))
                {
                    selectedInt = String.valueOf(building)+ "-"+"בניין";
                }
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("open-task").child(user.getOrg()).child(urgencySpinner.getSelectedItem().toString()).child(String.valueOf(selectedInt));
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
                formattedDateTime = now.format(formatter);

                // Create a HashMap to store the task data
                Map<String, Object> newTask = new HashMap<>();
                newTask.put("Description", description);
                newTask.put("time", formattedDateTime);
                newTask.put("name", user.getUserName());
                newTask.put("email", user.getEmail());
                newTask.put("role", user.getLevel());
                newTask.put("Description of place", "לא נבחרה אופציה");
                if(selectedOptions.toString()== null||selectedOptions.toString().isEmpty())
                {
                    newTask.put("object", "לא נבחרה אופציה");

                }
                else{
                    newTask.put("object", selectedOptions.toString());
                }

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
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            // Permission is already granted, proceed with gallery access
            selectImage();
        }
    }
    public void createSpinner(Dialog dialog, int building) {
        numberSpinner = dialog.findViewById(R.id.spinner_class);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference buildingRef = database.getReference("organization")
                .child(user.getOrg())
                .child("building")
                .child(String.valueOf(building))
                .child("class");

        buildingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String classesString = dataSnapshot.getValue(String.class);
                    if (classesString != null) {
                        // Remove square brackets and split the string into a list of class names
                        String cleanedClassesString = classesString.replace("[", "").replace("]", "");
                        List<String> numbers = Arrays.asList(cleanedClassesString.split(","));

                        // Create an ArrayAdapter using the list of numbers
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, numbers);

                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // Apply the adapter to the spinner
                        numberSpinner.setAdapter(adapter);
                    } else {
                        Log.e("createSpinner", "The 'class' field is null.");
                    }
                } else {
                    Log.e("createSpinner", "No data found for the specified building.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("createSpinner", "Database error: " + databaseError.getMessage());
            }
        });

        selectOptionsButton = dialog.findViewById(R.id.list_object);
        selectedOptionsTextView = dialog.findViewById(R.id.selectedOptionsTextView);
        listItems = getResources().getStringArray(R.array.options_array); // Define this array in res/values/strings.xml
        checkedItems = new boolean[listItems.length];
    }
    private void uploadImageToFirebase(Uri imageUri, Map<String, Object> newTask, DatabaseReference databaseReference, Dialog dialog) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+user.getOrg()+"/" + System.currentTimeMillis() + ".jpg");

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
    private boolean isWithinBounds(float x, float y,int LEFT, int RIGHT, int TOP, int BOTTOM) {
        return x >= LEFT && x <= RIGHT && y >= TOP && y <= BOTTOM;
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
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    private void loadBackgroundImageFromFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/640037/school.png");
        try {
            File localFile = File.createTempFile("school", "png");

            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                // Cache the loaded background image
                cachedBackgroundBitmap = bitmap;
                frameLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            }).addOnFailureListener(exception -> {
                Toast.makeText(requireContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
    }
    }
    private void showNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            return;
        }

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.noconiction, null);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setCancelable(false);

        noInternetDialog = builder.create();
        noInternetDialog.show();
    }


    private void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    private void monitorInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                requireActivity().runOnUiThread(() -> dismissNoInternetDialog());
            }

            @Override
            public void onLost(@NonNull Network network) {
                requireActivity().runOnUiThread(() -> showNoInternetDialog());
            }
        });
    }
}