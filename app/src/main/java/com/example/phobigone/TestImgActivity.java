package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class TestImgActivity extends AppCompatActivity {
    static Integer numImages = 6;
    static Integer delay = 10000;
    static Integer totalImages = 15;
    Integer seenImages = -1;
    private static final double SDRR_THRESHOLD = 0.6;
    private static final double RMSRR_THRESHOLD = 27.9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        ImageView spiderImg = findViewById(R.id.spider_vid);
        Integer level = getIntent().getIntExtra("level", 1);
        MainActivity.btService.resetRr();
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
                    double sdrr = MainActivity.btService.getSDRR();
                    double rmsrr = MainActivity.btService.getRMSRR();
                    if (sdrr>SDRR_THRESHOLD && rmsrr>RMSRR_THRESHOLD)
                        nextLevel(level);
                    else
                        endTest(level);
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
        Intent intent = new Intent(this, RelaxActivity.class);
        intent.putExtra("level", level+1);
        startActivity(intent);
    }

    private void endTest(Integer level) {
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", seenImages);
        intent.putExtra("numContent", numImages);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler, Integer level) {
        handler.removeCallbacks(runnable);
        endTest(level);
    }

    public int getRandomNumber(double min, double max) {
        return (int) Math.round((Math.random() * (max - min)) + min);
    }
}