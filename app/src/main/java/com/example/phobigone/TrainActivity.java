package com.example.phobigone;

import static com.example.phobigone.MainActivity.DELAY;
import static com.example.phobigone.MainActivity.IMAGES_TO_DISPLAY;
import static com.example.phobigone.MainActivity.TOTAL_IMAGES;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.HashMap;

public class TrainActivity extends AppCompatActivity {
    Integer seenImages = -1;

    private Uri[] randVids = new Uri[IMAGES_TO_DISPLAY];
    private ImageView spiderImg;
    private VideoView spiderVid;
    private Integer level;
    private Boolean sound;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting intent information
        level = getIntent().getIntExtra("level", 1);
        Integer[] randImgs = new Integer[IMAGES_TO_DISPLAY];

        Integer i = 0;

        // selection of the layout to be used for the selected train level
        // levels 1 and 3 should display images, while levels 2 and 4 should display videos
        if (level==1 || level==3) {
            setContentView(R.layout.activity_images);
            spiderImg = findViewById(R.id.spider_img);

            // array defining the random images to be displayed on the selected train level
            while (i<IMAGES_TO_DISPLAY) {
                Integer newRand = getRandomNumber(0.5, TOTAL_IMAGES+0.5);

                // getting the ids of the resources that will be displayed
                String idStr = "@drawable/level" + String.valueOf(level) + "_train_" + String.valueOf(newRand);
                Integer id = getResources().getIdentifier(idStr, null, getPackageName());

                // prevents image repetition in the same train session
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

            // array defining the random videos to be displayed on the selected train level
            while (i < IMAGES_TO_DISPLAY) {
                // random selection
                Integer newRand = getRandomNumber(1, TOTAL_IMAGES);

                // getting the paths for the resources that will be displayed
                String idStr = "@raw/level" + String.valueOf(level) + "_train_" + String.valueOf(newRand);
                Integer id = getResources().getIdentifier(idStr, null, getPackageName());
                Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + id);

                // prevents image repetition in the same train session
                boolean repeated = Arrays.asList(randVids).contains(path);
                if (!repeated) {
                    randVids[i] = path;
                    i++;
                }
            }
        }

        this.handler = new Handler();
        this.runnable = new Runnable() {
            public void run() {
                seenImages++;
                if(seenImages>IMAGES_TO_DISPLAY-1) {
                    endTrain();
                    return;
                }

                // showing a new image or video
                if (level==1 || level==3) {
                    spiderImg.setImageResource(randImgs[seenImages]);
                } else {
                    spiderVid.setVideoURI(randVids[seenImages]);
                    spiderVid.start();
                }
                handler.postDelayed(this, DELAY);  // time period to display the image or video
            }
        };
        handler.postDelayed(runnable, 0);  // time period before the image or video is displayed (set to 0)

        // since the video is running in a separate thread, the user can always
        // click the available button and give up from the train session
        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener(vw -> endTrain());
    }

    // sets the videos' volume
    private void setVolumeControl(MediaPlayer mp) {
        if(sound) {
            mp.setVolume(1F, 1F); // unmute
        } else {
            mp.setVolume(0F, 0F); // mute
        }
    }

    // defines an intent to the results activity, with the necessary information to display the
    // train statistics
    private void endTrain() {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, TrainResultsActivity.class);
        intent.putExtra("seenImages", seenImages);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    // random number generator (uniform distribution)
    public int getRandomNumber(double min, double max) {
        return (int) Math.round((Math.random() * (max - min)) + min);
    }

    @Override
    public void onBackPressed() {
        endTrain();
    }
}