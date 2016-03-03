package com.youyouza.pedmeter;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by youyouza on 16-1-6.
 *
 */

/***
 *
 * 文件存储在手机内存中，并不是手机的SD卡当中，所以在查找的时候记住是在内存中。
 *
 *
 *
 *
 *
 * */


public class PedometerService extends Service implements SensorEventListener {
    private SensorManager sm;
    private Sensor sensor;
    private SimpleDateFormat df;

//    AsyncTask
//    Handler

    private static  final String BROADCAST_ACTION_DATA="com.youyouza.updateUI.data";
    private static  final String BROADCAST_ACTION_PEDEMETER="com.youyouza.updateUI.pedemeter";
    private static final String stringExtraCalc="data";
    private static final String stringExtraPedemeter="Pedemeter";


    private  static final int CalAmount=32;

    private static final int calTimes=8;

    private static final int between=8;

    private long count=0L;
    private double valueArray[]=new double[CalAmount];

    private int index=0;

    private double beforeAccle=0.0;



    private String TAG="in the PedometerService";




    //add some var  help cal

    private static final double pinghRate=0.45;


    private static final int avgNumber=4;

    double xDirec[]=new double[avgNumber];
    double yDirec[]=new double[avgNumber];
    double zDirec[]=new double[avgNumber];

    boolean isFull=false;

    int DirecIndex=0;

    double sumXDirec=0.0;
    double sumYDirec=0.0;
    double sumZDirec=0.0;








//add element to array
    private void addOfDirec(double x,double y,double z){

        ridOfSum();
        setOfDirec(xDirec, DirecIndex, x);
        setOfDirec(yDirec,DirecIndex,y);
        setOfDirec(zDirec, DirecIndex, z);
        sumOfDirec();

        DirecIndex++;

        if(DirecIndex==avgNumber) isFull=true;

        DirecIndex=DirecIndex%avgNumber;


    }

//    set the index element of array

    private void setOfDirec(double Direc[],int index,double value){
        Direc[index]=value;
    }


//    to rid of before element from sum

    private void ridOfSum(){

        if(isFull) {
            sumXDirec -= xDirec[DirecIndex];
            sumYDirec -= yDirec[DirecIndex];
            sumZDirec -= zDirec[DirecIndex];
        }

    }

    //add to sum

    private void sumOfDirec(){

            sumXDirec+=xDirec[DirecIndex];
            sumYDirec+=yDirec[DirecIndex];
            sumZDirec+=zDirec[DirecIndex];
    }


    private double pinghua(double valueBefore,double valueNow){

        return valueBefore*(1-pinghRate)+valueNow*pinghRate;


    }


