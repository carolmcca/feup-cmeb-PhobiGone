package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class TrainImagesActivity extends AppCompatActivity {
    static Integer numImages = 6;
    static Integer delay = 2000;
    static Integer totalImages = 15;
    Integer seenImages = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        ImageView spiderImg = findViewById(R.id.spider_vid);
        Integer level = getIntent().getIntExtra("level", 1);
        Integer[] randImgs = new Integer[numImages];

        Integer i = 0;
        while (i<numImages) {
            Integer newRand = getRandomNumber(0.5, totalImages+0.5);
            String idStr = "@drawable/level" + String.valueOf(level) + "_train_" + String.valueOf(newRand);
            Integer id = getResources().getIdentifier(idStr, null, getPackageName());
            boolean repeated = Arrays.asList(randImgs).contains(id);
            if (!repeated) {
                randImgs[i] = id;
                i++;
            }
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                seenImages++;
                if(seenImages>randImgs.length-1) {
                    endTrain();
                    return;
                }
                spiderImg.setImageResource(randImgs[seenImages]);
                handler.postDelayed(this, delay);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v)->onBtClick(runnable, handler));
    }

    private void endTrain() {
        Intent intent = new Intent(this, TrainResultsActivity.class);
        intent.putExtra("seenImages", seenImages);
        intent.putExtra("numImages", numImages);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler) {
        handler.removeCallbacks(runnable);
        endTrain();
    }

    public int getRandomNumber(double min, double max) {
        return (int) Math.round((Math.random() * (max - min)) + min);
    }
}