package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class detailOnCloseTask extends AppCompatActivity {
    ImageView backButton;
    TextView detailDesc, detailTitle, detailLang,listObject2Fix,titleObject,whoOpen,whoClose;
    DataClass detail;
    User currentUser;
    String key ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_on_close_task);
        backButton = findViewById(R.id.backIcon);
        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        listObject2Fix = findViewById(R.id.listObject);
        titleObject = findViewById(R.id.titleObject);
        whoOpen = findViewById(R.id.whoOpen);
        whoClose = findViewById(R.id.whoClose);
        ImageView gifImageView = findViewById(R.id.detailImage);
        Glide.with(this)
                .asGif()
                .load(R.drawable.closetask) // Replace with your GIF file
                .listener(new com.bumptech.glide.request.RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Set loop count to play only once
                        resource.setLoopCount(1);
                        return false;
                    }
                })
                .into(gifImageView);
        if (getIntent().getExtras() != null) {
            detail = (DataClass) getIntent().getSerializableExtra("detail");

            detailDesc.setText(detail.getDescription());

            detailTitle.setText("where: " + detail.getNumClass());

            detailLang.setText(calculateDifference());

            whoClose.setText(detail.getWhoClose());

            whoOpen.setText(detail.getUserName());

            if ((detail.listObject() == null || detail.listObject().isEmpty() || detail.listObject().equals("לא נבחרה אופציה"))&&!detail.getDescriptionPlace().equals("לא נבחרה אופציה")) {
                titleObject.setText("Description of place:");
                listObject2Fix.setText(detail.getDescriptionPlace());
            } else {
                listObject2Fix.setText(detail.listObject());
            }

            key = detail.getKey();

            currentUser = detail.getCurrentUser();
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(detailOnCloseTask.this, TaskList.class);
                finish();
            }
        });
    }

    public String calculateDifference() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");

        try {
            Date closeDate = dateFormat.parse(detail.getWhenClose());
            Date openDate = dateFormat.parse(detail.getTime());

            // Calculate the difference in milliseconds
            long diffInMillis = closeDate.getTime() - openDate.getTime();

            // Convert milliseconds to days, hours, minutes, and seconds
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            long days = TimeUnit.SECONDS.toDays(diffInSeconds);
            diffInSeconds -= TimeUnit.DAYS.toSeconds(days);
            long hours = TimeUnit.SECONDS.toHours(diffInSeconds);
            diffInSeconds -= TimeUnit.HOURS.toSeconds(hours);
            long minutes = TimeUnit.SECONDS.toMinutes(diffInSeconds);
            diffInSeconds -= TimeUnit.MINUTES.toSeconds(minutes);
            long seconds = diffInSeconds;

            // Format the difference string
            return String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }

}