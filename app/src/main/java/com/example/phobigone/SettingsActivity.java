package com.example.phobigone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    Integer min = 0, max = 5;
    private String deviceName;
    private String deviceId;
    private boolean notifications;
    private boolean sound;
    private Integer time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // getting the settings information saved in the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        HashMap<String, String> settings = dbHelper.getSettings();
        this.deviceName = settings.get("device_name");
        this.notifications = settings.get("notifications").equals("1");
        this.sound = settings.get("sound").equals("1");
        this.time = Integer.valueOf(settings.get("exp_train_time"));

        // displaying the selected device name
        displayDeviceName();
        ListView devicesLv = findViewById(R.id.bluetooth_devices);
        ArrayList<Badge> devicesForm = new ArrayList<>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // getting bonded devices
        if (mBluetoothAdapter != null)
        {
            if (mBluetoothAdapter.isEnabled())
            {
                // listing the paired devices
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

        // displaying bonded devices
        BadgeListAdapter deviceAdapter = new BadgeListAdapter(this, R.layout.badge_adapter_layout, devicesForm);
        devicesLv.setAdapter(deviceAdapter);

        // listener to set a new bluetooth device selected by the user
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

        // setting notifications and sound according to the database information
        Switch notificationSwitch = findViewById(R.id.switch_not);
        notificationSwitch.setChecked(this.notifications);
        Switch soundSwitch = findViewById(R.id.switch_sound);
        soundSwitch.setChecked(this.sound);

        // setting the "daily goal" bar, assigning it the goal already saved in the database
        SeekBar sbar = findViewById(R.id.seekBar);
        sbar.setMin(min);
        sbar.setMax(max);
        sbar.setProgress((this.time - 5)/5);
        TextView prog = findViewById(R.id.txt_progress);
        prog.setText((5 * sbar.getProgress() + 5) + " minutes");

        // listener that allows the used to change their daily goal
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

        // ListView that displays the badges earned by the used
        ListView badgesLv = findViewById(R.id.badges);
        ArrayList<Badge> badges = dbHelper.getEarnedBadges();
        BadgeListAdapter badgeAdapter = new BadgeListAdapter(this, R.layout.badge_adapter_layout, badges);
        badgesLv.setAdapter(badgeAdapter);
    }

    // when the user closes the settings menu, all the performed changes are saved
    // in the database for future access
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
