package com.youyouza.pedmeter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.youyouza.Kmeans.Kmeans;
import com.youyouza.LRClass.LRclass;
import com.youyouza.data.bean.State;
import com.youyouza.data.bean.Step;
import com.youyouza.data.db.PedometerDB;
import com.youyouza.tools.TimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Created by youyouza on 16-1-6.
 */

/***
 * 文件存储在手机内存中，并不是手机的SD卡当中，所以在查找的时候记住是在内存中。
 */

public class PedometerService extends Service implements SensorEventListener {


    private SensorManager sm;
    private Sensor sensor;


    private SimpleDateFormat df;

//    private PowerManager.WakeLock mWakeLock;


//    private Notification notification;

//    for the running state

    public static boolean isRunning = false;


    private static final String BROADCAST_ACTION_PEDEMETER = "com.youyouza.updateUI.pedemeter";

    private static final String stringExtraPedemeter = "Pedemeter";

    private static final String stringControl = "com.youyouza.pedemeterControl";

    private static final String stringExtraControl = "isRunning";


    ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);
    ExecutorService countPool = Executors.newFixedThreadPool(4);

    PedometerDB db;


//    AsyncTask
//    Handler


//    private static final int CalAmount = 15;


    private static int count = 0;

    int IndexEnd = 90 * 2 + 2;

    private double valueArray[] = new double[IndexEnd + (IndexEnd / 2)];


    private int index = 0;

    private String TAG = "in the PedometerService";


    //add some var  help cal

    private static final double pinghRate = 0.45;


    private static final int avgNumber = 4;

    double xDirec[] = new double[avgNumber];

    boolean isFull = false;

    int DirecIndex = 0;

    double sumXDirec = 0.0;


    private Step Totalstep;

    double calcuArray[] = new double[IndexEnd];

    private State state;

    /**
     * smooth data with specific rate
     */


    private double pinghua(double valueBefore, double valueNow) {

        return valueBefore * (1 - pinghRate) + valueNow * pinghRate;


    }

//    private static final int avgNumber = 4;
//    double xDirec[] = new double[avgNumber];
//    double sumXDirec = 0.0;
//    int DirecIndex = 0;

    /**
     * add element to array 平滑前的累加
     */
    private void addOfDirec(double x) {

        if (isFull) {
            sumXDirec -= xDirec[DirecIndex];
        }
        xDirec[DirecIndex] = x;

        sumXDirec += xDirec[DirecIndex];

        DirecIndex++;

        if (DirecIndex == avgNumber) isFull = true;

        DirecIndex = DirecIndex % avgNumber;
    }
//    if (!isFull) return;

    //获取平滑后数据

