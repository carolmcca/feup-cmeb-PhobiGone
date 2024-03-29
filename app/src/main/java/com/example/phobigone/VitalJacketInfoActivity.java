package com.example.phobigone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class VitalJacketInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_jacket_info);

        //Set and on click listener to the starting test button
        Button startTrainBtn = findViewById(R.id.startTestBtn);
        startTrainBtn.setOnClickListener(vw -> {
            //Connect to Vital Jacket and start recieving data
            MainActivity.btService.run();
            //Start RelaxActivity (first activity of the test)
            Intent intent = new Intent(this, RelaxActivity.class);
            intent.putExtra("level", 1);
            startActivity(intent);
        });
    }
}