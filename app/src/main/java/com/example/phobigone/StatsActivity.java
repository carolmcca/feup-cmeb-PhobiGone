package com.example.phobigone;

import android.content.Intent;
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
    // DataBaseHelper Object to access database
    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Get Graph Views from Ids
        GraphView numDailyHoursGraph=findViewById(R.id.hoursGraph);
        GraphView levelEvolutionGraph=findViewById(R.id.levelEvol);

        // Get Spinner View from Ids
        Spinner spinnerOptions = (Spinner) findViewById(R.id.spinnerOptions);

        // SET SPINNER OPTIONS
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerOptions.setAdapter(spinnerAdapter);

        // Set Listener for Item
        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                // Get current Settings From Database,
                HashMap<String, String> settings = dbHelper.getSettings();
                // more specifically the expected daily training time
                Integer exp_train_time = Integer.valueOf(settings.get("exp_train_time"));

                // Remove all previous series plotted on the Graph Views
                numDailyHoursGraph.removeAllSeries();
                levelEvolutionGraph.removeAllSeries();

                //View chosen option
                String chosenOption = ((TextView) selectedItemView).getText().toString();

                //Set number of previous days to check
                Integer numDays=0;
                switch(chosenOption) {
                    case "Last 30 days":
                        numDays = 30;
                        break;
                    case "Last 7 days":
                        numDays = 7;
                        break;
                }

                // Get string of date to check from (format: YYYY/MM/DD)
                String fromDate = getDateToCheckFrom(numDays-1);

                // Get Arrays of DataPoints To Plot the daily train time
                // and baseline set by the user
                DataPoint [] trainTime = getLineOfTrainTime(numDays, fromDate);
                DataPoint [] baseline = getLineOfExpTrainTime(numDays, exp_train_time);

                // Set the Series with the DataPoints
                LineGraphSeries<DataPoint> trainSeries = new LineGraphSeries<>(trainTime);
                LineGraphSeries<DataPoint> baselineSeries = new LineGraphSeries<>(baseline);
                // Set the Series titles and cor of the plot
                trainSeries.setTitle("Your Trains");
                baselineSeries.setTitle("Your Goal");
                trainSeries.setColor(ContextCompat.getColor(getApplicationContext(), R.color.brown));
                baselineSeries.setColor(ContextCompat.getColor(getApplicationContext(), R.color.darkBrown));

                numDailyHoursGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.TRANSPARENT);

                // Set legend for the GraphView
                numDailyHoursGraph.getLegendRenderer().setVisible(true);
                numDailyHoursGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                numDailyHoursGraph.getLegendRenderer().setMargin(40);

                // Set x-axis according to the days selected and add the Series
                numDailyHoursGraph.getViewport().setMaxX(numDays + 5);
                numDailyHoursGraph.getViewport().setMinX(0);
                numDailyHoursGraph.getViewport().setXAxisBoundsManual(true);
                numDailyHoursGraph.addSeries(trainSeries);
                numDailyHoursGraph.addSeries(baselineSeries);

                // Get Arrays of DataPoints To Plot the level evolution throughout
                DataPoint [] level = getLineOfLevel(numDays, fromDate);

                // Set the respective Series with a title, a color for the plot
                // and a marker for the data points
                LineGraphSeries<DataPoint> levelSeries = new LineGraphSeries<>(level);
                levelSeries.setTitle("Your Level");
                levelSeries.setColor(ContextCompat.getColor(getApplicationContext(), R.color.brown));
                levelSeries.setDrawDataPoints(true);
                levelSeries.setDataPointsRadius(10f);

                levelEvolutionGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.TRANSPARENT);

                // Set legend for the GraphView related to the level evolution
                levelEvolutionGraph.getLegendRenderer().setVisible(true);
                levelEvolutionGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                levelEvolutionGraph.getLegendRenderer().setMargin(40);

                // Set the x and y axis and plot the level evolution Series
                levelEvolutionGraph.getViewport().setMaxX(numDays + 5);
                //levelEvolutionGraph.getViewport().setMinX(1);
                levelEvolutionGraph.getViewport().setXAxisBoundsManual(true);
                levelEvolutionGraph.getViewport().setMaxY(5.5);
                levelEvolutionGraph.getViewport().setMinY(0);
                levelEvolutionGraph.getViewport().setYAxisBoundsManual(true);
                levelEvolutionGraph.addSeries(levelSeries);
            }

            @Override // If nothing is selected nothing is viewed
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //spinnerOptions.setOnItemSelectedListener((Spinner spinner)-> onItemSelected(selectedView));
    }

    private DataPoint[] getLineOfExpTrainTime(int size, int value) {
        // this function returns an array of points with the baseline set
        // for the expected training time
        // size- number of Days
        // value - Expected training time
        DataPoint[] dataPoints = new DataPoint[size];
        for (int point = 0; point < size; point++){
            DataPoint dp = new DataPoint (point+1, value);
            dataPoints[point] = dp;
        }
        return dataPoints;
    }

    private DataPoint[] getLineOfTrainTime(int size, String fromDate) {
        // this function returns an array of points with the daily training Time
        // from a number of days, dependent on the option selected
        // size- number of Days
        // fromDate - first day to be considered
        DataPoint[] dataPoints = new DataPoint[size];
        for (int point = 0; point < size; point++){
            float daily_time = dbHelper.getTrainTimeOn(fromDate);
            DataPoint dp = new DataPoint (point+1, daily_time);
            dataPoints[point] = dp;
            fromDate = getDateToCheckFrom(size - point - 2);
        }
        return dataPoints;
    }

    private DataPoint[] getLineOfLevel(int size, String fromDate) {
        // this function returns an array of points with the level achieved
        // from a number of days dependent on the option selected
        // size- number of Days
        // fromDate - first day to be considered
        DataPoint[] dataPoints = new DataPoint[size];
        Integer filledPoints = 0;
        for (int point = 0; point < size; point++){
            // SQL Query gets a list of strings with the average level reached each day
            Float daily_level = dbHelper.getTestLevelOn(fromDate);
            if (daily_level != null) {
                DataPoint dp = new DataPoint (point+1, daily_level);
                dataPoints[filledPoints] = dp;
                filledPoints++;
            }
            fromDate = getDateToCheckFrom(size - point - 2);
        }
        DataPoint[] validPoints = new DataPoint[filledPoints];
        System.arraycopy(dataPoints, 0, validPoints, 0, filledPoints);
        return validPoints;
    }

    private String getDateToCheckFrom(int numDays){
        // Get date in the presented string format
        // numDays - number of days to present day to check from

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1*numDays);
        Date toDate = cal.getTime();

        return dateFormat.format(toDate);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