//    gravity = sumXDirec / avgNumber;


    public void cleanData() {


        isFull = false;
        sumXDirec = 0.0;
        DirecIndex = 0;

//        if (index > (IndexEnd/2+IndexEnd/4)) {
//
//            double[] DataLast = new double[index];
//            System.arraycopy(valueArray, 0, DataLast, 0, index - 1);
//            stepCount(DataLast);
//
//            Intent intent = new Intent();
//
//            intent.setAction(BROADCAST_ACTION_PEDEMETER);
//
//
////            private String[] runMessage={"run","in_pocket","in_hand","total"};
//
//            intent.putExtra("run", Totalstep.getStep_in_run());
//            intent.putExtra("in_pocket", Totalstep.getStep_pocket());
//            intent.putExtra("in_hand", Totalstep.getStep_in_hand());
//            intent.putExtra("total", Totalstep.getTotal_step());
//
//            sendOrderedBroadcast(intent, null);
//
//
//        }
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION_PEDEMETER);
        intent.putExtra("run", Totalstep.getStep_in_run());
        intent.putExtra("in_pocket", Totalstep.getStep_pocket());
        intent.putExtra("in_hand", Totalstep.getStep_in_hand());
        intent.putExtra("total", Totalstep.getTotal_step());

        sendOrderedBroadcast(intent, null);

        index = 0;

        count = 0;


    }


    /**
     * add the count to plus 1
     */

    public synchronized int addCount(int length, int type) {

        if (type == 1 || type == 2) {
            Totalstep.setStep_in_hand(Totalstep.getStep_in_hand() + length);
        } else if (type == 3) {

            if (Totalstep.getStep_in_hand() >= Totalstep.getStep_pocket())
                Totalstep.setStep_in_hand(Totalstep.getStep_in_hand() + length);
            else
                Totalstep.setStep_pocket(Totalstep.getStep_pocket() + length);

        } else if (type == 4) {

            Totalstep.setStep_pocket(Totalstep.getStep_pocket() + length);

        } else if (type == 5)
            Totalstep.setStep_in_run(Totalstep.getStep_in_run() + length);

        Totalstep.setTotal_step(Totalstep.getTotal_step() + length);

        count += length;
        return count;

    }

    public synchronized int getCount() {

        return Totalstep.getTotal_step();


    }


    @Override
    public void onCreate() {
        super.onCreate();

//        Log.i("startservice:", "in the PedometerService,service start");

        Log.i(TAG, "Service onCreate--->");


//        private SensorManager sm;
//        private Sensor sensor;

        //获取系统服务

        sm = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

//        获取加速度传感器
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        sensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

//        注册传感器监听器
//        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);


        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);// CPU保存运行


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(stringControl);

        registerReceiver(mReceiver, filter);


        newScheduledThreadPool.scheduleAtFixedRate(task1, 1000, 1000, TimeUnit.MILLISECONDS);


        Totalstep = new Step();

        Step step = new Step();
        Calendar calendar = TimeUtil.getNowCalendar();
        step.setDate(TimeUtil.formatCalendar(calendar));
        state = new State(calendar, step);


        db = PedometerDB.getInstance(getApplicationContext());
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("")
                .setContentText("")
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
        startForeground(1, notification);

    }


