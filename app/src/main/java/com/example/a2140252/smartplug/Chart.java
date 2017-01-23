package com.example.a2140252.smartplug;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.a2140252.smartplug.dataPackage.GraphData;
import com.example.a2140252.smartplug.dataPackage.GraphDetailData;
import com.example.a2140252.smartplug.dataPackage.PlugData;
//import com.example.a2140252.smartplug.dataPackage.UserData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class Chart {
    private static final int MAX_MONTH_COUNT = 12;         //月の数(X軸の数)
    private static final int MAX_YEAR_COUNT = 3;
    String[] years = new String[MAX_YEAR_COUNT];
    ArrayList<String>[] months = new ArrayList[MAX_YEAR_COUNT];
    ArrayList<Integer>[] powers = new ArrayList[MAX_YEAR_COUNT];

    public Chart(Context context, String id) {
        //SQLite
        MyOpenHelper helper = new MyOpenHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor c = null;

        //データの取得
        try {
            final String[] columns = new String[]{"date", "power"};
            String where = "plug_id=?";
            c = database.query("power",columns,where,new String[]{id}, null,null,null,null);
            int i = 0;
            if(c.moveToFirst()){
                int dateIndex = c.getColumnIndex("date");
                int powerIndex = c.getColumnIndex("power");
                String year = "";
                do{
                    String date = c.getString(dateIndex);
                    String newYear = date.substring(0, 4);
                    if(year.equals("")) {
                        year = newYear;
                        years[i] = year;

                        months[i] = new ArrayList<>();
                        powers[i] = new ArrayList<>();
                    } else if(!year.equals(newYear)) {
                        year = newYear;
                        i++;
                        years[i] = year;

                        months[i] = new ArrayList<>();
                        powers[i] = new ArrayList<>();
                    }
                    String month = date.substring(4);
                    int power = c.getInt(powerIndex);

                    Log.w("power", String.valueOf(power));
                    months[i].add(month);
                    powers[i].add(power);
                }while(c.moveToNext());
            }
        }finally {
            database.close();
            c.close();
        }
}

    public void createLineChart(LineChart lineChart) {
        lineChart.setDescription("月別消費電力グラフ");

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setEnabled(true);
        //グラフをタッチ操作するか
        lineChart.setTouchEnabled(true);
        //ダブルタップでズームするか
        lineChart.setDoubleTapToZoomEnabled(false);
        //指二本でズームするか
        lineChart.setScaleEnabled(true);
        lineChart.getLegend().setEnabled(true);

        //X軸周り
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(0);

        lineChart.setData(createLineChartData());
        lineChart.invalidate();
        // アニメーション
        lineChart.animateY(2000, Easing.EasingOption.EaseInBack);
    }

    // LineChartの設定
    private LineData createLineChartData() {
        ArrayList<ILineDataSet> ILineDataSets = new ArrayList<>();

        // X軸
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 1; i <= MAX_MONTH_COUNT; i++) {
            xValues.add(String.valueOf(i) + "月");
        }

        //一年ごとのループ
        for(int i = 0; i < years.length; i++) {
            ArrayList<Entry> values = new ArrayList<>();
            //一年間のデータ分ループ
            for(int j = 0; j < months[i].toArray().length; j++) {
                int month = Integer.parseInt(months[i].get(j)) - 1;
                values.add(new Entry(powers[i].get(j), month));
            }
            ILineDataSets.add(createLineDataSet(values, years[i], i));
        }

        LineData LineData = new LineData(xValues,ILineDataSets);
        return LineData;
    }

    private ILineDataSet createLineDataSet(ArrayList<Entry> values, String description, int index) {
        LineDataSet dataSet = new LineDataSet(values, description);
        int color = ColorTemplate.COLORFUL_COLORS[index];
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(color);
        dataSet.setColor(color);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(5f);
        dataSet.setFillColor(color);
        return dataSet;
    }
}
