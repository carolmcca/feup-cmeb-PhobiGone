package com.example.phobigone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.HashMap;

public class TrainActivity extends AppCompatActivity {
    static Integer numImages = 6;
    static Integer delay = 2000;
    static Integer totalImages = 15;
    Integer seenImages = -1;
    Uri[] randVids = new Uri[numImages];
    ImageView spiderImg;
    VideoView spiderVid;
    Integer level;
    Boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        level = getIntent().getIntExtra("level", 1);
        Integer[] randImgs = new Integer[numImages];
        Integer i = 0;

        if (level==1 || level==3) {
            setContentView(R.layout.activity_images);
            spiderImg = findViewById(R.id.spider_img);
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
        } else {
            setContentView(R.layout.activity_videos);
            spiderVid = (VideoView) findViewById(R.id.spider_vid);

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            HashMap<String, String> settings = dbHelper.getSettings();
            sound = settings.get("sound").equals("1");
            MediaController mc = new MediaController(this);
            spiderVid.setMediaController(mc);
            spiderVid.setOnPreparedListener(mp -> setVolumeControl(mp));
            mc.hide();

            while (i < numImages) {
                Integer newRand = getRandomNumber(1, totalImages);
                String idStr = "@raw/level" + String.valueOf(level) + "_train_" + String.valueOf(newRand);
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
                    endTrain();
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
        btExit.setOnClickListener((View v)->onBtClick(runnable, handler));
    }

    private void setVolumeControl(MediaPlayer mp) {
        if(sound) {
            mp.setVolume(1F, 1F);
        } else {
            mp.setVolume(0F, 0F);
        }
    }

    private void endTrain() {
        Intent intent = new Intent(this, TrainResultsActivity.class);
        intent.putExtra("seenImages", seenImages);
        intent.putExtra("numImages", numImages);
        intent.putExtra("level", level);
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