package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang,listObject2Fix;
    ImageView detailImage;
    TaskList TL = new TaskList();
    FloatingActionButton deleteButton;
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
        listObject2Fix = findViewById(R.id.listObject);
        backButton = findViewById(R.id.backIcon);
        if(getIntent().getExtras() != null) {
            detail = (DataClass) getIntent().getSerializableExtra("detail");
            detailDesc.setText(detail.getDescription());
            detailTitle.setText("class: " +detail.getNumClass());
            detailLang.setText(detail.getTime());
            if(detail.listObject()==null||detail.listObject().isEmpty())
            {
                listObject2Fix.setText("לא נבחרה אופציה");

            }
            else {
                listObject2Fix.setText(detail.listObject());
            }
            key = detail.getKey();
            Role = detail.getRole();
            currentUser = detail.getCurrentUser();
            imageUrl = detail.getImageUrl();

            if(!imageUrl.equals("dont use image"))
            {
                Glide.with(this).load(imageUrl).into(detailImage);
            }
        }

        if (currentUser.getLevel()==null||!currentUser.getLevel().equals("אב-בית")) {

            floatingActionMenu.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view)
           {
               Intent intent = new Intent(DetailActivity.this, TaskList.class);
               finish();
           }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("task");
                String[] parts = key.split("-");
                String NumClass = parts[0];
                String id = parts[1];
                if(imageUrl != null && !imageUrl.equals("dont use image")) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storageReference.delete();
                }
                FirebaseDatabase.getInstance().getReference("open-task").child(currentUser.getOrg()).child(NumClass).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,TL).commit();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
    }


}