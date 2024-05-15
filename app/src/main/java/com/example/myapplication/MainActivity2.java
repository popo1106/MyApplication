package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity2 extends AppCompatActivity {
    SharedPreferences.Editor editor;
    Profile profile = new Profile();
    TaskList TL = new TaskList();
    Home home = new Home();
    SharedPreferences sp;
    BottomNavigationView BNV;
    User user;
    Toolbar toolbar;


    String name;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(getIntent().getExtras() != null) {
            user = (User) getIntent().getSerializableExtra("user");
        }
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if(!(user.getLevel().equals("מנהל-ת")))
        {
            getSupportActionBar().hide();

        }
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("name",user);
        home.setArguments(bundle2);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
        BNV = findViewById(R.id.bottomNavigation);
        BNV.setSelectedItemId(R.id.bottom_home);

        BNV.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("name",user);
                home.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;

            }else if (id == R.id.bottom_profile) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("name",user);
                profile.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profile).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if (id == R.id.bottom_list) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("name",user);
                TL.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, TL).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;
            }
            return false;
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.waitingList)
        {

            Intent intent = new Intent(this, listWaiting.class);
            intent.putExtra("user", user);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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