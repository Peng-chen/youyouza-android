package com.youyouza.pedmeter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 */
public  class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    public OnDateChangeListener onDateChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
         Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

//        Log.i("date-->","Calendar"+": "+month);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Log.i("DatePickerFragment","picker:"+"year: "+year+"  month"+month+" day"+day);

        Log.i("DatePickerFragment","picker:-------->"+"year: "+view.getYear()+"  month"+view.getMonth()+" day"+view.getDayOfMonth());

        Calendar c1=Calendar.getInstance();
        c1.set(year,month,day);
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");

        String message=sp.format(c1.getTime());
        Log.i("DatePickerFragment","picker:"+message);
        onDateChangeListener.onChanged(message);

    }


}