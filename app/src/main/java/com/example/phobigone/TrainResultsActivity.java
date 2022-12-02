package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_results);

        Integer seenImages = getIntent().getIntExtra("seenImages", 0);
        Double numImages = (double) getIntent().getIntExtra("numImages", 1);

        TextView imageSeen = findViewById(R.id.imageSeen);
        imageSeen.setText(String.valueOf(seenImages) + " Images");

        TextView imagePercentage = findViewById(R.id.imagePercentage);
        imagePercentage.setText(String.valueOf(Math.round(seenImages/numImages*100)) + "%");

        Button btHome = findViewById(R.id.homeButton);
        Button btStats = findViewById(R.id.statsButton);

        btHome.setOnClickListener((View v)->onBtHomeClick());
        btStats.setOnClickListener((View v)->onBtStatsClick());
    }
    private void onBtHomeClick() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void onBtStatsClick() {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }
}
