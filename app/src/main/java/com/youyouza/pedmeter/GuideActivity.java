package com.youyouza.pedmeter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youyouza on 16-1-5.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener{


    private ViewPager vp;
    private ViewPagerAdapter  vpAdapter;
    private List<View> views;



    //显示底部的小圆点

    private ImageView[]dots;

    //当前页面被滑动时

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

//    当新的页面被选中
    @Override
    public void onPageSelected(int position) {

        for(int i=0;i<views.size();i++){

            if(i==position){


                dots[i].setBackgroundResource(R.drawable.dotselected);

            }else{

                dots[i].setBackgroundResource(R.drawable.dot);
            }


        }

    }

//    当滑动状态改变时

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guide);

//        初始化页面

        initViews();
        initDots();


    }

    private void initViews(){

        LayoutInflater inflater=LayoutInflater.from(this);
        views=new ArrayList<View>();


        views.add(inflater.inflate(R.layout.new_one,null));
        views.add(inflater.inflate(R.layout.new_two,null));
        views.add(inflater.inflate(R.layout.new_thr,null));
        views.add(inflater.inflate(R.layout.new_for, null));


//        初始化adapter

        vpAdapter=new ViewPagerAdapter(views,this);

//        设置监听器
        vp=(ViewPager)findViewById(R.id.viewpager);

        vp.setAdapter(vpAdapter);

//        绑定时间的监听

        vp.addOnPageChangeListener(this);
    }

    private void initDots(){

        LinearLayout ll= (LinearLayout) findViewById(R.id.ll);

         dots=new ImageView[views.size()];

        for(int i=0;i<views.size();i++){

            dots[i]=new ImageView(GuideActivity.this);

            if(0==i){

                dots[i].setBackgroundResource(R.drawable.dotselected);

            }else{

                dots[i].setBackgroundResource(R.drawable.dot);
            }

            dots[i].setPadding(0,0,20,0);
            ll.addView(dots[i]);

        }


    }








}
