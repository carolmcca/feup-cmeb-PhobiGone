package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Integer step = 5, min = 0, max = 5, def_progress = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Intent intent = getIntent();
        SeekBar sbar = findViewById(R.id.seekBar);
        sbar.setMin(min);
        sbar.setMax(max);
        sbar.setProgress(def_progress);
        TextView prog = findViewById(R.id.txt_progress);
        prog.setText((5 * sbar.getProgress() + 5) + " minutes");

        sbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        prog.setText((5 * sbar.getProgress() + 5) + " minutes");
                    }
                }
        );
    }
}