//    public void addSomeData(){
//        if(null==db)
//            db=PedometerDB.getInstance(getApplicationContext());
//        if(null==db) return;
//
//        Calendar calendar=TimeUtil.getBeforeMonth(2);
//
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand--->");
//        return START_REDELIVER_INTENT;
//        flags=START_STICKY;
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 在接收到数据后需要做的事情：
     * 1.进行平滑数据
     * 2.判断是否为行走
     * 3.进行波峰的聚类
     * 4.得到波峰的阈值范围
     * 5.对波峰进行计数以达到计步的目的
     */


    @Override
    public void onDestroy() {

        sm.unregisterListener(this);
        unregisterReceiver(mReceiver);

//        mWakeLock.release();
        newScheduledThreadPool.shutdown();
        countPool.shutdown();

        cleanData();
        saveDataToDB();
        cleanTotalStep();
        cleanState();


        db = null;

        super.onDestroy();
        Log.i(TAG, "Service onDestroy--->");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (Sensor.TYPE_ACCELEROMETER == event.sensor.getType()) {
//        if(Sensor.TYPE_LINEAR_ACCELERATION==event.sensor.getType()) {

            if (!isRunning) return;

            String message = "";
//        String message1 = "";

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            //求取和加速度
            double gravity = Math.sqrt(x * x + y * y + z * z);

            message += x + "  ";
            message += y + "  ";
            message += z + "  ";
            message += gravity + "  ";
//        message1 += gravity + " ";


            //进行数据平滑

            addOfDirec(gravity);

            if (!isFull) return;

            //获取平滑后数据

            gravity = sumXDirec / avgNumber;

            message += gravity + "  ";
            message += df.format(new Date());

//            message+=df.toString();
//        message1 += gravity + " ";

            synchronized (this) {
                valueArray[index++] = gravity;
            }

//        Log.v("service", "获取到新的更新:" + message);

//            Log.v("service", "the index is:" + index);

            if (index == IndexEnd) {


                Log.i("service", "可以进行一次计算:" + message);


                synchronized (this) {
                    System.arraycopy(valueArray, 0, calcuArray, 0, IndexEnd);
                    System.arraycopy(valueArray, IndexEnd - 2, valueArray, 0, index - (IndexEnd - 2));

                    index = index - (IndexEnd - 2);
                }

                CalculateStep calc = new CalculateStep(calcuArray);
//                calc.start();
                countPool.execute(calc);


            }

//        Log.i("Service", df.format(new Date())+"\n");

            index = index % (IndexEnd * 2);

            Thread t = new WriteMessage(message + " \n", "DataNow1.txt");
            countPool.execute(t);
//            t.start();

//        Thread t1 = new WriteMessage(message1 + " \n", "Data1.txt");
//
//        t1.start();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void stepCount(double[] data) {

        double[] centerData = Kmeans.getKmeanCenter(data, 3, 500);
        Arrays.sort(centerData);
        double temp1 = centerData[2] - centerData[1];
//        2017-03-02 21:15:55

//        Log.i("Service---> center is:", centerData[0] + "  " + centerData[1] + "  " + centerData[2]);

//        Thread t = new WriteMessage(centerData[0] + "  " + centerData[1] + "  " + centerData[2] + " \n", "temp.txt");
//
//        t.start();

        double topLevel = 0.0;


        int betweenMin = 16;
        int beteenMax = 75;

        int type = LRclass.getClass(centerData);

        if (type == 1) {
            if (temp1 < 1.0) return;
            topLevel = centerData[1] + temp1 / 2;
        } else if (type == 2) {

            topLevel = centerData[1] + temp1 / 2;

        } else if (type == 3 || type == 4) {

            topLevel = centerData[1] + temp1 / 4;

            betweenMin = 14;

        } else if (type == 5) {

            topLevel = centerData[1];

            betweenMin = 16;
            beteenMax = 75;

        }

        double topIndex = 0;


        for (int ite = 1; ite <= data.length - 2; ++ite) {


            if ((data[ite] < topLevel) || (data[ite] <= data[ite + 1]) || (data[ite] <= data[ite - 1]))
                continue;


//            Thread t1 = new WriteMessage(type+" "+(ite-topIndex) + " \n", "betweenOnIt.txt");
//
//            t1.start();

            if ((topIndex == 0) || ((ite - topIndex >= betweenMin) && (ite - topIndex < beteenMax))) {
                addCount(1, type);
                topIndex = ite;

            } else {

                if (ite - topIndex >= beteenMax)
                    topIndex = ite;

            }


        }

    }


    class CalculateStep extends Thread {

        private double[] data;

        public CalculateStep(double[] data) {
            this.data = data;

        }

        public void run() {

//            Log.i("Service————>in the run", data.length + "");
            stepCount(data);


        }


    }


    class WriteMessage extends Thread {

        String message = "";
        String fileName = "";

        public WriteMessage(String message, String fileName) {

            this.message += message;
            this.fileName += fileName;
        }

        public void run() {

            writeSD(message, fileName);


        }
    }


    /**
     * here is about write data into file
     */


    public static boolean writeSD(String data, String fileName) {

        try {

            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {

                File ext = Environment.getExternalStorageDirectory();

                FileOutputStream fos = new FileOutputStream(new File(ext, fileName), true);
                fos.write(data.getBytes());
                fos.close();

                return true;

            }

            return false;


        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

            return false;

        }

    }


    //    String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};

    public Step copyStep(Step stepbefore) {

        Step stepNow = new Step();
        stepNow.setId(stepbefore.getId());
        stepNow.setDate(stepbefore.getDate());
        stepNow.setTotal_step(stepbefore.getTotal_step());
        stepNow.setStep_in_hand(stepbefore.getStep_in_hand());
        stepNow.setStep_pocket(stepbefore.getStep_pocket());
        stepNow.setStep_in_run(stepbefore.getStep_in_run());
        return stepNow;

    }


    public void cleanTotalStep() {

        Totalstep.setId(0);
//        Totalstep.setDate(stepbefore.getDate());
        Totalstep.setTotal_step(0);
        Totalstep.setStep_in_hand(0);
        Totalstep.setStep_pocket(0);
        Totalstep.setStep_in_run(0);

    }

    public void cleanState() {

        state.step.setId(0);
        state.step.setTotal_step(0);
        state.step.setStep_in_hand(0);
        state.step.setStep_pocket(0);
        state.step.setStep_in_run(0);

    }

    public Step copyState() {

        Step tempStep = copyStep(Totalstep);
        Step saveStep = new Step();

        saveStep.setDate(state.step.getDate());

        saveStep.setId(state.step.getId());
        saveStep.setTotal_step(tempStep.getTotal_step() - state.step.getTotal_step());
        saveStep.setStep_in_hand(tempStep.getStep_in_hand() - state.step.getStep_in_hand());
        saveStep.setStep_pocket(tempStep.getStep_pocket() - state.step.getStep_pocket());
        saveStep.setStep_in_run(tempStep.getStep_in_run() - state.step.getStep_in_run());

        state.step = tempStep;

        return saveStep;
    }


    public synchronized void saveDataToDB() {

        Calendar calendarNow = TimeUtil.getNowCalendar();
//
//        if(!TimeUtil.isSameMinute(calendarNow,state.state)){

        //需要进行数据保存


        Step saveStep = copyState();

        state.state = calendarNow;

        state.step.setDate(TimeUtil.formatCalendar(calendarNow));


//        如果只是空数据，没必要进行存储

        if (saveStep.getTotal_step() < 1) return;

        if (null == db)
            db = PedometerDB.getInstance(getApplicationContext());


        Step stepInDB = db.loadStepsWithTime(saveStep.getDate());

        if (null != stepInDB) {

            stepInDB.setTotal_step(saveStep.getTotal_step() + stepInDB.getTotal_step());
            stepInDB.setStep_in_hand(saveStep.getStep_in_hand() + stepInDB.getStep_in_hand());
            stepInDB.setStep_pocket(saveStep.getStep_pocket() + stepInDB.getStep_pocket());
            stepInDB.setStep_in_run(saveStep.getStep_in_run() + stepInDB.getStep_in_run());

            db.updateStep(stepInDB);


        } else {
            db.saveStep(saveStep);

        }

//    }

    }

    TimerTask task1 = new TimerTask() {
        @Override
        public void run() {
            try {

                if (!isRunning) return;

                Intent intent = new Intent();

                intent.setAction(BROADCAST_ACTION_PEDEMETER);


                intent.putExtra("run", Totalstep.getStep_in_run());
                intent.putExtra("in_pocket", Totalstep.getStep_pocket());
                intent.putExtra("in_hand", Totalstep.getStep_in_hand());
                intent.putExtra("total", Totalstep.getTotal_step());

                sendOrderedBroadcast(intent, null);


                Calendar calendarNow = TimeUtil.getNowCalendar();

                boolean isSameMinuteNow = TimeUtil.isSameMinute(calendarNow, state.state);

                int addStep = Totalstep.getTotal_step() - state.step.getTotal_step();

                if (!isSameMinuteNow || (addStep > 0)) {
                    saveDataToDB();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//                Log.v("shake mediator screen off","trying re-registration");
                // Unregisters the listener and registers it again.
                Log.i(TAG, "detect the SCREEN_OFF------>");

//                mWakeLock.acquire();// 屏幕熄后，CPU继续运行

//                sm.unregisterListener(PedometerService.this);
////                sm.registerListener(PedometerService.this, sensor,
////                        SensorManager.SENSOR_DELAY_UI);
//                sm.registerListener(PedometerService.this, sensor,
//                        SensorManager.SENSOR_DELAY_GAME);

            } else if (intent.getAction().equals(stringControl)) {


                if (isRunning) {

                    //                now is running

                    boolean getisRunning = intent.getBooleanExtra(stringExtraControl, false);

//                    change to stop

                    if (!getisRunning) {

                        isRunning = false;

                        cleanData();
                        saveDataToDB();
                        cleanTotalStep();
                        cleanState();


                    }


                } else {

//                    not running

                    boolean getisRunning = intent.getBooleanExtra(stringExtraControl, false);

//                    change to running

                    if (getisRunning) {


                        isRunning = true;

                        cleanTotalStep();
                        cleanState();

                        cleanData();

                        Calendar calendarNow = TimeUtil.getNowCalendar();
                        state.state = calendarNow;
                        state.step.setDate(TimeUtil.formatCalendar(calendarNow));

                    }


                }


                Log.i("onReceive---->", "get button action" + isRunning + "");

            }
        }
    };


}
