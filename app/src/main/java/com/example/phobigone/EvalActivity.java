package com.example.phobigone;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import Bio.Library.namespace.BioLib;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EvalActivity extends AppCompatActivity {
    private BioLib lib = null;
    List<Long> peak = new ArrayList<>();
    List<Integer> bpm = new ArrayList<>();
    List<Integer> bpmi = new ArrayList<>();
    List<Integer> rr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval);

        /*Init BioLib*/
        try {
            lib = new BioLib (this, mHandler);
            Toast.makeText(getApplicationContext(),"Init BioLib", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error to init BioLib", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        /*Connect to VitalJacket*/
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String address = dbHelper.getAddress();

        try {
            lib.Connect(address, 5);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error to connect", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
        Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();

    }
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == BioLib.MESSAGE_PEAK_DETECTION) {
                BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                peak.add(qrs.position);
                bpmi.add(qrs.bpmi);
                bpm.add(qrs.bpm);
                rr.add(qrs.rr);
            }
           else {
                Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
