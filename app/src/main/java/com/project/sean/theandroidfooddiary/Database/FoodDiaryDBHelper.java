package com.project.sean.theandroidfooddiary.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.sean.theandroidfooddiary.Database.FoodDiaryContract.*;

/**
 *
 * Created by Sean on 16/05/2016.
 */
public class FoodDiaryDBHelper extends SQLiteOpenHelper {

    private static FoodDiaryDBHelper sInstance;

    public static final String DATABASE_NAME = "FoodDiary.db";

    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_TABLE_DIARY =
            "CREATE TABLE " + DiaryTable.TABLE_NAME;

    public static final String SQL_DELETE_TABLE_DIARY =
            "DROP TABLE IF EXISTS " + DiaryTable.TABLE_NAME;

    /**
     * Checks if an instance of the database is already open returning that database if
     * there is, else creates a new one.
     * @param context
     * @return
     */
    public static synchronized FoodDiaryDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new FoodDiaryDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     *
     * @param context
     */
    public FoodDiaryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DIARY);
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop all tables and data
        db.execSQL(SQL_DELETE_TABLE_DIARY);
        //Recreate the tables
        onCreate(db);
    }

    /**
     *
     * @param foodDiary
     * @return
     */
    public boolean insertStockData(FoodDiary foodDiary) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Var1. Column name, Var2. Value
        contentValues.put(DiaryTable.COL_DIARYID, foodDiary.getDiaryId());
        contentValues.put(DiaryTable.COL_DATE, foodDiary.getDate());
        contentValues.put(DiaryTable.COL_FOODITEM, foodDiary.getFoodItem());
        contentValues.put(DiaryTable.COL_FOODNOTE, foodDiary.getFoodNote());

        //Var1. Table name, Var2. , Var3. ContentValues to be input
        //Returns -1 if data is not inserted
        //Returns row ID of newly inserted row if successful
        long result = db.insert(DiaryTable.TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }
}
