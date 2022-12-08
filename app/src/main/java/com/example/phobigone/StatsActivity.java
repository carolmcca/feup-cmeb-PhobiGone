package com.example.phobigone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Get Views from Ids
        Spinner spinnerOptions= (Spinner) findViewById(R.id.spinnerOptions);
        // SET SPINNER OPTIONS
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerOptions.setAdapter(spinnerAdapter);

        spinnerOptions.setOnItemClickListener(this::onItemClick);

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

    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        // Select dates chosen
        // FROM DATABASE
        // SELECT date, train_time FROM Train WHERE date>DATA DA OPÇÃO

        GraphView numDailyHoursGraph=findViewById(R.id.hoursGraph);
        //GraphView levelEvolutionGraph=findViewById(R.id.levelEvol);
        //GraphView trainTimeGraph=findViewById(R.id.trainTime);

        //DatabaseHelper database=new DatabaseHelper("phobiGone.db");
        //View chosen option
        String chosenOption = view.toString();

        String fromdate="0000/00/00";
        //String queryTrain ="SELECT date AS totalTrain FROM Train GROUP BY date WHERE date>"
        //String queryTrain ="SELECT date AS totalTrain FROM Train GROUP BY date WHERE date>"
        //String queryTest ="SELECT date AS totalTrain FROM Train GROUP BY date WHERE date>"
        int numDays=0;
        switch(chosenOption) {
            case "Last 30 Days":
                fromdate=getDateToCheckFrom(30);
                break;
            case "Last 7 Days":
                fromdate=getDateToCheckFrom(7);
                break;
        }
        //queryTrain += fromdate;

        List<List<String>> trainingData;
        // Get Expected Training Time
        //List<String> expTrainTimeStr= database.readRowFromTable("SELECT exp_train_time FROM Setting")
        //int expTrainTime= Integer.parseInt( expTrainTimeStr.get(0));

        //DataPoint [] baseline = getLineOfExpTrainTime(values.size(),expTrainTime);
    }
    private DataPoint[] getLineOfExpTrainTime(int size,int value) {
        // size- number of Days
        // value - Expected training time
        DataPoint[] dataPoints = new DataPoint[size];
        for (int point = 0; point < size; point++){
            DataPoint dp = new DataPoint ((double) point, value);
            dataPoints[point] = dp;
        }
        return dataPoints;
    }

    private String getDateToCheckFrom(int numDays){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1*numDays);
        Date todate = cal.getTime();
        return dateFormat.format(todate);
    }
}
