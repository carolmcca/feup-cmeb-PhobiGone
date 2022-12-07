package com.example.phobigone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class TestVidActivity extends AppCompatActivity {
    static Integer numVideos = 6;
    static Integer delay = 10000;
    static Integer totalVideos = 15;
    Integer seenVideos = -1;
    private static final double SDRR_THRESHOLD = 0.6;
    private static final double RMSRR_THRESHOLD = 27.9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        VideoView spiderVid = (VideoView) findViewById(R.id.spider_vid);
        Intent intent = getIntent();
        Integer level = intent.getIntExtra("level", 2);
        MainActivity.btService.resetRr();
        Uri[] randVids = new Uri[numVideos];

        Integer i = 0;
        while (i < numVideos) {
            Integer newRand = getRandomNumber(1, totalVideos);
            String idStr = "@raw/level" + String.valueOf(level) + "_test_" + String.valueOf(newRand);
            Integer id = getResources().getIdentifier(idStr, null, getPackageName());
            Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + id);
            boolean repeated = Arrays.asList(randVids).contains(path);
            if (!repeated) {
                randVids[i] = path;
                i++;
            }
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                seenVideos++;
                if (seenVideos > randVids.length - 1) {
                    double sdrr = MainActivity.btService.getSDRR();
                    double rmsrr = MainActivity.btService.getRMSRR();
                    if (level==2 && sdrr<SDRR_THRESHOLD && rmsrr<RMSRR_THRESHOLD)
                        nextLevel(level);
                    else
                        endTrain(level);
                    return;
                }
                spiderVid.setVideoURI(randVids[seenVideos]);
                spiderVid.start();
                handler.postDelayed(this, delay);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v) -> onBtClick(runnable, handler, level));
    }

    private void endTrain(Integer level) {
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", seenVideos);
        intent.putExtra("numContent", numVideos);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private void nextLevel(Integer level) {
        Intent intent = new Intent(this, RelaxActivity.class);
        intent.putExtra("level", level+1);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler, Integer level) {
        handler.removeCallbacks(runnable);
        endTrain(level);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}