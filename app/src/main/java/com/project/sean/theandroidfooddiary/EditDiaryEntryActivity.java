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
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sean on 18/05/2016.
 */
public class EditDiaryEntryActivity extends AppCompatActivity implements View.OnClickListener {

    private FoodDiaryDBHelper dbHelper;

    TextView tv_edit_diary_select_date;

    Calendar selectedDate;

    EditText editUpdateFoodItem;
    EditText editUpdateTime;
    EditText editUpdateNote;

    int clockHour = 01;
    int clockMinute = 02;

    private FoodDiary updateDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary_entry);
        setTitle("Update Diary Entry");

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

        selectedDate = (Calendar) getIntent().getSerializableExtra("calendar");

        tv_edit_diary_select_date = (TextView) findViewById(R.id.tv_edit_diary_select_date);

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

        tv_edit_diary_select_date.setText(currentDate);

        editUpdateFoodItem = (EditText) findViewById(R.id.editUpdateFoodItem);

        editUpdateTime = (EditText) findViewById(R.id.editUpdateTime);
        editUpdateTime.setOnClickListener(this);

        editUpdateNote = (EditText) findViewById(R.id.editUpdateNote);

        Button buttonUpdateEntry = (Button) findViewById(R.id.buttonUpdateEntry);
        buttonUpdateEntry.setOnClickListener(this);

        Log.d("AddDiaryEntry Date:", String.valueOf(selectedDate.getTimeInMillis()));

        updateDiary = (FoodDiary) getIntent().getSerializableExtra("diaryEntry");

        final Calendar updateCurrentTime = Calendar.getInstance();
        updateCurrentTime.set(Calendar.HOUR_OF_DAY, updateDiary.getHour());
        updateCurrentTime.set(Calendar.MINUTE, updateDiary.getMinute());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String updateTime = timeFormat.format(updateCurrentTime.getTime());

        editUpdateFoodItem.setText(updateDiary.getFoodItem());
        editUpdateTime.setText(updateTime);
        editUpdateNote.setText(updateDiary.getFoodNote());

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdateEntry: {
                updateDiaryEntry();
                break;
            }
            case R.id.editUpdateTime: {
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
    public void updateDiaryEntry() {
        if(!isEmpty(editUpdateFoodItem) &&
                !isEmpty(editUpdateTime)) {
            FoodDiary foodDiary = new FoodDiary();

            foodDiary.setDiaryId(updateDiary.getDiaryId());
            foodDiary.setDate(selectedDate.getTimeInMillis());
            foodDiary.setHour(clockHour);
            foodDiary.setMinute(clockMinute);
            foodDiary.setFoodItem(editUpdateFoodItem.getText().toString());
            if(!isEmpty(editUpdateNote)) {
                foodDiary.setFoodNote(editUpdateNote.getText().toString());
            } else {
                foodDiary.setFoodNote("No note entered.");
            }

            boolean isInserted = dbHelper.updateFoodDiaryEntry(foodDiary);

            if(isInserted) {
                Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_LONG).show();
                editUpdateFoodItem.getText().clear();
                editUpdateTime.getText().clear();
                editUpdateNote.getText().clear();

                Log.d("Date Added:", String.valueOf(selectedDate.getTimeInMillis()));

                int ENTRY_UPDATED = 2;

                Intent intent = new Intent(EditDiaryEntryActivity.this, MainActivity.class);
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
                editUpdateTime.setText(selectHourMinute);

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
