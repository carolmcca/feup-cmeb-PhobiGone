package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestResultsActivity extends AppCompatActivity {
    Integer numLevels = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_results);

        Integer seenContent = getIntent().getIntExtra("seenContent", 0);
        Double numContent = (double) getIntent().getIntExtra("numContent", 1);
        Integer level = getIntent().getIntExtra("level", 1);

        TextView textLevel = findViewById(R.id.textLevel);
        textLevel.setText("Level " + String.valueOf(level));

        TextView imageSeen = findViewById(R.id.imageSeen);
        imageSeen.setText(String.valueOf((int)(seenContent + numContent*(level-1))) + " Images");

        TextView imagePercentage = findViewById(R.id.imagePercentage);
        imagePercentage.setText(String.valueOf(Math.round((seenContent + numContent*(level-1))/(numLevels*numContent)*100)) + "%");

        Button btHome = findViewById(R.id.homeButton);
        Button btStats = findViewById(R.id.statsButton);

        btHome.setOnClickListener((View v)->onBtHomeClick());
        btStats.setOnClickListener((View v)->onBtStatsClick());

        Button homeBt = findViewById(R.id.homeButton);
        Button StatsBt = findViewById(R.id.statsButton);

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
