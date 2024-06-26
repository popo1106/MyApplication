package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang,listObject2Fix,titleObject;
    ImageView detailImage;
    FloatingActionButton deleteButton,editButton;
    String imageUrl = "";
    String key ="";
    String Role;
    DataClass detail;
    User currentUser;
    ImageView backButton;
    FloatingActionMenu floatingActionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        floatingActionMenu = findViewById(R.id.option);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        listObject2Fix = findViewById(R.id.listObject);
        titleObject = findViewById(R.id.titleObject);
        backButton = findViewById(R.id.backIcon);
        if (getIntent().getExtras() != null) {
            detail = (DataClass) getIntent().getSerializableExtra("detail");
            detailDesc.setText(detail.getDescription());
            detailTitle.setText("where: " + detail.getNumClass());
            detailLang.setText(detail.getTime());
            if ((detail.listObject() == null || detail.listObject().isEmpty() || detail.listObject().equals("לא נבחרה אופציה"))&&!detail.getDescriptionPlace().equals("לא נבחרה אופציה")) {
                titleObject.setText("Description of place:");
                listObject2Fix.setText(detail.getDescriptionPlace());
            } else {
                listObject2Fix.setText(detail.listObject());
            }
            key = detail.getKey();
            Role = detail.getRole();
            currentUser = detail.getCurrentUser();
            imageUrl = detail.getImageUrl();

            if (!imageUrl.equals("dont use image")) {
                Glide.with(this).load(imageUrl).into(detailImage);
            }
        }

        if (currentUser.getLevel() == null || !currentUser.getLevel().equals("אב-בית")) {

            floatingActionMenu.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, alarmmanager.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DeleteAction", "Urgency: " + detail.getUrgency());

                String[] parts = key.split("@");
                String NumClass = parts[0];
                String id = parts[1];

                DatabaseReference openTaskRef = FirebaseDatabase.getInstance().getReference("open-task").child(currentUser.getOrg()).child(detail.getUrgency()).child(NumClass).child(id);
                DatabaseReference closeTaskRef = FirebaseDatabase.getInstance().getReference("close-task").child(currentUser.getOrg()).child(NumClass);

                openTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("DeleteAction93", "Urgency: " + detail.getUrgency());
                        if (dataSnapshot.exists()) {
                            Map<String, Object> taskData = (Map<String, Object>) dataSnapshot.getValue();

                            // Prepare data for close-task
                            Map<String, Object> closeTaskData = new HashMap<>();
                            closeTaskData.put("Description", taskData.get("Description"));
                            closeTaskData.put("object", taskData.get("object"));
                            closeTaskData.put("Description of place", taskData.get("Description of place"));
                            closeTaskData.put("who close", currentUser.getUserName());
                            closeTaskData.put("when close", getCurrentDateTime());
                            closeTaskData.put("when open", taskData.get("time"));
                            closeTaskData.put("who open", taskData.get("name"));
                            closeTaskData.put("who open(email)", taskData.get("email"));
                            Log.e("DeleteAction23", "Urgency: " + detail.getUrgency());

                            // Retrieve the last task number
                            closeTaskRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int nextTaskNumber = 1; // Default to 1 if no tasks exist

                                    if (snapshot.exists()) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            String lastTaskKey = child.getKey();
                                            nextTaskNumber = Integer.parseInt(lastTaskKey) + 1;
                                        }
                                    }

                                    // Add the data to close-task under the next available number
                                    closeTaskRef.child(String.valueOf(nextTaskNumber)).setValue(closeTaskData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Delete the task from open-task after adding to close-task
                                            if (imageUrl != null && !imageUrl.equals("dont use image")) {
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                                storageReference.delete();
                                            }
                                            openTaskRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(DetailActivity.this, "Task moved to close-task and deleted from open-task", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(DetailActivity.this, "Failed to delete task from open-task", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DetailActivity.this, "Failed to add task to close-task", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(DetailActivity.this, "Failed to retrieve the last task number", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DetailActivity.this, "Failed to read task data", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("DeleteAction113", "Urgency: " + detail.getUrgency());
            }

        });


    }
        private String getCurrentDateTime () {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
            return now.format(formatter);
        }

    }