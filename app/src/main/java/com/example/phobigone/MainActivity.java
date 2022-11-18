package com.example.phobigone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btTest = findViewById(R.id.bt_test);
        Button btEval = findViewById(R.id.bt_eval);
        Button btStats = findViewById(R.id.bt_stats);
        Button btFacts = findViewById(R.id.bt_facts);

        btTest.setOnClickListener((View v)->onBtClick(btTest.getId()));
        btEval.setOnClickListener((View v)->onBtClick(btEval.getId()));
        btStats.setOnClickListener((View v)->onBtClick(btStats.getId()));
        btFacts.setOnClickListener((View v)->onBtClick(btFacts.getId()));
    }

    private void onBtClick(int id) {
        Intent intent;

        switch (id) {
            case R.id.bt_test:
                intent = new Intent(this, TestActivity.class);
                break;
            case R.id.bt_eval:
                intent = new Intent(this, EvalActivity.class);
                break;
            case R.id.bt_stats:
                intent = new Intent(this, StatsActivity.class);
                break;
            default:
                intent = new Intent(this, CuriousFactsActivity.class);
        }

        startActivity(intent);
    }
}