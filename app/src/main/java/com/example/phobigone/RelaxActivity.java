package com.example.phobigone;

import static com.example.phobigone.MainActivity.DELAY;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class RelaxActivity extends AppCompatActivity {
    Integer relaxTimes = -1;
    final Handler relax_handler = new Handler();
    Runnable relax_runnable;
    Boolean sound;
    Integer level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        VideoView spiderVid = (VideoView) findViewById(R.id.spider_vid);
        this.level = getIntent().getIntExtra("level", 1);

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
                    endRelax();
                    return;}
                Integer vidId = getResources().getIdentifier("@raw/relax", null, getPackageName());
                spiderVid.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + vidId));
                spiderVid.start();
                relax_handler.postDelayed(this, DELAY);  //for interval...
            }
        };
        relax_handler.postDelayed(relax_runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener(vw -> endTest());
    }

    private void setVolumeControl(MediaPlayer mp) {
        if(sound) {
            mp.setVolume(1F, 1F);
        } else {
            mp.setVolume(0F, 0F);
        }
    }

    private void endTest() {
        this.relax_handler.removeCallbacks(relax_runnable);
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", 0);
        intent.putExtra("level", this.level);
        startActivity(intent);
    }

    private void endRelax() {
        Intent intent = new Intent(this, TestActivity.class);

        intent.putExtra("level", this.level);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        endTest();
    }
}
