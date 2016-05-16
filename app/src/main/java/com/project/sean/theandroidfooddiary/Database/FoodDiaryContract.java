package com.project.sean.theandroidfooddiary.Database;

import android.provider.BaseColumns;

/**
 * Food Diary database class for table name and column names.
 * Created by Sean on 16/05/2016.
 */
public class FoodDiaryContract {

    //Empty constructor
    public FoodDiaryContract() {}

    /**
     *
     */
    public static abstract class DiaryTable implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "DIARY_INFO";
        //Column names
        public static final String COL_DIARYID = "DIARY_ID";
        public static final String COL_DATE = "DIARY_DATE";
        public static final String COL_FOODITEM = "FOOD_ITEM";
        public static final String COL_FOODNOTE = "FOOD_NOTE";
    }
}
