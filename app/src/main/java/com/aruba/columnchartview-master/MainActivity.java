package com.aruba.columnchartview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ColumnChartView clv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clv_test = (ColumnChartView) findViewById(R.id.clv_test);
        List<ColumnChartView.Item> items = new ArrayList<>();

        for (int i = 1; i < 32; i++) {
            items.add(new ColumnChartView.Item("01/" + (i < 10 ? "0" + i : i), new Random().nextInt(90)));
        }

        clv_test.setStep_y(15);
        clv_test.setAnime(true);
        clv_test.initItems(items, 90);
    }
}
