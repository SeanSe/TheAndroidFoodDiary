package com.project.sean.theandroidfooddiary;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.project.sean.theandroidfooddiary.Database.FoodDiary;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryContract;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;
import com.project.sean.theandroidfooddiary.Database.FoodLibrary;

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
    EditText edit_add_diary_food_id;

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
        edit_add_diary_food_id = (EditText) findViewById(R.id.edit_add_diary_food_id);

        editTime = (EditText) findViewById(R.id.editTime);
        editTime.setOnClickListener(this);

        editNote = (EditText) findViewById(R.id.editNote);

        Button buttonAddEntry = (Button) findViewById(R.id.buttonAddEntry);
        buttonAddEntry.setOnClickListener(this);

        Button button_scan_item= (Button) findViewById(R.id.button_scan_item);
        button_scan_item.setOnClickListener(this);

        edit_add_diary_food_id.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (dbHelper.foodIdExsists(v.getText().toString())) {
                        //TO-DO add a connection to the database
                        Cursor result = dbHelper.getFoodItemDetails(v.getText().toString());
                        FoodLibrary foodResult = new FoodLibrary();
                        foodResult.setFoodId(result.getString(0));
                        foodResult.setFoodName(result.getString(1));
                        foodResult.setNote(result.getString(2));

                        editAddFoodItem.setText(foodResult.getFoodName());
                        editNote.setText(foodResult.getNote());
                    } else {
                        Toast.makeText(AddDiaryEntryActivity.this, "No food item found for ID: " + v.getText(),
                                Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

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
            case R.id.button_scan_item: {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
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
     * Handles all Scan requests and results.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            if(scanningResult.getContents() == null) {
                Toast.makeText(this, "Scan cancelled.", Toast.LENGTH_LONG);
            } else {
                String scanContent = scanningResult.getContents();
                if (dbHelper.foodIdExsists(scanContent)) {
                    //TO-DO get the stock information from the database
                    Cursor result = dbHelper.getFoodItemDetails(scanContent);
                    FoodLibrary foodResult = new FoodLibrary();
                    foodResult.setFoodId(result.getString(0));
                    foodResult.setFoodName(result.getString(1));
                    foodResult.setNote(result.getString(2));

                    editAddFoodItem.setText(foodResult.getFoodName());
                    editNote.setText(foodResult.getNote());
                } else {
                    Toast.makeText(this, "No food item found for ID: " + scanContent,
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this,"No scan data received!",
                    Toast.LENGTH_LONG).show();
        }
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
                startActivity(intent);
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
