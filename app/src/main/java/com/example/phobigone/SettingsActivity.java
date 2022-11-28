package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    Integer step = 5, min = 0, max = 5;
    private boolean notifications;
    private boolean sound;
    private Integer time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> settings = dbHelper.readRowFromTable("SELECT notifications, sound, exp_train_time FROM Setting;");
        this.notifications = settings.get(0).equals("1");
        this.sound = settings.get(1).equals("1");
        this.time = Integer.valueOf(settings.get(2));

        Switch notificationSwitch = findViewById(R.id.switch_not);
        notificationSwitch.setChecked(this.notifications);
        Switch soundSwitch = findViewById(R.id.switch_sound);
        soundSwitch.setChecked(this.sound);

        //Intent intent = getIntent();
        SeekBar sbar = findViewById(R.id.seekBar);
        sbar.setMin(min);
        sbar.setMax(max);
        sbar.setProgress((this.time - 5)/5);
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
                        SettingsActivity.this.time = 5 * sbar.getProgress() + 5;
                    }
                }
        );

        ListView badgesLv = findViewById(R.id.badges);
        ArrayList<Badge> badges = dbHelper.getEarnedBadges();
        BadgeListAdapter badgeAdapter = new BadgeListAdapter(this, R.layout.badge_adapter_layout, badges);
        badgesLv.setAdapter(badgeAdapter);


    }

    @Override
    protected void onDestroy() {
        Switch notificationSwitch = findViewById(R.id.switch_not);
        this.notifications = notificationSwitch.isChecked();
        Switch soundSwitch = findViewById(R.id.switch_sound);
        this.sound = soundSwitch.isChecked();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.saveSettings(this.notifications, this.sound, this.time);
        super.onDestroy();
    }
}
