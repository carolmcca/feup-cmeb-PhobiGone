package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TrainResultsActivity extends AppCompatActivity {
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_results);

        Button btHome = findViewById(R.id.homeButton);
        Button btStats = findViewById(R.id.statsButton);

<<<<<<< Updated upstream
        btHome.setOnClickListener((View v)->onBtClick(btHome.getId()));
        btStats.setOnClickListener((View v)->onBtClick(btStats.getId()));
    }

    private void onBtClick(int id) {
        Intent intent;

        switch (id) {
            case R.id.homeButton:
                intent = new Intent(this, MainActivity.class);
                break;
            default:
                intent = new Intent(this, StatsActivity.class);
        }

        startActivity(intent);
=======
        btHome.setOnClickListener((View v)->onBtHomeClick());
        btStats.setOnClickListener((View v)->onBtStatsClick());
    }
    private void onBtHomeClick() {
        Intent intent = new Intent(this, MainActivity.class);
    }
    private void onBtStatsClick() {
        Intent intent = new Intent(this, StatsActivity.class);
>>>>>>> Stashed changes
    }
}
