package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class TestImgActivity extends AppCompatActivity {
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
            String idStr = "@drawable/level" + String.valueOf(level) + "_test_" + String.valueOf(newRand);
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
                    nextLevel(level);
                    return;
                }
                spiderImg.setImageResource(randImgs[seenImages]);
                handler.postDelayed(this, delay);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v)->onBtClick(runnable, handler, level));
    }

    private void nextLevel(Integer level) {
        Intent intent = new Intent(this, TestVidActivity.class);
        intent.putExtra("level", level+1);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler, Integer level) {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", seenImages);
        intent.putExtra("numContent", numImages);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    public int getRandomNumber(double min, double max) {
        return (int) Math.round((Math.random() * (max - min)) + min);
    }
}