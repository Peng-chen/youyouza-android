package com.youyouza.pedmeter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.youyouza.data.bean.GragphStep;
import com.youyouza.data.bean.Step;
import com.youyouza.data.db.PedometerDB;
import com.youyouza.tools.TimeUtil;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.VerticalBarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;
import java.util.List;


public class HistoryStep extends AppCompatActivity implements View.OnClickListener {


    private BarChart mBarChart;
    private VerticalBarChart mVerticalBarChart;

    private String startTime="";
    private String endTime="";

    private boolean isFirst=true;

    private int []ColorArray={0xFF123456,0xFF343456,0xFF563456,0xFF873F56,0xFF56B7F1,0xFF343456,0xFF1FF4AC,0xFF1BA4E6};

    private TextView startText;
    private TextView endText;

    private FloatingActionButton floatingActionButton;
    int clickIndex=0;

    private PedometerDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.history_step);
        mBarChart = (BarChart)findViewById(R.id.barchart_history_step);

        mVerticalBarChart=(VerticalBarChart)findViewById(R.id.verbarchart_history_step);

        startText=(TextView)findViewById(R.id.chooseBeginShow_step);

        endText=(TextView)findViewById(R.id.chooseEndShow_step);





        floatingActionButton=(FloatingActionButton) findViewById(R.id.fab_history_step);



        floatingActionButton.setOnClickListener(this);
        startText.setOnClickListener(this);
        endText.setOnClickListener(this);



//        initDate();

//        drawDefault();


        db=PedometerDB.getInstance(getApplicationContext());

        showGraph();
    }



    @Override
    protected void onResume() {
        super.onResume();


    }



    public void drawDefault(){

        Log.i("HistoryStep","-------->drawDefault");


        if(null==mBarChart) Log.i("HistoryStep","------------------->null");

        mBarChart.addBar(new BarModel(2.3f, 0xFF123456));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));
        mBarChart.setShowValues(true);
        mBarChart.startAnimation();

        Log.i("HistoryStep","--------->end drawdefault");

    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.chooseBeginShow_step:
                clickIndex=1;
                showDatePickerDialog();
                break;
            case R.id.chooseEndShow_step:
                clickIndex=2;
                showDatePickerDialog();

                break;
            case R.id.fab_history_step:
                showGraph();
                break;
            default:break;

        }

    }

    public void initDate(){

        startTime= TimeUtil.formatCalendar(TimeUtil.getBeforeMonth(1),"yyyy-MM-dd ");
        endTime=TimeUtil.format();

    }


    public void showGraph(){

        if(startTime.length()==0||endTime.length()==0){
            initDate();
        }

        if(null==db)
            db=PedometerDB.getInstance(getApplicationContext());
        List<Step> list=db.loadStepsofDate(startTime,endTime);

        List<GragphStep> gragphStepList =new ArrayList<GragphStep>();

        GragphStep gragphStepTemp =new GragphStep();

        gragphStepTemp.setDate(startTime);


        for(Step stepNow:list){

            String tempDate=TimeUtil.getDate(stepNow.getDate());

            if(tempDate.equals(gragphStepTemp.getDate())){

                gragphStepTemp.setTotalStep(gragphStepTemp.getTotalStep()+stepNow.getTotal_step());

            }else{

                gragphStepList.add(gragphStepTemp);
                gragphStepTemp =new GragphStep();
                gragphStepTemp.setDate(tempDate);
                gragphStepTemp.setTotalStep(stepNow.getTotal_step());

            }
        }

        gragphStepList.add(gragphStepTemp);

        showGragh(gragphStepList);

    }


    public void showGragh(List<GragphStep> list){


        if(null==mBarChart||null==mVerticalBarChart) return;

        mBarChart.clearChart();

        mVerticalBarChart.clearChart();


        int colorIndex=0;

        float totalStep=0.0f;

        for(GragphStep gragphStep :list){

            if(gragphStep.getTotalStep()==0) continue;

            totalStep+= gragphStep.getTotalStep();

            mBarChart.addBar(new BarModel(gragphStep.getDate().substring(6), gragphStep.getTotalStep(),ColorArray[colorIndex++]));
            colorIndex=colorIndex%ColorArray.length;



        }

        if(totalStep>0.9f)
            mVerticalBarChart.addBar(new BarModel("",totalStep, 0xFF56B7F1));

        if(!isFirst&&totalStep>0.9f){
            mVerticalBarChart.update();
            mBarChart.update();


        }

        mVerticalBarChart.startAnimation();
        mBarChart.startAnimation();

        isFirst=false;

    }





    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onChanged(String message) {
                switch(clickIndex){

                    case 1:
                        startText.setText(message);
//                        startTime=new String(message);
                        startTime=(message.split(" "))[0];
                        break;
                    case 2:
                        endText.setText(message);
                        endTime=(message.split(" "))[0];
//                        endTime=new String(message);
                        break;
                    default:break;
                }

                Log.i("HistoryStep","----------->"+" startTime :"+startTime+" endTime :"+endTime);

            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

}
