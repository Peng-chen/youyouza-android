package com.youyouza.pedmeter;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youyouza.data.db.PedometerDB;
import com.youyouza.tools.TimeUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    Preference preference_one;
    Preference preference_thr;
    Preference preference_six;
    Preference preference_total;


    PedometerDB db;


    public int indexNow=-1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName("myPreference");

        addPreferencesFromResource(R.xml.setting_preferences);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        preference_one =findPreference("cleanData_oneMonth");
        preference_one.setOnPreferenceClickListener(this);
        preference_thr =findPreference("cleanData_threeMoth");
        preference_thr.setOnPreferenceClickListener(this);
        preference_six =findPreference("cleanData_sixMonth");
        preference_six.setOnPreferenceClickListener(this);
        preference_total =findPreference("cleanData_total");
        preference_total.setOnPreferenceClickListener(this);


        db=PedometerDB.getInstance(getActivity().getApplicationContext());
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        String ckey=preference.getKey();


        if(ckey.equals("cleanData_oneMonth")){

            Log.i("Setting","Delete "+1);

            indexNow=1;

            getSure();





        }else if(ckey.equals("cleanData_threeMoth")){


            indexNow=3;
            Log.i("Setting","Delete "+3);


            getSure();




        }else if(ckey.equals("cleanData_sixMonth")){

            indexNow=6;

            Log.i("Setting","Delete "+6);


            getSure();


        }else if(ckey.equals("cleanData_total")){

            Log.i("Setting","Delete "+6);

            indexNow=0;
            getSure();


        }



        return true;
    }




    public void getSure(){

        new AlertDialog.Builder(getActivity())
                .setTitle("真的需要删除么？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {


                                if(indexNow>=0) {

//                                    Log.i("in the Dialog","delete for some one------>");
                                    deleteWithTime(indexNow);
                                }
//                                Log.i("in the Dialog","sure------>"+indexNow);

                                indexNow=-1;

                            }
                        }).setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        indexNow=-1;
                    }
                }).create().show();

//
//        if(index>=0)
//            deleteWithTime(index);

    }

    public void deleteWithTime(int index){

//        Log.i("deleteWithTime"," ------>"+index);

        if(null==db) {


            Log.i("Setting","db is null-------> ");

            db=PedometerDB.getInstance(getActivity().getApplicationContext());

        }

        if(null==db) return;



        String stringTime= TimeUtil.formatCalendar(TimeUtil.getBeforeMonth(index),"yyyy-MM");
        db.DeleteStepWithTime(stringTime);



    }

}
