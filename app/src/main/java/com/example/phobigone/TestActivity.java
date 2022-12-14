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

public class TestActivity extends AppCompatActivity {
    static Integer numImages = 6;
    static Integer delay = 2000;
    static Integer totalImages = 15;
    Integer seenImages = -1;
    private static final double SDRR_THRESHOLD = 0.6;
    private static final double RMSRR_THRESHOLD = 27.9;
    final Handler handler = new Handler();
    Runnable runnable;
    Integer[] randImgs = new Integer[numImages];
    Uri[] randVids = new Uri[numImages];
    ImageView spiderImg;
    VideoView spiderVid;
    Boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting intent information
        Integer level = getIntent().getIntExtra("level", 1);

        // deleting previously acquired RR intervals
        MainActivity.btService.resetRr();

        Integer i = 0;

        // selection of the layout to be used for the current test level
        // levels 1 and 3 should display images, while levels 2 and 4 should display videos
        if (level==1 || level==3) {
            setContentView(R.layout.activity_images);
            spiderImg = findViewById(R.id.spider_img);

            // array defining the random images to be displayed on the current test level
            while (i<numImages) {
                // random selection
                Integer newRand = getRandomNumber(0.5, totalImages+0.5);

                // getting the ids of the resources that will be displayed
                String idStr = "@drawable/level" + String.valueOf(level) + "_test_" + String.valueOf(newRand);
                Integer id = getResources().getIdentifier(idStr, null, getPackageName());

                // prevents image repetition in the same test session
                boolean repeated = Arrays.asList(randImgs).contains(id);
                if (!repeated) {
                    randImgs[i] = id;
                    i++;
                }
            }
        } else {
            setContentView(R.layout.activity_videos);
            spiderVid = (VideoView) findViewById(R.id.spider_vid);

            // getting information about the sound settings defined by the user
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            HashMap<String, String> settings = dbHelper.getSettings();
            sound = settings.get("sound").equals("1");

            // the MediaControler object will allow to mute or unmute the video
            MediaController mc = new MediaController(this);
            spiderVid.setMediaController(mc);
            spiderVid.setOnPreparedListener(mp -> setVolumeControl(mp));
            mc.hide();

            // array defining the random videos to be displayed on the current test level
            while (i < numImages) {
                // random selection
                Integer newRand = getRandomNumber(1, totalImages);

                // getting the paths for the resources that will be displayed
                String idStr = "@raw/level" + String.valueOf(level) + "_test_" + String.valueOf(newRand);
                Integer id = getResources().getIdentifier(idStr, null, getPackageName());
                Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + id);

                // prevents video repetition in the same test session
                boolean repeated = Arrays.asList(randVids).contains(path);
                if (!repeated) {
                    randVids[i] = path;
                    i++;
                }
            }
        }

        // a thread that runs in parallel, showing the relaxing video
        runnable = new Runnable() {
            public void run() {
                seenImages++;
                if(seenImages>numImages-1) {
                    // getting sdrr and rmsrr values from the VitalJacket
                    double sdrr = MainActivity.btService.getSDRR();
                    double rmsrr = MainActivity.btService.getRMSRR();

                    // the user can go to the next level if:
                    //      * they haven't already seen all levels AND
                    //      * their ECG values don't reveal excessive stress
                    if (level!=4 && (sdrr>SDRR_THRESHOLD || rmsrr>RMSRR_THRESHOLD))
                        nextLevel(level);
                    // test finished due to excessive user's stress
                    else if (sdrr<SDRR_THRESHOLD && rmsrr<RMSRR_THRESHOLD)
                        endTest(level, true);
                    // the finished the 4 available levels
                    else
                        endTest(level, false);
                    return;
                }

                // showing a new image or video
                if (level==1 || level==3) {
                    spiderImg.setImageResource(randImgs[seenImages]);
                } else {
                    spiderVid.setVideoURI(randVids[seenImages]);
                    spiderVid.start();
                }
                handler.postDelayed(this, delay);  // time period to display the image or video
            }
        };
        handler.postDelayed(runnable, 0); // time period before the image or video is displayed (set to 0)

        // since the video is running in a separate thread, the user can always
        // click the available button and give up from the test session
        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v)->onBtClick(runnable, handler, level));
    }

    // sets the videos' volume
    private void setVolumeControl(MediaPlayer mp) {
        if(sound) {
            mp.setVolume(1F, 1F); //unmute
        } else {
            mp.setVolume(0F, 0F); // mute
        }
    }

    // defines an intent for the relax activity, since the user completed the test and
    // is ready to go up a level
    private void nextLevel(Integer level) {
        Intent intent = new Intent(this, RelaxActivity.class);
        intent.putExtra("level", level+1);
        startActivity(intent);
    }

    // defines an intent to the results activity, with the necessary information to display the
    // test statistics
    private void endTest(Integer level, boolean onStress) {
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", seenImages);
        intent.putExtra("numContent", numImages);
        intent.putExtra("level", level);
        intent.putExtra("onStress", onStress);
        startActivity(intent);
    }

    // when the user gives up, the parallel threads are removed and 'endTest' is called
    private void onBtClick(Runnable runnable, Handler handler, Integer level) {
        handler.removeCallbacks(runnable);
        endTest(level, false);
    }

    // random number generator (uniform distribution)
    public int getRandomNumber(double min, double max) {
        return (int) Math.round((Math.random() * (max - min)) + min);
    }

    // when the user clicks on the back button, the paralel threads are removed
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}