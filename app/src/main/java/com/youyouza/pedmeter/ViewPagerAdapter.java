package com.youyouza.pedmeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * Created by youyouza on 16-1-5.
 */
public class ViewPagerAdapter extends PagerAdapter{



    private List<View> views;
    private Activity activity;

    private static String SHAREDPREFERENCES_NAME="first_pref";

    public ViewPagerAdapter(List<View> views,Activity activity){

       this.activity=activity;
        this.views=views;

    }


//    当前界面的页数

    @Override
    public int getCount() {

        if(views!=null)
            return views.size();
        return 0;


    }


//    销毁view


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

       container.removeView(views.get(position));

    }

//    初始化contarin


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

    container.addView(views.get(position));

        if(position==views.size()-1){

            Button button=(Button)views.get(position).findViewById(R.id.start);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setGuied();
                    goHome();


                }
            });


        }



       return views.get(position);

    }


    private void goHome(){
        Intent intent=new Intent(activity,LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }


    private void setGuied(){

        SharedPreferences preferences=activity.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor  editor=preferences.edit();
        editor.putBoolean("isFirstIn",false);

        editor.commit();


    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
