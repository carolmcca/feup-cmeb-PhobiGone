package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_results);

        Integer seenImages = getIntent().getIntExtra("seenImages", 0);
        Double numImages = (double) getIntent().getIntExtra("numImages", 1);
        Integer level = getIntent().getIntExtra("level", 1);

        TextView imageSeen = findViewById(R.id.imageSeen);
        imageSeen.setText(String.valueOf(seenImages) + " Images");

        TextView imagePercentage = findViewById(R.id.imagePercentage);
        float img_perc = Math.round(seenImages/numImages*100);
        imagePercentage.setText(String.valueOf(img_perc) + "%");

        TextView congratsMsg = findViewById(R.id.congrats_msg);
        if (img_perc < 25) {
            if (level!=1) {
                congratsMsg.setText("Try to go down a level!");
            }
            else {
                congratsMsg.setText("You need to train harder!");
            }
        }
        else if (img_perc < 100) {
            congratsMsg.setText("You can do better!");
        }
        else {
            if (level!=4) {
                congratsMsg.setText("Step up to the next level!");
            }
            else {
                congratsMsg.setText("Try a full test session!");
            }
        }

        Button btHome = findViewById(R.id.homeButton);
        Button btStats = findViewById(R.id.statsButton);

        btHome.setOnClickListener((View v)->onBtHomeClick());
        btStats.setOnClickListener((View v)->onBtStatsClick());

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addTrain(seenImages*10/60);

        updateBadges(dbHelper);
    }

    private void onBtHomeClick() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void onBtStatsClick() {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    private void updateBadges(DatabaseHelper dbHelper) {
        Integer trainNo = Integer.valueOf(dbHelper.readRowFromTable("SELECT COUNT(*) AS trainNo FROM TRAIN").get(0));
        Integer streak = Integer.valueOf(dbHelper.readRowFromTable("SELECT streak FROM TRAIN ORDER BY date").get(0));

        Map<Integer, Integer> trainBadgeLimits = new HashMap<Integer, Integer>() {{ put(1, 5); put(2, 10); put(3, 20); }};
        Map<Integer, Integer> streakBadgeLimits = new HashMap<Integer, Integer>() {{ put(4, 2); put(5, 4); put(6, 8); put(7, 15); put(8, 30); put(9, 60); }};


        // Streak badges
        ArrayList<Integer> keys = new ArrayList(streakBadgeLimits.keySet());
        Collections.sort(keys);

        for (Integer key : keys) {
            if (streak < streakBadgeLimits.get(key))
                streakBadgeLimits.remove(key);
            else
                break;
        }
        for (Integer badgeId : streakBadgeLimits.keySet()) {
            dbHelper.earnBadge(badgeId);
        }

        // Train badges

        keys = new ArrayList(trainBadgeLimits.keySet());
        Collections.sort(keys);

        for (Integer key : keys) {
            if (trainNo < trainBadgeLimits.get(key))
                trainBadgeLimits.remove(key);
            else
                break;
        }
        for (Integer badgeId : trainBadgeLimits.keySet()) {
            dbHelper.earnBadge(badgeId);
        }

    }
}
