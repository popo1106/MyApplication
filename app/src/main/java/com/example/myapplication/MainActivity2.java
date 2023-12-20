package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {
    SharedPreferences.Editor editor;
    Profile profile = new Profile();
    TaskList TL = new TaskList();
    Home home = new Home();
    SharedPreferences sp;
    BottomNavigationView BNV;


    String name;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
        BNV = findViewById(R.id.bottomNavigation);
        BNV.setSelectedItemId(R.id.bottom_home);

        BNV.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;

            }else if (id == R.id.bottom_profile) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("name",name);
                bundle1.putString("password",password);
                profile.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profile).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if (id == R.id.bottom_list) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, TL).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            }
            return false;
        });
    }

    public void logout(View view) {
        sp = getSharedPreferences("checkBox",MODE_PRIVATE);
        sp = getSharedPreferences("checkBox",MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("remember","false");
        editor.apply();
        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        startActivity(intent);
    }
}