    @Override
    public void onCreate() {
        super.onCreate();

//        Log.i("startservice:", "in the PedometerService,service start");

        Log.i(TAG, "Service onCreate--->");

        sm=(SensorManager) getSystemService(Service.SENSOR_SERVICE);
		sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensor=sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
//        sensor=sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    }




    public static boolean writeSD(String data,String fileName){

        try {

            String state = Environment.getExternalStorageState();
//            File path = Environment.getExternalStoragePublicDirectory(
//                   Environment.DIRECTORY_MOVIES);
//            File file = new File(path, "/" + fileName);

//            FileOutputStream fos = new FileOutputStream(file,false);

//            fos.write(data.getBytes());

//            fos.close();

//            Log.i("file road",file.getPath());
//            return true;
            if(state.equals(Environment.MEDIA_MOUNTED)){

                File ext = Environment.getExternalStorageDirectory();

                FileOutputStream fos = new FileOutputStream(new File(ext,fileName),true);

                if(fos==null) Log.i("wirte something ","failed");

                fos.write(data.getBytes());

                fos.close();
//                Log.i("write something ", fileName + data);
//
//                Log.i("filePath:",ext.getPath());
                return true;

            }else{

//                Log.i("file", "not have SD card");


            }

            return false;



        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

            return false;

        }

    }




    public static String readSD(){

        try {

            String state = Environment.getExternalStorageState();

            if(state.equals(Environment.MEDIA_MOUNTED)){

                File ext = Environment.getExternalStorageDirectory();

                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ext.getPath()+"/Data.txt")));

                String line = br.readLine();

                br.close();

                return line;

            }

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return null;

    }


    @Override
    public void onDestroy() {

        sm.unregisterListener(this);

        Log.i(TAG, "Service onDestroy--->");
//        Log.i("stopservice:", "in the PedometerService,service stop");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand--->");
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        String  message="";
        String message1="";
        double x=event.values[0];
        double y=event.values[1];
        double z=event.values[2];

        double gravityOrign=Math.sqrt(x * x + y * y + z * z);
        message1+=x+"  ";
        message1+=y+"  ";
        message1+=z+"  ";
        message1+=gravityOrign+"  ";

//        message1+="\n";
//
//        writeSD(message1,"DataOrign.txt");

        addOfDirec(x,y,z);

        if(!isFull) return;


            x=sumXDirec/avgNumber;
            y=sumYDirec/avgNumber;
            z=sumZDirec/avgNumber;


        double gravity=Math.sqrt(x*x+y*y+z*z);

        message+=x+"  ";
        message+=y+"  ";
        message+=z+"  ";
        message+=gravity+"  ";
//        message+="\n";

//        writeSD(message,"Data.txt");

        if(beforeAccle>1.0)

            gravity=pinghua(beforeAccle, gravity);


        valueArray[index++]=gravity;

        beforeAccle=gravity;


//        Log.v("service", "获取到新的更新:" + message);

        double maxXrate=-1.0; //保存最大的相似度
        int Length=0;     //保存此时的更新长度
        double maxARate=-1.0; //保存此时的标准差

        if(index==CalAmount){
//            Log.i("service", "可以进行一次计算:");



            for(int start=0;start<=calTimes;start++){

                double sumAvalue=0.0;
                double sumBvalue=0.0;
                for(int copyIndex=0;copyIndex<start+between;copyIndex++){
                    sumAvalue+=valueArray[copyIndex];
                    sumBvalue+=valueArray[copyIndex+start+between];
                }

                sumAvalue=sumAvalue/(start+between);
                sumBvalue=sumBvalue/(start+between);



                double sumTogether=0.0;
                double rouA=0.0;
                double rouB=0.0;

                for(int copyIndex=0;copyIndex<start+between;copyIndex++){
                    double temp1=valueArray[copyIndex]-sumAvalue;
                    double temp2=valueArray[copyIndex+start+between]-sumBvalue;
                    sumTogether=sumTogether+temp1*temp2;
                    rouA=rouA+temp1*temp1;
                    rouB=rouB+temp2*temp2;
                }

                double tempA=rouA;
                double tempB=rouB;

             rouA=Math.sqrt(rouA/(start+between));
			 rouB=Math.sqrt(rouB/(start+between));

                double Xrate=sumTogether/(rouA*rouB*(start+between));

                if(Xrate>maxXrate){
                    maxARate=tempA;
                    Length=start+between;
                    maxXrate=Xrate;
                }

            }



//            Log.i("已经计算完毕：", "此时的标准差是：" + maxARate + "  以及相似系数为： " + maxXrate);


//            writeSD(maxARate + " " + maxXrate + "\n", "Calc.txt");


//
//            Intent intent=new Intent();
//
//            intent.setAction(BROADCAST_ACTION_DATA);
//
//
//            intent.putExtra(stringExtraCalc, Length + "此时的标准差是：" + maxARate + " \n 以及相似系数为： " + maxXrate + "  " + "\n" + df.format(new Date()));
//
//            sendOrderedBroadcast(intent, null);

            if(Length==0) Length=CalAmount;



            for(int i=Length;i<CalAmount;i++){
                valueArray[i-Length]=valueArray[i];
            }


            index=Length;

//            Log.i("广播发送完成：", "----------------end----------------------");

            if((maxARate>=3.0)&&(maxXrate>=0.45)){

                synchronized(this){

                    count++;
                }


//                更新ui

//                Log.i("已经可能为一步，进行广播：", "----------------send----------------------"+count);
//                Intent intent1=new Intent();
//                intent1.setAction(BROADCAST_ACTION_PEDEMETER);
//
//                intent1.putExtra(stringExtraPedemeter, count+"");
//
//                sendOrderedBroadcast(intent1, null);



            }else {

                //                更新ui
//                if(onStepChangedListener!=null){
//
//                    onStepChangedListener.onStepChanged(message+"");
//
//                    Log.i("service","进行数据的记录，结果为： "+message);
//
//                }

            }


        }



        index=index%CalAmount;


        Thread t = new MessageSendThread(
                Length + "\n 此时的标准差是：" + maxARate + " \n 以及相似系数为： " + maxXrate + "  " + "\n"
                        + count + "\n" + df.format(new Date())
                ,message + message1 + maxARate + " " + maxXrate + "\n");

        t.start();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class MessageSendThread extends Thread{

        private String messageWrite="";
        private String messageBrocast="";

    public MessageSendThread(String messageBrocast,String messageWrite){
        this.messageWrite+=messageWrite;
        this.messageBrocast+=messageBrocast;


    }
        public void run() {
            Intent intent=new Intent();

            intent.setAction(BROADCAST_ACTION_PEDEMETER);


            intent.putExtra(stringExtraPedemeter,messageBrocast);

            sendOrderedBroadcast(intent, null);

            writeSD(messageWrite,"Data.txt");
        }


    }


}
