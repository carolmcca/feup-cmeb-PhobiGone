package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {
    static Integer numImages = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        ImageView spiderImg = findViewById(R.id.spider_img);

        int[] ids = {R.drawable.badge1,R.drawable.setts,R.drawable.badge1};

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i=-1;
            public void run() {
                i++;
                if(i>ids.length-1) {
                    endTrain();
                    return;
                }
                spiderImg.setImageResource(ids[i]);
                handler.postDelayed(this, 2000);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0);  //for interval...

        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v)->onBtClick(runnable, handler));
    }

    private void endTrain() {
        Intent intent = new Intent(this, TrainResultsActivity.class);
        startActivity(intent);
    }

    private void onBtClick(Runnable runnable, Handler handler) {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, TrainResultsActivity.class);
        startActivity(intent);
    }
}