package com.example.phobigone;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;

public class LevelActivity extends AppCompatActivity {
    static Integer numImages = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        ImageView spiderImg = findViewById(R.id.spider_img);

        Integer[] ids = new Integer[numImages];
        ids[0] = R.drawable.badge1;
        ids[1] = R.drawable.setts;
        ids[2] = R.drawable.badge1;

        spiderImg.setImageResource(ids[0]);

        Integer id = ids[1];

        Handler handler = new Handler();
        Runnable runnableCode = new MyRunnable(id) {
            public void run() {
                spiderImg.setImageResource(id);
            }
        };

        handler.postDelayed(runnableCode, 2000);


        Button btExit = findViewById(R.id.bt_exit);
        btExit.setOnClickListener((View v)->onBtClick());
    }

    private void onBtClick() {
        finish();
    }

    public static abstract class MyRunnable implements Runnable {
        public Integer id;
        public MyRunnable(Integer id){this.id=id;}
        public abstract void run();
    }
}
