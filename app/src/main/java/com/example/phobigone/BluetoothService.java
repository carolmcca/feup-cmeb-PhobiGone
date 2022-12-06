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
    //TODO: remove this
    /*private List<Long> peak = new ArrayList<>();
    private List<Integer> bpm = new ArrayList<>();
    private List<Integer> bpmi = new ArrayList<>();*/
    private List<Float> rr = new ArrayList<>();
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == BioLib.MESSAGE_PEAK_DETECTION) {
                BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                //TODO:remove this
                /*peak.add(qrs.position);
                bpmi.add(qrs.bpmi);
                bpm.add(qrs.bpm);*/
                rr.add(Float.valueOf(qrs.rr));
            }
        }
    };


    public BluetoothService(Context appContext) {
        this.appContext = appContext;
    }

    public void run() {
        /*Init BioLib*/
        try {
            lib = new BioLib (appContext, mHandler);
            Toast.makeText(appContext,"Init BioLib", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(appContext,"Error to init BioLib", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        /*Connect to VitalJacket*/
        DatabaseHelper dbHelper = new DatabaseHelper(appContext);
        String address = dbHelper.getAddress();
        try {
            lib.Connect(address, 5);
            Toast.makeText(appContext,"Connected", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(appContext,"Error to connect", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public double getSDRR() {
        float mean = mean(rr);
        List diffSquares = new ArrayList();
        for(Float rr_value : rr) {
            diffSquares.add(Math.pow((rr_value-mean), 2));
        }
        return Math.sqrt(mean(diffSquares));
    }

    public double getRMSRR() {
        List squares = new ArrayList();
        for(Float rr_value : rr) {
            squares.add(Math.pow(rr_value, 2));
        }
        return Math.sqrt(mean(squares));
    }

    private float mean(List<Float> list) {
        float sum = 0;
        for(Float value : list) {
            sum += value;
        }
        return sum/rr.size();
    }

    public void resetRr() {
        this.rr.clear();
    }
}
