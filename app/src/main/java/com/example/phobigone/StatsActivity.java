package com.example.phobigone;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class StatsActivity extends AppCompatActivity {
    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        GraphView numDailyHoursGraph=findViewById(R.id.hoursGraph);
        GraphView levelEvolutionGraph=findViewById(R.id.levelEvol);

        // Get Views from Ids
        Spinner spinnerOptions = (Spinner) findViewById(R.id.spinnerOptions);
        // SET SPINNER OPTIONS
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerOptions.setAdapter(spinnerAdapter);

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                HashMap<String, String> settings = dbHelper.getSettings();
                Integer exp_train_time = Integer.valueOf(settings.get("exp_train_time"));

                numDailyHoursGraph.removeAllSeries();
                levelEvolutionGraph.removeAllSeries();

                //View chosen option
                String chosenOption = ((TextView) selectedItemView).getText().toString();

                Integer numDays=0;
                switch(chosenOption) {
                    case "Last 30 days":
                        numDays = 30;
                        break;
                    case "Last 7 days":
                        numDays = 7;
                        break;
                }

                String fromdate = getDateToCheckFrom(numDays-1);

                DataPoint [] trainTime = getLineOfTrainTime(numDays, fromdate);
                DataPoint [] baseline = getLineOfExpTrainTime(numDays, exp_train_time);

                LineGraphSeries<DataPoint> trainSeries = new LineGraphSeries<>(trainTime);
                LineGraphSeries<DataPoint> baselineSeries = new LineGraphSeries<>(baseline);
                trainSeries.setTitle("Your Trains");
                baselineSeries.setTitle("Your Goal");
                trainSeries.setColor(ContextCompat.getColor(getApplicationContext(), R.color.brown));
                baselineSeries.setColor(ContextCompat.getColor(getApplicationContext(), R.color.darkBrown));

                numDailyHoursGraph.getLegendRenderer().setVisible(true);
                numDailyHoursGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                numDailyHoursGraph.getLegendRenderer().setMargin(40);

                numDailyHoursGraph.getViewport().setMaxX(numDays + 5);
                numDailyHoursGraph.getViewport().setMinX(0);
                numDailyHoursGraph.getViewport().setXAxisBoundsManual(true);
                numDailyHoursGraph.addSeries(trainSeries);
                numDailyHoursGraph.addSeries(baselineSeries);

                DataPoint [] level = getLineOfLevel(numDays, fromdate);

                LineGraphSeries<DataPoint> levelSeries = new LineGraphSeries<>(level);
                levelSeries.setTitle("Your Level");
                levelSeries.setColor(ContextCompat.getColor(getApplicationContext(), R.color.brown));
                levelSeries.setDrawDataPoints(true);
                levelSeries.setDataPointsRadius(10f);

                levelEvolutionGraph.getLegendRenderer().setVisible(true);
                levelEvolutionGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                levelEvolutionGraph.getLegendRenderer().setMargin(40);

                levelEvolutionGraph.getViewport().setMaxX(numDays + 5);
                //levelEvolutionGraph.getViewport().setMinX(1);
                levelEvolutionGraph.getViewport().setXAxisBoundsManual(true);
                levelEvolutionGraph.getViewport().setMaxY(4.5);
                levelEvolutionGraph.getViewport().setMinY(0);
                levelEvolutionGraph.getViewport().setYAxisBoundsManual(true);
                levelEvolutionGraph.addSeries(levelSeries);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //spinnerOptions.setOnItemSelectedListener((Spinner spinner)-> onItemSelected(selectedView));
    }

    private DataPoint[] getLineOfExpTrainTime(int size, int value) {
        // size- number of Days
        // value - Expected training time
        DataPoint[] dataPoints = new DataPoint[size];
        for (int point = 0; point < size; point++){
            DataPoint dp = new DataPoint (point+1, value);
            dataPoints[point] = dp;
        }
        return dataPoints;
    }

    private DataPoint[] getLineOfTrainTime(int size, String fromdate) {
        // size- number of Days
        // fromdate - first day to be considered
        float daily_time;
        DataPoint[] dataPoints = new DataPoint[size];
        for (int point = 0; point < size; point++){
            List <String> train_time = dbHelper.readRowFromTable("SELECT train_time FROM Train WHERE date='" + fromdate + "'");
             if (train_time.size() == 0) {
                 daily_time = 0;
             } else {
                 daily_time = Float.valueOf(train_time.get(0));
             }

            DataPoint dp = new DataPoint (point+1, daily_time);
            dataPoints[point] = dp;
            fromdate = getDateToCheckFrom(size - point - 1);
        }
        return dataPoints;
    }

    private DataPoint[] getLineOfLevel(int size, String fromdate) {
        // size- number of Days
        // fromdate - first day to be considered
        float daily_level;
        DataPoint[] dataPoints = new DataPoint[size];
        Integer filledPoints = 0;
        for (int point = 0; point < size; point++){
            List <String> test_level = dbHelper.readRowFromTable("SELECT AVG(level) FROM Test WHERE date='" + fromdate + "'");

            if (test_level.get(0) != null) {
                daily_level = Float.valueOf(test_level.get(0));
                DataPoint dp = new DataPoint (point+1, daily_level);
                dataPoints[filledPoints] = dp;
                filledPoints++;
            }
            fromdate = getDateToCheckFrom(size - point - 2);
        }
        DataPoint[] validPoints = new DataPoint[filledPoints];
        System.arraycopy(dataPoints, 0, validPoints, 0, filledPoints);
        return validPoints;
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
