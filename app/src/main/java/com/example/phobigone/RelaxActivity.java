package com.example.phobigone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class RelaxActivity extends AppCompatActivity {
    static Integer delay = 10000;
    Integer relaxTimes = -1;
    static Integer numVideos = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        VideoView spiderVid = (VideoView) findViewById(R.id.spider_vid);
        Integer level = getIntent().getIntExtra("level", 1);

        final Handler relax_handler = new Handler();
        Runnable relax_runnable = new Runnable() {
            public void run() {
                relaxTimes++;
                if (relaxTimes > 0) {
                    endRelax(level);
                    return;}
                Integer vidId = getResources().getIdentifier("@raw/relax", null, getPackageName());
                spiderVid.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + vidId));
                spiderVid.start();
                relax_handler.postDelayed(this, delay);  //for interval...
            }
        };
        relax_handler.postDelayed(relax_runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v) -> onBtClick(relax_runnable, relax_handler, level));
    }

    //TODO: test (?)
    private void endTrain(Integer level) {
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", 0);
        intent.putExtra("numContent", numVideos);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler, Integer level) {
        handler.removeCallbacks(runnable);
        endTrain(level);
    }

    private void endRelax(Integer level) {
        Intent intent;
        if (level==1 || level==3) {
            intent = new Intent(this, TestImgActivity.class);
        } else {
            intent = new Intent(this, TestVidActivity.class);
        }
        intent.putExtra("level", level);
        startActivity(intent);
    }
}