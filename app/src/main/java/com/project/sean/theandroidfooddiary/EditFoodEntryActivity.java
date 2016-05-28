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
 * Created by Sean on 24/05/2016.
 */
public class EditFoodEntryActivity extends AppCompatActivity {

    //Instance of the database
    private FoodDiaryDBHelper dbHelper;

    private Button buttonFL_edit_scan_food;
    private Button buttonUpdateFoodLib;

    private EditText editFL_update_food_id;
    private EditText editUpdateFoodLib;
    private EditText editupdateFoodNote;

    public FoodLibrary updateLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_lib);
        setTitle("Update Food Entry");

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

        updateLib = (FoodLibrary) getIntent().getSerializableExtra("foodEntry");

        editFL_update_food_id = (EditText) findViewById(R.id.editFL_update_food_id);
        editUpdateFoodLib = (EditText) findViewById(R.id.editUpdateFoodLib);
        editupdateFoodNote = (EditText) findViewById(R.id.editupdateFoodNote);

        buttonUpdateFoodLib = (Button) findViewById(R.id.buttonUpdateFoodLib);
        buttonFL_edit_scan_food = (Button) findViewById(R.id.buttonFL_edit_scan_food);


        editFL_update_food_id.setText(updateLib.getFoodId());
        editUpdateFoodLib.setText(updateLib.getFoodName());
        editupdateFoodNote.setText(updateLib.getNote());

        buttonFL_edit_scan_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(EditFoodEntryActivity.this);
                scanIntegrator.initiateScan();
            }
        });

        buttonUpdateFoodLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFoodLibEntry();
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
            //Set the edit text field here
            editFL_update_food_id.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Update the selected food item in the database with the new details entered.
     */
    public void updateFoodLibEntry() {
//        if(!isEmpty(editFL_update_food_id) &&
//                !isEmpty())
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
