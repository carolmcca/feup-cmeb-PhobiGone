package com.example.phobigone;

import static com.example.phobigone.MainActivity.DELAY;
import static com.example.phobigone.MainActivity.IMAGES_TO_DISPLAY;
import static com.example.phobigone.MainActivity.RMSRR_THRESHOLD;
import static com.example.phobigone.MainActivity.SDRR_THRESHOLD;
import static com.example.phobigone.MainActivity.TOTAL_IMAGES;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.HashMap;

public class TestActivity extends AppCompatActivity {
    Integer seenImages = -1;

    private Integer[] randImgs = new Integer[IMAGES_TO_DISPLAY];
    private Uri[] randVids = new Uri[IMAGES_TO_DISPLAY];
    private ImageView spiderImg;
    private VideoView spiderVid;
    private Boolean sound;
    private Integer level;
    boolean onStress;
    private Runnable runnable;
    private Handler handler;
    private VibrationEffect vibrationEffect;
    long[] pattern = new long[]{600, 600, 600, 600, 600, 600, 600, 600, 600, 600};
    int[] amplitude = new int[]{255, 0, 255, 0, 255, 0, 255, 0, 255, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting intent information
        this.level = getIntent().getIntExtra("level", 1);
        level = 5;

        // deleting previously acquired RR intervals
        MainActivity.btService.resetRr();

        Integer i = 0;

        // selection of the layout to be used for the current test level
        // levels 1 and 3 should display images, while levels 2, 4 and 5 should display videos
        if (level==1 || level==3) {
            setContentView(R.layout.activity_images);
            spiderImg = findViewById(R.id.spider_img);

            // array defining the random images to be displayed on the current test level
            while (i<IMAGES_TO_DISPLAY) {
                // random selection
                Integer newRand = getRandomNumber(0.5, TOTAL_IMAGES+0.5);

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
            while (i < IMAGES_TO_DISPLAY) {
                // random selection
                Integer newRand = getRandomNumber(0.5, TOTAL_IMAGES+0.5);

                // the fifth level uses the same list of videos as the fourth one
                int video_level = level;
                if (video_level == 5) {
                    video_level = 4;
                }

                // getting the paths for the resources that will be displayed
                String idStr = "@raw/level" + String.valueOf(video_level) + "_test_" + String.valueOf(newRand);
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

        this.handler = new Handler();
        // a thread that runs in parallel, showing the relaxing video
        this.runnable = new Runnable() {
            public void run() {
                seenImages++;
                if(seenImages>IMAGES_TO_DISPLAY-1) {
                    // getting sdrr and rmsrr values from the VitalJacket
                    double sdrr = MainActivity.btService.getSDRR();
                    double rmsrr = MainActivity.btService.getRMSRR();

                    // the user can go to the next level if:
                    //      * they haven't already seen all levels AND
                    //      * their ECG values don't reveal excessive stress
                    if (level!=5 && sdrr<SDRR_THRESHOLD && rmsrr<RMSRR_THRESHOLD) {
                        nextLevel();
                    // test finished due to excessive user's stress
                    } else if (sdrr<SDRR_THRESHOLD && rmsrr<RMSRR_THRESHOLD) {
                        onStress = true;
                        endTest();
                    // the user finished the 5 available levels
                    } else {
                        onStress = false;
                        endTest();
                    }
                    return;
                }

                // showing a new image or video
                if (level==1 || level==3) {
                    spiderImg.setImageResource(randImgs[seenImages]);
                } else {
                    spiderVid.setVideoURI(randVids[seenImages]);
                    spiderVid.start();

                    if (level == 5){
                        // in level 5, the phone will vibrate half of the times for 5 seconds
                        int random_vibration = getRandomNumber(0.5, 2.5); // can be 1 or 2 with equal probability
                        if (random_vibration == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                // creating a 6 seconds vibration and canceling any vibrations taking place (none expected)
                                vibrationEffect = VibrationEffect.createWaveform(pattern, amplitude,-1);
                                vibrator.cancel();
                                // applying the 6 seconds vibration
                                vibrator.vibrate(vibrationEffect);
                            }
                        }
                    }
                }

                handler.postDelayed(this, DELAY);  // time period to display the image or video
            }
        };
        handler.postDelayed(runnable, 0); // time period before the image or video is displayed (set to 0)

        // since the video is running in a separate thread, the user can always
        // click the available button and give up from the test session
        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v)->onBtClick());
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
    private void nextLevel() {
        Intent intent = new Intent(this, RelaxActivity.class);
        intent.putExtra("level", this.level+1);
        startActivity(intent);
    }

    // defines an intent to the results activity, with the necessary information to display the
    // test statistics
    private void endTest() {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("seenContent", seenImages);
        intent.putExtra("level", level);
        intent.putExtra("onStress", onStress);
        startActivity(intent);
    }

    // when the user gives up, the onStress is set to false and 'endTest' is called
    private void onBtClick() {
        this.onStress = false;
        endTest();
    }

    // random number generator (uniform distribution)
    public int getRandomNumber(double min, double max) {
        return (int) Math.round((Math.random() * (max - min)) + min);
    }

    @Override
    public void onBackPressed() {
        endTest();
    }
}