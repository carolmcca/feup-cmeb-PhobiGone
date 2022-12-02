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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import Bio.Library.namespace.BioLib;
import android.os.Handler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            //lib.Request(address, 1000);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error to connect", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
        Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                ListView rrLv = findViewById(R.id.rr);
                ArrayAdapter rrAdapter = new ArrayAdapter(EvalActivity.this, android.R.layout.simple_list_item_1, EvalActivity.this.rr);
                rrLv.setAdapter(rrAdapter);
                handler.postDelayed(this, 10000);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0);  //for interval...
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

            /*switch (msg.what)
            {
                case BioLib.MESSAGE_READ:
                    Toast.makeText(getApplicationContext(), "Message read", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_DEVICE_NAME:
                    Toast.makeText(getApplicationContext(), "Connected to", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_BLUETOOTH_NOT_SUPPORTED:
                    Toast.makeText(getApplicationContext(), "Bluetooth NOT supported. Aborting! ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_BLUETOOTH_ENABLED:
                    Toast.makeText(getApplicationContext(), "Bluetooth is now enabled! ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_BLUETOOTH_NOT_ENABLED:
                    Toast.makeText(getApplicationContext(), "Bluetooth not enabled! ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.REQUEST_ENABLE_BT:
                    Toast.makeText(getApplicationContext(), "Request enable ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.STATE_CONNECTING:
                    Toast.makeText(getApplicationContext(), "Connecting to device ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.STATE_CONNECTED:
                    Toast.makeText(getApplicationContext(), "Connecting to device ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.UNABLE_TO_CONNECT_DEVICE:
                    Toast.makeText(getApplicationContext(), "Unable to connect device! ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_DISCONNECT_TO_DEVICE:
                    Toast.makeText(getApplicationContext(), "Device connection was lost", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_RTC:
                    Toast.makeText(getApplicationContext(), "Message RTC", Toast.LENGTH_SHORT).show();

                    break;

                case BioLib.MESSAGE_TIMESPAN:
                    Toast.makeText(getApplicationContext(), "SPAN: ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_PEAK_DETECTION:
                    Toast.makeText(getApplicationContext(), "Peak detection", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_ACC_UPDATED:
                    Toast.makeText(getApplicationContext(), "acc updated", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_ECG_STREAM:
                    Toast.makeText(getApplicationContext(), "ECG stream", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    break;
            }*/
        }
    };

}
