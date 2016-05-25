package com.project.sean.theandroidfooddiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;
import com.project.sean.theandroidfooddiary.Database.FoodLibrary;

/**
 * Created by Sean on 23/05/2016.
 */
public class AddFoodEntryActivity extends AppCompatActivity {

    //Instance of the database
    private FoodDiaryDBHelper dbHelper;

    private EditText edit_add_food_id;
    private EditText editAddFoodLib;
    private EditText editFoodNote;

    private Button button_scan_food;
    private Button buttonAddFoodLib;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_lib);
        setTitle("Add To Food Library");

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

        //EditText
        edit_add_food_id = (EditText) findViewById(R.id.edit_add_food_id);
        editAddFoodLib = (EditText) findViewById(R.id.editAddFoodLib);
        editFoodNote = (EditText) findViewById(R.id.editFoodNote);

        //Button
        button_scan_food = (Button) findViewById(R.id.button_scan_food);
        buttonAddFoodLib = (Button) findViewById(R.id.buttonAddFoodLib);

        button_scan_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(AddFoodEntryActivity.this);
                scanIntegrator.initiateScan();
            }
        });

        buttonAddFoodLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodLibEntry();
            }
        });
    }

    /**
     * Receives the intent from the scanning fragment, containing the barcode
     * data if any is present. Then sets the EditText according to the returned content.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            //String scanFormat = scanningResult.getFormatName();
//            Toast toast = Toast.makeText(getApplicationContext(), scanningResult.getContents(), Toast.LENGTH_SHORT);
//            toast.show();
            //Set the edit text field here
            edit_add_food_id.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Add a new food item to the food library database.
     */
    public void addFoodLibEntry() {
        if(!isEmpty(edit_add_food_id) &&
                !isEmpty(editAddFoodLib)) {
            if(!dbHelper.foodIdExsists(edit_add_food_id.getText().toString())) {
                FoodLibrary foodLibrary = new FoodLibrary();

                foodLibrary.setFoodId(edit_add_food_id.getText().toString());
                foodLibrary.setFoodName(editAddFoodLib.getText().toString());
                foodLibrary.setNote(editFoodNote.getText().toString());

                boolean isInserted = dbHelper.insertFoodData(foodLibrary);

                if(isInserted) {
                    Toast.makeText(this, "Data inserted successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddFoodEntryActivity.this, FoodLibraryActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Error, data not inserted.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Food ID already exists.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Food ID and Food name are required.", Toast.LENGTH_LONG).show();
        }
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
