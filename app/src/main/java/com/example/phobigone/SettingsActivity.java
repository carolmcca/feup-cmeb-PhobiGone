package com.example.phobigone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    Integer step = 5, min = 0, max = 5;
    private String deviceName;
    private String deviceId;
    private boolean notifications;
    private boolean sound;
    private Integer time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        HashMap<String, String> settings = dbHelper.getSettings();
        this.deviceName = settings.get("device_name");
        this.notifications = settings.get("notifications").equals("1");
        this.sound = settings.get("sound").equals("1");
        this.time = Integer.valueOf(settings.get("exp_train_time"));

        displayDeviceName();
        ListView devicesLv = findViewById(R.id.bluetooth_devices);
        ArrayList<Badge> devicesForm = new ArrayList<>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null)
        {
            if (mBluetoothAdapter.isEnabled())
            {
                // Listing paired devices
                Set<BluetoothDevice> paired_devices = mBluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : paired_devices)
                {
                    devicesForm.add(new Badge(device.getName(), device.getAddress(), "bluetooth_icon"));
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            }
        }


        BadgeListAdapter deviceAdapter = new BadgeListAdapter(this, R.layout.badge_adapter_layout, devicesForm);
        devicesLv.setAdapter( deviceAdapter );

        devicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick( AdapterView<?> parent, View item, int position, long id)
            {
                SettingsActivity.this.deviceName = deviceAdapter.getItem(position).getTitle();
                SettingsActivity.this.deviceId = deviceAdapter.getItem(position).getDescription();
                displayDeviceName();
            }
        });

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
        dbHelper.saveSettings(this.deviceId, this.deviceName, this.notifications, this.sound, this.time);
        super.onDestroy();
    }

    private void displayDeviceName() {
        TextView deviceNameTv = findViewById(R.id.device_name);
        if(!(deviceName==null))
            deviceNameTv.setText(this.deviceName);
    }
}
