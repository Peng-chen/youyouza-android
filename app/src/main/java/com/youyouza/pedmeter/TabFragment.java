package com.youyouza.pedmeter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;


public class TabFragment extends Fragment {



    private static final String BROADCAST_ACTION_DATA = "com.youyouza.updateUI.data";
    private static final String BROADCAST_ACTION_PEDEMETER = "com.youyouza.updateUI.pedemeter";
    private static final String stringExtraCalc = "data";
    private static final String stringExtraPedemeter = "Pedemeter";

    private static final String[] colorForStep = {"#FE6DA8", "#56B7F1", "#CDA67F", "#FED70E"};

//	private Button startBtn;
//	private Button stopBtn;

    private PieChart mPieChart;

    private long step = 0;

    private boolean isFirst = false;


    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter filter;


    @Override
    public void onAttach(Context context) {

        Log.i("TabFgment->onAttach", "start on------Onattach");


        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.i("TabFgment->onCreateView", getArguments().getInt("page") + "");

        View view = null;

        switch (getArguments().getInt("page")) {

            case 0:
                view = inflater.inflate(R.layout.first, container, false);

                mPieChart = (PieChart) view.findViewById(R.id.piechart);

//				mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
//				mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
//				mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
                mPieChart.addPieSlice(new PieModel("step", step, Color.parseColor("#FED70E")));
                mPieChart.startAnimation();

                isFirst = true;

                if (myBroadcastReceiver == null) {
                    filter = new IntentFilter(BROADCAST_ACTION_PEDEMETER);
                    myBroadcastReceiver = new MyBroadcastReceiver(stringExtraPedemeter);
                    getActivity().registerReceiver(myBroadcastReceiver, filter);
                }


                break;
            case 1:
                view = inflater.inflate(R.layout.second, container, false);

                StackedBarChart mStackedBarChart = (StackedBarChart) view.findViewById(R.id.stackedbarchart);

                StackedBarModel s1 = new StackedBarModel("12.4");

                s1.addBar(new BarModel(2.3f, 0xFF63CBB0));
                s1.addBar(new BarModel(2.3f, 0xFF56B7F1));
                s1.addBar(new BarModel(2.3f, 0xFFCDA67F));

                StackedBarModel s2 = new StackedBarModel("13.4");
                s2.addBar(new BarModel(1.1f, 0xFF63CBB0));
                s2.addBar(new BarModel(2.7f, 0xFF56B7F1));
                s2.addBar(new BarModel(0.7f, 0xFFCDA67F));

                StackedBarModel s3 = new StackedBarModel("14.4");

                s3.addBar(new BarModel(2.3f, 0xFF63CBB0));
                s3.addBar(new BarModel(2.f, 0xFF56B7F1));
                s3.addBar(new BarModel(3.3f, 0xFFCDA67F));

                StackedBarModel s4 = new StackedBarModel("15.4");
                s4.addBar(new BarModel(1.f, 0xFF63CBB0));
                s4.addBar(new BarModel(4.2f, 0xFF56B7F1));
                s4.addBar(new BarModel(2.1f, 0xFFCDA67F));

                mStackedBarChart.addBar(s1);
                mStackedBarChart.addBar(s2);
                mStackedBarChart.addBar(s3);
                mStackedBarChart.addBar(s4);

                mStackedBarChart.startAnimation();


                break;
            case 2:
                view = inflater.inflate(R.layout.third, container, false);


                BarChart mBarChart = (BarChart) view.findViewById(R.id.barchart);
                mBarChart.addBar(new BarModel(2.3f, 0xFF123456));
                mBarChart.addBar(new BarModel(2.f, 0xFF343456));
                mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
                mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
                mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
                mBarChart.addBar(new BarModel(2.f, 0xFF343456));
                mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
                mBarChart.addBar(new BarModel(4.f, 0xFF1BA4E6));
                mBarChart.startAnimation();


                break;
            case 3:
                view = inflater.inflate(R.layout.history_fragment, container, false);
                ValueLineChart mCubicValueLineChart = (ValueLineChart) view.findViewById(R.id.cubiclinechart);

                ValueLineSeries series = new ValueLineSeries();
                series.setColor(0xFF56B7F1);

                series.addPoint(new ValueLinePoint("Jan", 2.4f));
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

                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();
                break;


        }

        return view;

    }

    @Override
    public void onDestroyView() {


        Log.i("TabFgment-DestroyView", "destroy on------onDestroyView");
//		if(myBroadcastReceiver!=null)
//			getActivity().unregisterReceiver(myBroadcastReceiver);
//		if(newScheduledThreadPool!=null)
//			newScheduledThreadPool.shutdown();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        Log.i("TabFgment-onDestroy", "destroy on------onDestroy");
        if (myBroadcastReceiver != null)
            getActivity().unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }


    class MyBroadcastReceiver extends BroadcastReceiver {

        private String extra;

        private int colorIndex = 0;

        public MyBroadcastReceiver(String extra) {
            this.extra = extra;
            colorIndex = 0;

        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("TabFgment-broast", "onReceive on------onReceive");
            step = intent.getLongExtra(extra, step);
            if (mPieChart != null) {
                mPieChart.clearChart();
                mPieChart.addPieSlice(new PieModel("step", step, Color.parseColor(colorForStep[colorIndex++])));
                mPieChart.update();

                colorIndex = colorIndex % colorForStep.length;

            }

        }
    }
}
