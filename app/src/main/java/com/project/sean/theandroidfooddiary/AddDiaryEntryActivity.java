package com.project.sean.theandroidfooddiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sean on 16/05/2016.
 */
public class AddDiaryEntryActivity extends AppCompatActivity {

    TextView tv_add_diary_select_date;

    Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);
        setTitle("Add Diary Entry");

        selectedDate = (Calendar) getIntent().getSerializableExtra("calendar");

        tv_add_diary_select_date = (TextView) findViewById(R.id.tv_add_diary_select_date);

        //Get todays date and set the MainActivity on launch to it
        int year = selectedDate.get(Calendar.YEAR);
        //int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        //Gets the day of the week, EEEE is for the long name
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        weekDay = dayFormat.format(selectedDate.getTime());

        //Gets the month of the year, MM is displays "05" for May
        String monthNumber;
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        monthNumber = monthFormat.format(selectedDate.getTime());

        String currentDate = weekDay + " " + day + "/" + monthNumber + "/" + year;

        tv_add_diary_select_date.setText(currentDate);
    }
}
