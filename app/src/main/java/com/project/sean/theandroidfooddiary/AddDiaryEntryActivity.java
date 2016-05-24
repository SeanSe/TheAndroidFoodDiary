package com.project.sean.theandroidfooddiary;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.sean.theandroidfooddiary.Database.FoodDiary;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryContract;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Sean on 16/05/2016.
 */
public class AddDiaryEntryActivity extends AppCompatActivity implements View.OnClickListener {

    private FoodDiaryDBHelper dbHelper;

    TextView tv_add_diary_select_date;

    Calendar selectedDate;

    EditText editAddFoodItem;
    EditText editTime;
    EditText editNote;

    int clockHour = 01;
    int clockMinute = 02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);
        setTitle("Add Diary Entry");

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

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

        editAddFoodItem = (EditText) findViewById(R.id.editAddFoodItem);

        editTime = (EditText) findViewById(R.id.editTime);
        editTime.setOnClickListener(this);

        editNote = (EditText) findViewById(R.id.editNote);

        Button buttonAddEntry = (Button) findViewById(R.id.buttonAddEntry);
        buttonAddEntry.setOnClickListener(this);

        Log.d("AddDiaryEntry Date:", String.valueOf(selectedDate.getTimeInMillis()));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddEntry: {
                addDiaryEntry();
                break;
            }
            case R.id.editTime: {
                addTime();
                break;
            }
        }
    }

    /**
     * Inflates the menu, adds items to the action bar if it is present.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_entry, menu);
        return true;
    }

    /**
     * Add a diary entry to the database.
     */
    public void addDiaryEntry() {
        if(!isEmpty(editAddFoodItem) &&
                !isEmpty(editTime)) {
            FoodDiary foodDiary = new FoodDiary();

            foodDiary.setDate(selectedDate.getTimeInMillis());
            foodDiary.setHour(clockHour);
            foodDiary.setMinute(clockMinute);
            foodDiary.setFoodItem(editAddFoodItem.getText().toString());
            if(!isEmpty(editNote)) {
                foodDiary.setFoodNote(editNote.getText().toString());
            } else {
                foodDiary.setFoodNote("No note entered.");
            }

            boolean isInserted = dbHelper.insertDiaryData(foodDiary);

            if(isInserted) {
                Toast.makeText(this, "Data inserted successfully!", Toast.LENGTH_LONG).show();
                editAddFoodItem.getText().clear();
                editTime.getText().clear();
                editNote.getText().clear();

                Log.d("Date Added:", String.valueOf(selectedDate.getTimeInMillis()));

                int ENTRY_ADDED = 1;

                Intent intent = new Intent(AddDiaryEntryActivity.this, MainActivity.class);
                intent.putExtra("calendar", selectedDate);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Error, data not inserted.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addTime() {
        final Calendar mcurrentTime = Calendar.getInstance();
        int currentHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //Displays in the EditText the time selected by user.
                mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String selectHourMinute = timeFormat.format(mcurrentTime.getTime());
                editTime.setText(selectHourMinute);

                //Variables used to store in the database.
                clockHour = selectedHour;
                clockMinute = selectedMinute;
            }
        }, currentHour, currentMinute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    /**
     * Checks if an EditText field is empty.
     * @param etText
     * @return true if empty, false if not
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
