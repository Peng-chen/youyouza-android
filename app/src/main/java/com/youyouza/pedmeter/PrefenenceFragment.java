package com.youyouza.pedmeter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by youyouza on 16-4-28.
 */
public class PrefenenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName("myPreference");

        addPreferencesFromResource(R.xml.preferences);

//        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
//        findPreference("myname").setSummary(sp.getString("myname", "default value"));
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        findPreference("myname").setSummary(sp.getString("myname", "youyouza"));
        findPreference("myTall").setSummary(sp.getString("myTall","0"));
        if(null!=findPreference("myWeight"))
        findPreference("myWeight").setSummary(sp.getString("myWeight","0"));
        findPreference("gender").setSummary(sp.getString("gender","0"));
        findPreference("myLegLength").setSummary(sp.getString("myLegLength","0"));

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );


        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onResume() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
        super.onResume();
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//        if("myname".equals(preference.getKey())){
//
//            EditTextPreference editTextPreference = (EditTextPreference)findPreference("myname");
//            editTextPreference.setSummary(editTextPreference.getText());
//
//        }
//            Log.i("in->onPreference ","myname");
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }
        if (key.equals("gender")) {
            Preference connectionPref = findPreference(key);
            //一旦“gender“对应的Preference的value改变，就将该列表中的“性别“一栏中的概述“选择您的性别”更改为新选择的value值
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }

    }
}
