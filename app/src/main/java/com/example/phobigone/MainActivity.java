package com.example.phobigone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "phobigone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String aux = dbHelper.readRowFromTable("SELECT notifications FROM Setting;").get(0);
        boolean notificationsEnabled = (dbHelper.readRowFromTable("SELECT notifications FROM Setting;")).get(0).equals("1");
        if (notificationsEnabled)
            scheduleNotification(buildNotification());
        else
            cancelNotification();

        Button btTrain = findViewById(R.id.bt_train);
        Button btTest = findViewById(R.id.bt_test);
        Button btStats = findViewById(R.id.bt_stats);
        Button btFacts = findViewById(R.id.bt_facts);

        btTrain.setOnClickListener((View v)->onBtClick(btTrain.getId()));
        btTest.setOnClickListener((View v)->onBtClick(btTest.getId()));
        btStats.setOnClickListener((View v)->onBtClick(btStats.getId()));
        btFacts.setOnClickListener((View v)->onBtClick(btFacts.getId()));

        /*Button not = findViewById(R.id.bt_not);
        not.setOnClickListener(vw -> scheduleNotification(buildNotification()));*/
        
    }

    private void scheduleNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //long future = SystemClock.elapsedRealtime() + 5000;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, future, pendingIntent);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);  //set repeating every 24 hours

    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
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
        Intent intent;

        switch (id) {
            case R.id.bt_train:
                intent = new Intent(this, TrainActivity.class);
                break;
            case R.id.bt_test:
                intent = new Intent(this, TestImgActivity.class);
                intent.putExtra("level", 1);
                break;
            case R.id.bt_stats:
                intent = new Intent(this, StatsActivity.class);
                break;
            default:
                intent = new Intent(this, CuriousFactsActivity.class);
        }

        startActivity(intent);
    }
}