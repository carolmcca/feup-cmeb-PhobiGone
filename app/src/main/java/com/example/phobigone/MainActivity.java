package com.example.phobigone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // Global constants
    public static final Integer DELAY = 10000;
    public static final Integer IMAGES_TO_DISPLAY = 6;
    public static final Integer TOTAL_IMAGES = 15;
    public static final double SDRR_THRESHOLD = 0.6;
    public static final double RMSRR_THRESHOLD = 27.9;

    public static final String CHANNEL_ID = "phobigone";
    public static BluetoothService btService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Check if notifications are active
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        HashMap<String, String> settings = dbHelper.getSettings();
        boolean notificationsEnabled = settings.get("notifications").equals("1");
        // If notifications are enabled, schedule one
        if (notificationsEnabled)
            scheduleNotification(buildNotification());
        else // If not cancel the notifications
            cancelNotification();

        // Views of buttons of main Menu
        Button btTrain = findViewById(R.id.bt_train);
        Button btTest = findViewById(R.id.bt_test);
        Button btStats = findViewById(R.id.bt_stats);
        Button btFacts = findViewById(R.id.bt_facts);

        // Respective Listeners to the buttons
        btTrain.setOnClickListener((View v)->onBtClick(btTrain.getId()));
        btTest.setOnClickListener((View v)->onBtClick(btTest.getId()));
        btStats.setOnClickListener((View v)->onBtClick(btStats.getId()));
        btFacts.setOnClickListener((View v)->onBtClick(btFacts.getId()));
        
    }

    private void scheduleNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);  //set repeating every 24 hours

    }

    private Notification buildNotification() {
        // This function is responsible for the display of notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        // Messages to display
        String notTitle = "Daily Reminder";
        String smallNotContent = "Our spiders are missing you!";
        String bigNotContent = "Our spiders are missing you! Join us and overcome your phobia";

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set notification builder with title, text, respective style
        // Set a priority, the channel ID and other notification attributes
        builder.setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(notTitle)
                .setContentText(smallNotContent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigNotContent))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setChannelId(CHANNEL_ID);
        return builder.build();
    }

    private void cancelNotification() {
        // this function is responsible for the cancellation of the notification
        // this happens when the user turns the notifications off

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return(super.onOptionsItemSelected(item));
    }

    private void onBtClick(int id) {
        // This function creates the intent according to the button that is pressed
        Intent intent;

        switch (id) {
            case R.id.bt_train:
                intent = new Intent(this, LevelActivity.class);
                break;
            case R.id.bt_test:
                btService = new BluetoothService(getApplicationContext());
                intent = new Intent(this, VitalJacketInfoActivity.class);
                break;
            case R.id.bt_stats:
                intent = new Intent(this, StatsActivity.class);
                break;
            case R.id.bt_facts:
                intent = new Intent(this, CuriousFactsActivity.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        startActivity(intent);
    }
}