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

        // getting the view where the video will be displayed
        VideoView spiderVid = (VideoView) findViewById(R.id.spider_vid);

        // getting intent information
        this.level = getIntent().getIntExtra("level", 1);

        // getting information about the sound settings defined by the user
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        HashMap<String, String> settings = dbHelper.getSettings();
        sound = settings.get("sound").equals("1");

        // the MediaControler object will allow to mute or unmute the video
        MediaController mc = new MediaController(this);
        spiderVid.setMediaController(mc);
        spiderVid.setOnPreparedListener(mp -> setVolumeControl(mp));
        mc.hide();

        // a thread that runs in parallel, showing the relaxing video
        relax_runnable = new Runnable() {
            public void run() {
                relaxTimes++;
                if (relaxTimes > 0) { // the relaxing video is only shown once between activities
                    endRelax();
                    return;}
                // path to the relaxing video
                Integer vidId = getResources().getIdentifier("@raw/relax", null, getPackageName());
                spiderVid.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + vidId));
                // starting the video
                spiderVid.start();
                relax_handler.postDelayed(this, DELAY);  //for interval...
            }
        };
        relax_handler.postDelayed(relax_runnable, 0);  // time period before the video is displayed (set to 0)

        // since the video is running in a separate thread, the user can always
        // click the available button and give up from the test session
        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener(vw -> endTest());
    }

    // sets the videos' volume
    private void setVolumeControl(MediaPlayer mp) {
        if(sound) {
            mp.setVolume(1F, 1F); // unmute
        } else {
            mp.setVolume(0F, 0F); // mute
        }
    }

    // removes the parallel threads and defines an intent to the results activity, with the necessary information to display the
    // test statistics
    private void endTest() {
        this.relax_handler.removeCallbacks(relax_runnable);
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", 0);
        intent.putExtra("level", this.level);
        startActivity(intent);
    }

    // defines an intent to the test activity: when the relaxation period ends, a new test level is started
    private void endRelax() {
        Intent intent = new Intent(this, TestActivity.class);

        intent.putExtra("level", this.level);
        startActivity(intent);
    }

    // when the user clicks on the back button, the endTest() method is called
    @Override
    public void onBackPressed() {
        endTest();
    }
}
