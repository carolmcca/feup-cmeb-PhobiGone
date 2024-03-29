package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        // each button gives access to a different train level
        Button btLevel1 = findViewById(R.id.bt_level1);
        Button btLevel2 = findViewById(R.id.bt_level2);
        Button btLevel3 = findViewById(R.id.bt_level3);
        Button btLevel4 = findViewById(R.id.bt_level4);

        // defining listeners for the buttons
        btLevel1.setOnClickListener((View v)->onBtClick(1));
        btLevel2.setOnClickListener((View v)->onBtClick(2));
        btLevel3.setOnClickListener((View v)->onBtClick(3));
        btLevel4.setOnClickListener((View v)->onBtClick(4));
    }

    // settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    // defines an intent to the settings activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return(super.onOptionsItemSelected(item));
    }

    // defines an intent to start the train session on the correct level
    private void onBtClick(int level) {
        Intent intent = new Intent(this, TrainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }
}
