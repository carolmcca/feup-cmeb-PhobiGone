package com.example.phobigone;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;
import Bio.Library.namespace.BioLib;
import android.os.Handler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BluetoothService implements Serializable {
    private BioLib lib = null;
    private Context appContext;
    private List<Double> rr = new ArrayList();
    //Handles bluetooth messages from Vital Jacket (adds the incoming rr to the ArrayList rr)
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == BioLib.MESSAGE_PEAK_DETECTION) {
                BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                rr.add((double) qrs.rr);
            }
        }
    };


    public BluetoothService(Context appContext) {
        this.appContext = appContext;
    }

    public void run() {
        //Init BioLib
        try {
            lib = new BioLib (appContext, mHandler);
            Toast.makeText(appContext,"Init BioLib", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(appContext,"Error to init BioLib", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //Connect to VitalJacket
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        String address = dbHelper.getAddress();
        try {
            lib.Connect(address, 1);
            Toast.makeText(appContext,"Connected", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(appContext,"Error to connect", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //Get the SDRR (called at the end of each test level)
    public double getSDRR() {
        double mean = mean(rr);
        List diffSquares = new ArrayList();
        for(double rr_value : rr) {
            diffSquares.add(Math.pow((rr_value-mean), 2));
        }
        return Math.sqrt(mean(diffSquares));
    }

    //Get the RMSRR (called at the end of each test level)
    public double getRMSRR() {
        List squares = new ArrayList();
        for(double rr_value : rr) {
            squares.add(Math.pow(rr_value, 2));
        }
        return Math.sqrt(mean(squares));
    }

    //Calculate the mean of the values on a List
    private double mean(List<Double> list) {
        double sum = 0;
        for(double value : list) {
            sum += value;
        }
        return sum/rr.size();
    }

    //Delete all elements on the rr List (called in the beginning of each level)
    public void resetRr() {
        this.rr.clear();
    }
}
