package com.project.sean.theandroidfooddiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sean on 15/05/2016.
 */
public class FoodLibraryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_library);
        setTitle("Food Library");
    }
}