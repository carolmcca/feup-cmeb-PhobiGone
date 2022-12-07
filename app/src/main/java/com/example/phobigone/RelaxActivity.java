package com.example.phobigone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class RelaxActivity extends AppCompatActivity {
    static Integer delay = 10000;
    Integer relaxTimes = -1;
    static Integer numVideos = 6;
    final Handler relax_handler = new Handler();
    Runnable relax_runnable;
    Boolean sound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        VideoView spiderVid = (VideoView) findViewById(R.id.spider_vid);
        Integer level = getIntent().getIntExtra("level", 1);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        HashMap<String, String> settings = dbHelper.getSettings();
        sound = settings.get("sound").equals("1");


        MediaController mc = new MediaController(this);
        spiderVid.setMediaController(mc);
        spiderVid.setOnPreparedListener(mp -> setVolumeControl(mp));

        relax_runnable = new Runnable() {
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

    private void setVolumeControl(MediaPlayer mp) {
        if(sound) {
            mp.setVolume(1F, 1F);
        } else {
            mp.setVolume(0F, 0F);
        }
    }

    private void endTest(Integer level) {
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", 0);
        intent.putExtra("numContent", numVideos);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler, Integer level) {
        handler.removeCallbacks(runnable);
        endTest(level);
    }

    private void endRelax(Integer level) {
        Intent intent = new Intent(this, TestActivity.class);
        //intent = new Intent(this, TestVidActivity.class);

        intent.putExtra("level", level);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        relax_handler.removeCallbacks(relax_runnable);
        super.onDestroy();
    }
}
