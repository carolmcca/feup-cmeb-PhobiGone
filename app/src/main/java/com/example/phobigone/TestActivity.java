package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button btLevel1 = findViewById(R.id.bt_level1);
        Button btLevel2 = findViewById(R.id.bt_level2);
        Button btLevel3 = findViewById(R.id.bt_level3);
        Button btLevel4 = findViewById(R.id.bt_level4);

        btLevel1.setOnClickListener((View v)->onBtClick(btLevel1.getId()));
        btLevel2.setOnClickListener((View v)->onBtClick(btLevel2.getId()));
        btLevel3.setOnClickListener((View v)->onBtClick(btLevel3.getId()));
        btLevel4.setOnClickListener((View v)->onBtClick(btLevel4.getId()));
    }
    private void onBtClick(int id) {
        Intent intent = new Intent (this, LevelActivity.class);
        intent.putExtra("level", id);

        startActivity(intent);
    }

}
