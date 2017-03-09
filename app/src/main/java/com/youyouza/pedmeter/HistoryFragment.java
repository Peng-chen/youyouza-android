package com.youyouza.pedmeter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youyouza.data.bean.Step;
import com.youyouza.data.db.PedometerDB;
import com.youyouza.tools.TimeUtil;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.StandardValue;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.Calendar;
import java.util.List;


public class HistoryFragment extends Fragment {


    ValueLineChart mCubicValueLineChart;

    private BarChart mBarChart;

    private FloatingActionButton floatingActionButton;

    int todayData[] = new int[25];


    int[] weekday = TimeUtil.getWeekArray(7);


    boolean isFirst = true;

    int[] weekStep = new int[8];

    //    String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
    String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    PedometerDB db;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {

//        Log.i("HistoryFrag->onAttach", "start on------Onattach");
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = null;


        view = inflater.inflate(R.layout.history_fragment, container, false);
        mCubicValueLineChart = (ValueLineChart) view.findViewById(R.id.cubiclinechart);


        mBarChart = (BarChart) view.findViewById(R.id.barchart);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);

//        floatingActionButton.setBackgroundColor(1);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        loadToTalData();

                                                    }
                                                }


        );

        loadToTalData();


        return view;

    }


    @Override
    public void onDestroyView() {


//        Log.i("HistoryFragm-DestyView", "destroy on------onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

//        Log.i("HistoryFrag-onDestroy", "destroy on------onDestroy");
        super.onDestroy();
    }


    public void loadToTalData() {


        cleanData();

        db = PedometerDB.getInstance(getActivity());

        if (null != db) {

            Log.i("HistFragment", "db instance not null");

            List<Step> weekData = db.loadStepsofWeek(TimeUtil.formatCalendar(TimeUtil.getBeforeDay(7), "yyyy-MM-dd "),TimeUtil.formatCalendar(TimeUtil.getBeforeDay(-1), "yyyy-MM-dd "));


            drawData(weekData);


        }

    }

    public void drawData(List<Step> weekData) {

        if (weekData.size() <= 0) {

            Log.i("HisFragment--->", "database is null");

            return;
        }


        int day = 0;
        int hour = 0;

        for (Step step : weekData) {

            day = TimeUtil.getDayFromDateString(step.getDate());
            int i = 0;
            for (; i < 7; ++i) {

                if (weekday[i] == day) break;

            }
            if (i < 7)
                weekStep[i] += step.getTotal_step();
            else if (i == 7) {

//                Log.i("hisFragmetn","------>"+step.getDate());
                hour = TimeUtil.getHourFromDateString(step.getDate());
                todayData[hour] += step.getTotal_step();
//                Log.i("hisFragmetn","------>"+step.getDate()+"    "+todayData[hour]);
            }

        }

        drawIt(hour);

        int dayOfWeek = TimeUtil.getWeek(TimeUtil.getNowCalendar());

        drawBarChart(dayOfWeek);


        isFirst = false;

    }


    public void drawBarChart(int dayOfWeek) {


        mBarChart.clearChart();

//        Log.i("HisFragment--->","draw barchart------------------------->");

        int[] colorArray = {0xFF343456, 0xFF1FF4AC, 0xFF873F56, 0xFF563456, 0xFF1BA4E6, 0xFF56B7F1, 0xFF56B7F1};

        int tempDayOfWeek = dayOfWeek;


        for (int i = 0; i < 7; ++i) {
            mBarChart.addBar(new BarModel(weeks[tempDayOfWeek], weekStep[i], colorArray[i]));
//            Log.i("HisFragment--->",weeks[tempDayOfWeek]+" "+weekday[i]+" "+weekStep[i]+" "+colorArray[i]);
            ++tempDayOfWeek;
            tempDayOfWeek = tempDayOfWeek % 7;

//            Log.i("HisFragment--->",weeks[tempDayOfWeek]+" "+weekday[i]+" "+weekStep[i]+" "+colorArray[i]);

        }

        mBarChart.setShowValues(true);
        if (!isFirst)
            mBarChart.update();
        mBarChart.startAnimation();


    }


    public void drawIt(int hour) {

        mCubicValueLineChart.clearChart();

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);


        hour=TimeUtil.getNowCalendar().get(Calendar.HOUR_OF_DAY);

//        Log.i("HisF","----->"+hour);

        int i = hour - 6;

//        Log.i("HisFrag","------>"+hour);


        if (i < 0) i = 0;

        int max = 0;

        for (; (i < 25)&&(i<=hour+7); ++i) {
            series.addPoint(new ValueLinePoint(i + ":00", todayData[i]));
            if (todayData[i] > max) max = todayData[i];
        }

        i=0;

        for (i = i + max / 4; (max > 0) && i < max; i = i + max / 4)
            mCubicValueLineChart.addStandardValue(new StandardValue(i));


        mCubicValueLineChart.setShowStandardValues(true);
        mCubicValueLineChart.setShowIndicator(true);
        mCubicValueLineChart.addSeries(series);
        if (!isFirst)
            mCubicValueLineChart.update();
        mCubicValueLineChart.startAnimation();

    }


    public void cleanData() {

        for (int i = 0; i < weekStep.length; ++i) {
            weekStep[i] = 0;
        }

        for (int i = 0; i < todayData.length; ++i) {

            todayData[i] = 0;

        }

    }


    public void drawDefault() {

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        series.addPoint(new ValueLinePoint("Feb", 3.4f));
        series.addPoint(new ValueLinePoint("Mar", .4f));
        series.addPoint(new ValueLinePoint("Apr", 1.2f));
        series.addPoint(new ValueLinePoint("Mai", 2.6f));
        series.addPoint(new ValueLinePoint("Jun", 1.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));
        mCubicValueLineChart.setShowIndicator(false);
        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }


    public void drawIt(List<Step> data) {


        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        int hour = 0;

        for (Step step : data) {
            hour = TimeUtil.getHourFromDateString(step.getDate());
            todayData[hour] += step.getTotal_step();
            Log.i("HisFragment-->", todayData[hour] + "");
        }


         hour=TimeUtil.getNowCalendar().get(Calendar.HOUR_OF_DAY);

        Log.i("HisF","----->"+hour);


        int i = hour - 6;
//        int  i=0;

        if (i < 0) i = 0;

        int max = 0;

        for (; (i < 25); ++i) {
            series.addPoint(new ValueLinePoint(i + ":00", todayData[i]));
            if (todayData[i] > max) max = todayData[i];
        }


        for (i = i + max / 4; (max > 0) && i < max; i = i + max / 4)

            mCubicValueLineChart.addStandardValue(new StandardValue(i));


        mCubicValueLineChart.setShowStandardValues(true);
        mCubicValueLineChart.setShowIndicator(true);
        mCubicValueLineChart.addSeries(series);

        mCubicValueLineChart.startAnimation();


    }


}
