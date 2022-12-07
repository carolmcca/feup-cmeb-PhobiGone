package com.example.phobigone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class TestActivity extends AppCompatActivity {
    static Integer numImages = 6;
    static Integer delay = 2000;
    static Integer totalImages = 15;
    Integer seenImages = -1;
    private static final double SDRR_THRESHOLD = 0.6;
    private static final double RMSRR_THRESHOLD = 27.9;
    Integer[] randImgs = new Integer[numImages];
    Uri[] randVids = new Uri[numImages];
    ImageView spiderImg;
    VideoView spiderVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer level = getIntent().getIntExtra("level", 1);
        MainActivity.btService.resetRr();
        Integer i = 0;

        if (level==1 || level==3) {
            setContentView(R.layout.activity_images);
            spiderImg = findViewById(R.id.spider_img);

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
        } else {
            setContentView(R.layout.activity_videos);
            spiderVid = (VideoView) findViewById(R.id.spider_vid);

            while (i < numImages) {
                Integer newRand = getRandomNumber(1, totalImages);
                String idStr = "@raw/level" + String.valueOf(level) + "_test_" + String.valueOf(newRand);
                Integer id = getResources().getIdentifier(idStr, null, getPackageName());
                Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + id);
                boolean repeated = Arrays.asList(randVids).contains(path);
                if (!repeated) {
                    randVids[i] = path;
                    i++;
                }
            }
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                seenImages++;
                if(seenImages>numImages-1) {
                    double sdrr = MainActivity.btService.getSDRR();
                    double rmsrr = MainActivity.btService.getRMSRR();
                    if (level!=4 && sdrr<SDRR_THRESHOLD && rmsrr<RMSRR_THRESHOLD)
                        nextLevel(level);
                    else
                        endTest(level);
                    return;
                }

                if (level==1 || level==3) {
                    spiderImg.setImageResource(randImgs[seenImages]);
                } else {
                    spiderVid.setVideoURI(randVids[seenImages]);
                    spiderVid.start();
                }
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