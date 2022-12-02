package com.example.phobigone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class TrainVideosActivity extends AppCompatActivity {
    static Integer numVideos = 6;
    static Integer delay = 2000;
    static Integer totalVideos = 15;
    Integer seenVideos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        VideoView spiderVid = (VideoView) findViewById(R.id.spider_vid);
        Integer level = getIntent().getIntExtra("level", 2);
        Uri[] randVids = new Uri[numVideos];

        Integer i = 0;
        while (i < numVideos) {
            Integer newRand = getRandomNumber(1, totalVideos);
            String idStr = "@raw/level" + String.valueOf(level) + "_train_" + String.valueOf(newRand);
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
                    endTrain();
                    return;
                }
                spiderVid.setVideoURI(randVids[seenVideos]);
                spiderVid.start();
                handler.postDelayed(this, delay);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v) -> onBtClick(runnable, handler));
    }

    private void endTrain() {
        Intent intent = new Intent(this, TrainResultsActivity.class);
        intent.putExtra("seenImages", seenVideos);
        intent.putExtra("numImages", numVideos);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler) {
        handler.removeCallbacks(runnable);
        endTrain();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}