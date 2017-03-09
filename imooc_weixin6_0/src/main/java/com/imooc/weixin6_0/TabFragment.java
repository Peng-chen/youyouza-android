package com.imooc.weixin6_0;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.LegendModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.models.StandardValue;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment
{
	private String mTitle = "Default";
	private EditText edit;
	public static final String TITLE = "title";

   int index=0;

	PieChart mPieChart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{


		Log.i("TabFgment->onCreateView",getArguments().getInt("page")+"");

		View view=null;
		if (getArguments() != null)
		{
			mTitle = getArguments().getString(TITLE);
		}
		switch (getArguments().getInt("page")){

			case 0:
				view=inflater.inflate(R.layout.first,container,false);

				BarChart mBarChart = (BarChart) view.findViewById(R.id.barchart);
				mBarChart.addBar(new BarModel("enen",2.3f, 0xFF123456));
				mBarChart.addBar(new BarModel("enen",2.f,  0xFF343456));
				mBarChart.addBar(new BarModel("enen",3.3f, 0xFF563456));
				mBarChart.addBar(new BarModel("enen",1.1f, 0xFF873F56));
				mBarChart.addBar(new BarModel("enen",2.7f, 0xFF56B7F1));
				mBarChart.addBar(new BarModel("enen",2.f,  0xFF343456));
				mBarChart.addBar(new BarModel("enen",0.4f, 0xFF1FF4AC));
				mBarChart.addBar(new BarModel("enen",4.f,  0xFF1BA4E6));
				mBarChart.startAnimation();
//				SharedPreferences sp=inflater.getContext().getSharedPreferences("myPreference",0);
//				 edit=(EditText)view.findViewById(R.id.known);
//				edit.setText(sp.getString("myname","default"));


				break;
			case 1:
				view=inflater.inflate(R.layout.second,container,false);

				StackedBarChart mStackedBarChart = (StackedBarChart) view.findViewById(R.id.stackedbarchart);

				StackedBarModel s1 = new StackedBarModel("12.4");

				s1.addBar(new BarModel(2.3f, 0xFF63CBB0));
//				s1.addBar(new BarModel(2.3f, 0xFF56B7F1));
//				s1.addBar(new BarModel(2.3f, 0xFFCDA67F));

				StackedBarModel s2 = new StackedBarModel("13.4");
				s2.addBar(new BarModel(1.1f, 0xFF63CBB0));
//				s2.addBar(new BarModel(2.7f, 0xFF56B7F1));
//				s2.addBar(new BarModel(0.7f, 0xFFCDA67F));

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
			case 3:
				view=inflater.inflate(R.layout.third,container,false);
				 mPieChart = (PieChart) view.findViewById(R.id.piechart);

				mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
				mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
				mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
				mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));
				mPieChart.startAnimation();

				mPieChart.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						mPieChart.clearChart();
						if(index==0){
							mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
							mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
							mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
							mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));

						}else if(index==1){
							mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));

						}
						else if(index==2){
							mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));

						}
						else if(index==3){
							mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));

						}else if(index==4){
							mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));

						}

						mPieChart.update();
						mPieChart.startAnimation();
						index=(index+1)%5;
					}
				});


				break;
			case 2:
				view=inflater.inflate(R.layout.four,container,false);

				StackedBarChart mStackedBarChart1 = (StackedBarChart) view.findViewById(R.id.stackedbarchart1);

				StackedBarModel s11 = new StackedBarModel("");

				s11.addBar(new BarModel(1.1f, 0xFF63CBB0));
				s11.addBar(new BarModel(1.1f, 0xFF56B7F1));
				s11.addBar(new BarModel(1.1f, 0xFFCDA67F));
				s11.addBar(new BarModel(1.1f, 0xFF63CBB0));
				s11.addBar(new BarModel(1.1f, 0xFF56B7F1));

				mStackedBarChart1.addBar(s11);


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
				series.addPoint(new ValueLinePoint("Nov", 0.4f));
				series.addPoint(new ValueLinePoint("Dec", 1.3f));

//				mCubicValueLineChart.addStandardValue(new StandardValue(1.1f));
//				mCubicValueLineChart.addStandardValue(new StandardValue(2.1f));
//				mCubicValueLineChart.addStandardValue(new StandardValue(3.1f));
//				mCubicValueLineChart.addStandardValue(new StandardValue(4.1f));
				mCubicValueLineChart.addStandardValue(new StandardValue(5.1f));



//				mCubicValueLineChart.setActivateIndicatorShadow(true);

				mCubicValueLineChart.setShowStandardValues(true);

				//				指示线是否要显示
//				mCubicValueLineChart.setShowIndicator(true);
				mCubicValueLineChart.setIndicatorTextUnit("5.1");


//				List<LegendModel> le=new ArrayList<>();


				//显示横轴用的
//				le.add(new LegendModel("123"));
//				le.add(new LegendModel("456"));
//				le.add(new LegendModel("789"));
//				mCubicValueLineChart.addLegend(le);
//
//			   mCubicValueLineChart.setUseCustomLegend(true);

				mCubicValueLineChart.addSeries(series);




				mCubicValueLineChart.startAnimation();

				mStackedBarChart1.startAnimation();




				break;

		}




		return view;

	}

    @Override
    public void onDestroyView() {
        Log.i("TabFragment","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Log.i("TabFragment","onDestroy");
        super.onDestroy();
    }
}
