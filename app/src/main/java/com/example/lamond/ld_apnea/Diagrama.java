package com.example.lamond.ld_apnea;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

//https://github.com/PhilJay/MPAndroidChart/wiki
//https://github.com/PhilJay/MPAndroidChart
public class Diagrama extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagrama);
        LineChart chart = findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(1,2));
        entries.add(new Entry(2,1));
        entries.add(new Entry(3,3));
        entries.add(new Entry(4,2));
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.BLACK);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        chart.getDescription().setText("Frecuencia");
        chart.setData(lineData);
        chart.invalidate();

    }
}
