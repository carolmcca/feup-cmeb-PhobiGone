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

        TextView imageSeen = findViewById(R.id.imageSeen);
        imageSeen.setText(String.valueOf((int)(seenContent + numContent*(level-1))) + " Images");

        TextView imagePercentage = findViewById(R.id.imagePercentage);
        float img_perc = Math.round((seenContent + numContent*(level-1))/(numLevels*numContent)*100);
        imagePercentage.setText(String.valueOf(img_perc) + "%");

        TextView textLevel = findViewById(R.id.textLevel);
        if (img_perc != 100) {
            textLevel.setText("Level " + String.valueOf(level));
        }
        else {
            textLevel.setText("Legend!");
        }

        TextView congratsMsg = findViewById(R.id.congrats_msg);
        if (img_perc < 25) {
            congratsMsg.setText("You need to train harder!");
        }
        else if (img_perc < 50) {
            congratsMsg.setText("You can do better!");
        }
        else if (img_perc < 75) {
            congratsMsg.setText("You should be proud!");
        }
        else if (img_perc < 100) {
            congratsMsg.setText("You are almost fearless!");
        }
        else {
            congratsMsg.setText("Fear mastered!");
        }

        Button btHome = findViewById(R.id.homeButton);
        Button btStats = findViewById(R.id.statsButton);

        btHome.setOnClickListener((View v)->onBtHomeClick());
        btStats.setOnClickListener((View v)->onBtStatsClick());

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addTest(level, img_perc);

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
