package com.aruba.columnchartview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ColumnChartView clv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clv_test = (ColumnChartView) findViewById(R.id.clv_test);
        List<ColumnChartView.Item> items = new ArrayList<>();
        items.add(new ColumnChartView.Item("01/01", 20));
        items.add(new ColumnChartView.Item("01/02", 60));
        items.add(new ColumnChartView.Item("01/03", 80));
        items.add(new ColumnChartView.Item("01/04", 30));
        items.add(new ColumnChartView.Item("01/05", 40));
        items.add(new ColumnChartView.Item("01/06", 45));
        items.add(new ColumnChartView.Item("01/07", 55));
        items.add(new ColumnChartView.Item("01/08", 66));

        List<ColumnChartView.ItemY> itemys = new ArrayList<>();
        itemys.add(new ColumnChartView.ItemY("10"));
        itemys.add(new ColumnChartView.ItemY("30"));
        itemys.add(new ColumnChartView.ItemY("50"));
        itemys.add(new ColumnChartView.ItemY("70"));
        itemys.add(new ColumnChartView.ItemY("90"));
        clv_test.initItems(items, itemys);
    }
}
