package com.youyouza.pedmeter;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.youyouza.data.bean.Count;
import com.youyouza.data.bean.Step;
import com.youyouza.data.bean.User;
import com.youyouza.data.db.PedometerDB;
import com.youyouza.tools.TimeUtil;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.VerticalBarChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class stepFragment extends Fragment implements View.OnClickListener {


    private static final String BROADCAST_ACTION_PEDEMETER = "com.youyouza.updateUI.pedemeter";

    private static final String stringExtraPedemeter = "Pedemeter";

    private static final String[] colorForStep = {"#FE6DA8", "#56B7F1", "#CDA67F", "#FED70E"};

    private static final String stringControl="com.youyouza.pedemeterControl";

    private static final String stringExtraControl="isRunning";

    private boolean isRunning=false;

//	private Button startBtn;
//	private Button stopBtn;

    private PieChart mPieChart;

    private VerticalBarChart mBarChart;

    private SimpleDateFormat df;

    FloatingActionButton floatingActionButton;

    FloatingActionButton reFreshfloatingActionButton;

    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter filter;


    private User user;

    private Step step;

    private Count count;

    private String[] runMessage={"run","in_pocket","in_hand","total"};

    private int colorIndex=0;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUserData(getActivity());
        step=new Step();
        count=new Count();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        view = inflater.inflate(R.layout.first1, container, false);

        mPieChart = (PieChart) view.findViewById(R.id.piechart);

        mBarChart = (VerticalBarChart) view.findViewById(R.id.verbarchart);

        mPieChart.addPieSlice(new PieModel("步数 距离 卡路里 (点击查看)", 0, Color.parseColor(colorForStep[2])));
//        mPieChart.addPieSlice(new PieModel("距离(米)", 0, Color.parseColor(colorForStep[0]) ));
//        mPieChart.addPieSlice(new PieModel("卡路里(大卡)", 0, Color.parseColor(colorForStep[1]) ));
        mPieChart.startAnimation();


        mBarChart.addBar(new BarModel("",0f, 0xFF56B7F1));


        mBarChart.startAnimation();



        if (myBroadcastReceiver == null) {
            filter = new IntentFilter(BROADCAST_ACTION_PEDEMETER);
            myBroadcastReceiver = new MyBroadcastReceiver(stringExtraPedemeter);
            getActivity().registerReceiver(myBroadcastReceiver, filter);
        }


        floatingActionButton=(FloatingActionButton)view.findViewById(R.id.fabNow);

        reFreshfloatingActionButton=(FloatingActionButton)view.findViewById(R.id.fabFresh);

//        startBtn=(Button)view.findViewById(R.id.startBtn);
//        stopBtn=(Button)view.findViewById(R.id.stopBtn);


//        stopBtn.setOnClickListener(this);
//        startBtn.setOnClickListener(this);
        mPieChart.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        reFreshfloatingActionButton.setOnClickListener(this);

        LoadTodayStep();

        if(PedometerService.isRunning){

//            已经开始
            isRunning=true;
//            图标显示停止按钮
            floatingActionButton.setImageResource(R.drawable.stop_200);

        }else{

//            没开始，图标显示开始按钮

            floatingActionButton.setImageResource(R.drawable.start_200);

        }

        Log.i("stepFragCreateView--->",user.toString());

        return view;

    }

    @Override
    public void onDestroyView() {


        Log.i("stepFrag-DestroyView", "destroy on------onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        Log.i("stepFragment-onDestroy", "destroy on------onDestroy");
        if (myBroadcastReceiver != null)
            getActivity().unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    //    myname myTall myWeight gender myLegLength

    private void loadUserData(Context context){

        SharedPreferences sp=context.getSharedPreferences("myPreference",0);
        if(null==user)
            user=new User();

        user.setName(sp.getString("myname","youyouza"));

        user.setTall(Double.parseDouble(sp.getString("myTall","180")));

        user.setWeight(Double.parseDouble(sp.getString("myWeight","75")));

        user.setGender(sp.getString("gender","male"));

        user.setLegLength(Double.parseDouble(sp.getString("myLegLength","65")));

    }



    private void LoadTodayStep(){

        PedometerDB  db=PedometerDB.getInstance(getActivity());

        if(null!=db){

//            Log.i("StepFragm"," db is not null");

            List<Step>  list=db.loadStepsofToday(TimeUtil.getTodayTime());

            int total=0;
            for(Step step:list){
                total+=step.getTotal_step();
//                Log.i("stepFrag",total+"  ");
            }

            if(null!=mBarChart){
                mBarChart.clearChart();
            }
            mBarChart.addBar(new BarModel("",total,0xFF56B7F1));
            mBarChart.update();

        }


    }

    private void refreshDraw(){

        if(null==count) count=new Count();

        int tempIndex=count.getIndex();

        if (mPieChart != null)
            mPieChart.clearChart();

        colorIndex=colorIndex%4;
        switch (tempIndex){
            case 4:

                mPieChart.addPieSlice(new PieModel("距离(米)", count.getDistance(), Color.parseColor(colorForStep[colorIndex]) ));

                colorIndex=(colorIndex+1)%4;

                mPieChart.addPieSlice(new PieModel("卡路里(大卡)", count.getCalorie(), Color.parseColor(colorForStep[colorIndex]) ));

                colorIndex=(colorIndex+1)%4;
                mPieChart.addPieSlice(new PieModel("步数(步)", count.getTotalStep(), Color.parseColor(colorForStep[colorIndex])  )  );
                break;
            case 0:
                mPieChart.addPieSlice(new PieModel("步数(步)", count.getTotalStep(), Color.parseColor(colorForStep[colorIndex])  )  );
                break;
            case 1:
                mPieChart.addPieSlice(new PieModel("距离(米)", count.getDistance(), Color.parseColor(colorForStep[colorIndex]) ));
                break;
            case 2:
                mPieChart.addPieSlice(new PieModel("卡路里(大卡)", count.getCalorie(), Color.parseColor(colorForStep[colorIndex]) ));
                break;

        }

        colorIndex=(colorIndex+1)%4;

        mPieChart.update();

//        mPieChart.startAnimation();

    }


    @Override
    public void onClick(View v) {

        boolean isBroadcast=false;



         if(R.id.fabNow==v.getId()){


             if(isRunning){
//                 需要停止
                 isRunning=false;
//                 图标显示是否开始
                 floatingActionButton.setImageResource(R.drawable.start_200);

             }

             else {
//                 需要开始
                 isRunning=true;
//                 图标显示停止按钮

                 floatingActionButton.setImageResource(R.drawable.stop_200);


             }

             Thread satartService = new Thread(new Runnable() {
                 @Override
                 public void run() {
//                     Log.i("frame------","start count!++++++++++0++++++++++"+df.format(new Date()));
                     try {
                         if(isRunning)
                         Thread.sleep(1500);
                         Intent intent=new Intent();
                         intent.setAction(stringControl);
                         intent.putExtra(stringExtraControl, isRunning);
                         getActivity().sendBroadcast(intent,null);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }

//                     Log.i("frame------","start count!++++++++++1++++++++++"+df.format(new Date()));
                 }
             });


             satartService.start();



         }




            if(R.id.piechart==v.getId()){


            int tempIndex=count.getIndex();

            refreshDraw();

            count.setIndex((tempIndex+1)%3);

        }

        if(R.id.fabFresh==v.getId()){

//            Log.i("steFragment","on Click---------------->"+getActivity().toString());

            LoadTodayStep();
            loadUserData(getActivity());


        }



    }


    class MyBroadcastReceiver extends BroadcastReceiver {

        private String extra;

        public MyBroadcastReceiver(String extra) {
            this.extra = extra;

        }

        @Override
        public void onReceive(Context context, Intent intent) {

//            Log.i("stepFragment-broast", "onReceive on------onReceive");

//            step = intent.getIntExtra(extra, step);

//            if(PedometerService.isRunning) floatingActionButton.setImageResource(R.drawable.start_200);
//            else floatingActionButton.setImageResource(R.drawable.stop_200);


            int tempTotalStep=step.getTotal_step();


            //            private String[] runMessage={"run","in_pocket","in_hand","total"};

            step.setStep_in_run( intent.getIntExtra("run", step.getStep_in_run() ));
            step.setTotal_step(intent.getIntExtra("total", step.getTotal_step() ) );
            step.setStep_in_hand(intent.getIntExtra("in_pocket", step.getStep_in_hand()) );
            step.setStep_pocket(intent.getIntExtra("in_hand", step.getStep_pocket() ) );

            if(null==count){
                count=new Count();
            }

            if(null!=count){

                count.setTotalStep(step.getTotal_step());

//                 步长：
//
//                身高=132+0.54*步长（厘米）走
//                run=1.2*height;


                float legLength=(float) (user.getTall()-132)/0.54f;

//                走 步长=身高/3

//                float Walklength= (float) (( step.getTotal_step()-step.getStep_in_run() )*(user.getTall()/(100.0*3)) );

                float Walklength=  (( step.getTotal_step()-step.getStep_in_run() )*( legLength/100.0f ) );
                float Runlength= (float) ((step.getStep_in_run())*1.2*(user.getTall()/100.0) );
                count.setDistance(Walklength+Runlength);


                //                卡路里=weight*distance*1.036/1000;  是大卡

                float calluli=((float) user.getWeight())*(Walklength+Runlength)*1.036f/1000.0f;
                count.setCalorie(calluli);

            }


            refreshDraw();


            if(tempTotalStep<step.getTotal_step()) LoadTodayStep();

        }


    }
}
