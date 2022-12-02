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

        btLevel1.setOnClickListener((View v)->onBtClick(1));
        btLevel2.setOnClickListener((View v)->onBtClick(2));
        btLevel3.setOnClickListener((View v)->onBtClick(3));
        btLevel4.setOnClickListener((View v)->onBtClick(4));
    }
    private void onBtClick(int level) {
        Intent intent = new Intent();
        if (level==1 || level==3) {
            intent = new Intent (this, TrainImagesActivity.class);
        } else {
            intent = new Intent(this, TrainVideosActivity.class);
        }
        intent.putExtra("level", level);

        startActivity(intent);
    }

}
