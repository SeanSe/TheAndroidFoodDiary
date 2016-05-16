package com.project.sean.theandroidfooddiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

//        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
//        String dayOfWeek = dayFormat.format(cal.getTime());
//
//        //Gets the month of the year, MM is displays "05" for May
//        String monthNumber;
//        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
//        monthNumber = monthFormat.format(cal.getTime());

        //Store time in the DB as milliseconds
        //cal.getTimeInMillis();

//        TextView tv1= (TextView) getActivity().findViewById(R.id.date_text_view);

//        String chosenDate = dayOfWeek + " "+view.getDayOfMonth() + "/"+ monthNumber + "/" +view.getYear();
//        tv1.setText(chosenDate);

        //Call from fragment to activity
        ((MainActivity)getActivity()).diaryList(cal);
    }
}