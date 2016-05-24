package com.project.sean.theandroidfooddiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;
import com.project.sean.theandroidfooddiary.Database.FoodLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the list of items on the users food, can add,update and delete the details
 * stored within the database. The items can either have a barcode or food ID associated
 * with them so they can be easily retrieved for later processing.
 * Created by Sean on 15/05/2016.
 */
public class FoodLibraryActivity extends AppCompatActivity {

    private FoodDiaryDBHelper dbHelper;

    private ListView lv_food_lib;

    private ArrayList<FoodLibrary> foodLibList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_library);
        setTitle("Food Library");

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

        //List view for the food library
        lv_food_lib = (ListView) findViewById(R.id.lv_food_lib);

        //Potential use for showing when a list is empty
//        TextView emptyText1 = (TextView) findViewById(R.id.emptyText1);
//        lv_food_lib.setEmptyView(emptyText1);

        //FloatingActionButton to add a new entry into the diary.
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodLibraryActivity.this, AddFoodEntryActivity.class);
                startActivity(intent);
            }
        });
    }

}
