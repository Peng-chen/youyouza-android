package com.youyouza.pedmeter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by youyouza on 16-1-5.
 */
public class SplashActivity extends Activity{


    boolean isFirstIn=false;
    private static final int GO_HOME=1000;
    private static final int GO_GUIDE=1001;


    //延迟3秒
    private static final long SPLASH_DELAY_MILLIS=3000;

    private static String SHAREDPREFERENCES_NAME="first_pref";



    private Handler mHandler=new Handler(){


        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case GO_HOME:
                    goHome();break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void goHome(){

        Intent intent=new Intent(this,LoginActivity.class);

        Log.i("SplashActivity","start to LoginActiviry");

        startActivity(intent);
        finish();

    }


    private void goGuide(){

        Intent intent=new Intent(this,GuideActivity.class);

        Log.i("SplashActivity","start to GuideActiviry");
        startActivity(intent);
        finish();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        init();

    }


    private void init(){

//        获取sharedpreferences的数据，并且可以使用来记录程序使用的次数
        SharedPreferences preferences=getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);

//        获取是否是第一次打开

        isFirstIn=preferences.getBoolean("isFirstIn",true);

//        如果是第一次打开，那么跳转到GuideActivity,否则跳转到LoginActivity

        if(!isFirstIn){
            mHandler.sendEmptyMessageDelayed(GO_HOME,SPLASH_DELAY_MILLIS);
        }else{
            mHandler.sendEmptyMessageDelayed(GO_GUIDE,SPLASH_DELAY_MILLIS);
        }

    }







}
