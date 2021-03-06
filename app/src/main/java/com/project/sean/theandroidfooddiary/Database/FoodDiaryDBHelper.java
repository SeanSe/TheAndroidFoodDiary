package com.project.sean.theandroidfooddiary.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.sean.theandroidfooddiary.Database.FoodDiaryContract.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Food Diary database helper class, will deal with handling the SQL
 * queries for the app.
 * Created by Sean on 16/05/2016.
 */
public class FoodDiaryDBHelper extends SQLiteOpenHelper {

    private static FoodDiaryDBHelper sInstance;

    public static final String DATABASE_NAME = "FoodDiary.db";

    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_TABLE_DIARY =
            "CREATE TABLE " + DiaryTable.TABLE_NAME + " (" +
                    DiaryTable.COL_DIARYID + " INTEGER PRIMARY KEY, " +
                    DiaryTable.COL_DATE + " INTEGER, " +
                    DiaryTable.COL_HOUR + " INTEGER, " +
                    DiaryTable.COL_MINUTE + " INTEGER, " +
                    DiaryTable.COL_FOODITEM + " TEXT, " +
                    DiaryTable.COL_FOODNOTE + " TEXT)";

    public static final String SQL_CREATE_TABLE_LIBRARY =
            "CREATE TABLE " + FoodLibTable.TABLE_NAME + " (" +
                    FoodLibTable.COL_FOODID + " TEXT PRIMARY KEY, " +
                    FoodLibTable.COL_FOODNAME + " TEXT, " +
                    FoodLibTable.COL_NOTE + " TEXT)";

    public static final String SQL_DELETE_TABLE_DIARY =
            "DROP TABLE IF EXISTS " + DiaryTable.TABLE_NAME;

    public static final String SQL_DELETE_TABLE_LIBRARY =
            "DROP TABLE IF EXISTS " + FoodLibTable.TABLE_NAME;

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
        db.execSQL(SQL_CREATE_TABLE_LIBRARY);

//        ContentValues contentValues = new ContentValues();
//        //Var1. Column name, Var2. Value
//        //contentValues.put(DiaryTable.COL_DIARYID, foodDiary.getDiaryId());
//        //contentValues.put(DiaryTable.COL_DATE, 1463353200000L);
//        contentValues.put(DiaryTable.COL_DATE, 1463356800000L);
//        contentValues.put(DiaryTable.COL_HOUR, 01);
//        contentValues.put(DiaryTable.COL_MINUTE, 01);
//        contentValues.put(DiaryTable.COL_FOODITEM, "Cheese");
//        contentValues.put(DiaryTable.COL_FOODNOTE, "All good");
//
//        db.insert(DiaryTable.TABLE_NAME, null, contentValues);
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
        db.execSQL(SQL_DELETE_TABLE_LIBRARY);
        //Recreate the tables
        onCreate(db);
    }

    //------------------------------------- Food Diary ---------------------------------------

    /**
     * Add a new diary entry into the database.
     * @param foodDiary
     * @return
     */
    public boolean insertDiaryData(FoodDiary foodDiary) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Var1. Column name, Var2. Value
        //contentValues.put(DiaryTable.COL_DIARYID, foodDiary.getDiaryId());
        contentValues.put(DiaryTable.COL_DATE, foodDiary.getDate());
        contentValues.put(DiaryTable.COL_HOUR, foodDiary.getHour());
        contentValues.put(DiaryTable.COL_MINUTE, foodDiary.getMinute());
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

    /**
     * Checks if a diary entry for a given date already exists.
     * @param date
     * @return
     */
    public boolean exsists(long date) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = null;
        String exists = "SELECT " + DiaryTable.COL_DATE + " FROM " + DiaryTable.TABLE_NAME +
                " WHERE " + DiaryTable.COL_DATE + " = " + date;
        Cursor cursor = db.rawQuery(exists, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates an ArrayList of food diary entries for the given date.
     * @param date
     * @return
     */
    public ArrayList<FoodDiary> getAllFoodDiaryEntries(long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<FoodDiary> diaryList = new ArrayList<>();
        //SELECT * QUERY
        String SQL_GETALLDIARY = "SELECT * FROM " + DiaryTable.TABLE_NAME +
                " WHERE " + DiaryTable.COL_DATE + " = " + date + " ORDER BY " +
                DiaryTable.COL_HOUR + " ASC, " + DiaryTable.COL_MINUTE + " ASC";
        Cursor cursor = db.rawQuery(SQL_GETALLDIARY, null);
        if(cursor.moveToFirst()) {
            do {
                FoodDiary foodDiary = new FoodDiary();
                foodDiary.setDiaryId(cursor.getInt(0));
                foodDiary.setDate(cursor.getLong(1));
                foodDiary.setHour(cursor.getInt(2));
                foodDiary.setMinute(cursor.getInt(3));
                foodDiary.setFoodItem(cursor.getString(4));
                foodDiary.setFoodNote(cursor.getString(5));

                diaryList.add(foodDiary);
            } while (cursor.moveToNext());
        }
        return diaryList;
    }

    //SELECT * FROM DIARY_INFO WHERE DIARY_DATE BETWEEN 100000000 AND 100000002 ORDER BY DIARY_DATE ASC, DIARY_HOUR ASC, DIARY_MINUTE ASC

    public ArrayList<FoodDiary> getFoodDiaryResults(long startDate, long endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<FoodDiary> diaryList = new ArrayList<>();
        //SELECT * QUERY
        String SQL_GETRESULTS = "SELECT * FROM " + DiaryTable.TABLE_NAME +
                " WHERE " + DiaryTable.COL_DATE + " BETWEEN " + startDate + " AND " +
                endDate + " ORDER BY " + DiaryTable.COL_DATE + " ASC, " + DiaryTable.COL_HOUR + " ASC, " +
                DiaryTable.COL_MINUTE + " ASC";
        Cursor cursor = db.rawQuery(SQL_GETRESULTS, null);
        if(cursor.moveToFirst()) {
            do {
                FoodDiary foodDiary = new FoodDiary();
                foodDiary.setDiaryId(cursor.getInt(0));
                foodDiary.setDate(cursor.getLong(1));
                foodDiary.setHour(cursor.getInt(2));
                foodDiary.setMinute(cursor.getInt(3));
                foodDiary.setFoodItem(cursor.getString(4));
                foodDiary.setFoodNote(cursor.getString(5));

                diaryList.add(foodDiary);
            } while (cursor.moveToNext());
        }
        return diaryList;
    }


    /**
     * Update the selected food diary entry, changing the details stored in the DB
     * @param foodDiary
     * @return
     */
    public boolean updateFoodDiaryEntry(FoodDiary foodDiary) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Var 1. column name
        //Var 2. value
        contentValues.put(DiaryTable.COL_DIARYID, foodDiary.getDiaryId());
        contentValues.put(DiaryTable.COL_DATE, foodDiary.getDate());
        contentValues.put(DiaryTable.COL_HOUR, foodDiary.getHour());
        contentValues.put(DiaryTable.COL_MINUTE, foodDiary.getMinute());
        contentValues.put(DiaryTable.COL_FOODITEM, foodDiary.getFoodItem());
        contentValues.put(DiaryTable.COL_FOODNOTE, foodDiary.getFoodNote());

        String diary_id = DiaryTable.COL_DIARYID + " = " + foodDiary.getDiaryId();
        //Var 1. table name
        //Var 2. content value
        //Var 3. condition to pass - id = ?
        //Var 4. String array of ids
        long result = db.update(DiaryTable.TABLE_NAME, contentValues, diary_id, null);
        if(result == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Deletes the selected food diary entry from the database.
     * @param diaryId
     * @return
     */
    public boolean deleteFoodDiaryEntry(int diaryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String diary_id = DiaryTable.COL_DIARYID + " = " + diaryId;
        //Var 1. table name
        //Var 2. where clause, asks for the diary ID
        //Var 3. String array for where clause
        return db.delete(DiaryTable.TABLE_NAME, diary_id, null) > 0;
    }


    //------------------------------------- Food Library ---------------------------------------

    /**
     * Add a new food library entry into the database.
     * @param foodLibrary
     * @return
     */
    public boolean insertFoodData(FoodLibrary foodLibrary) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Var1. Column name, Var2. Value
        contentValues.put(FoodLibTable.COL_FOODID, foodLibrary.getFoodId());
        contentValues.put(FoodLibTable.COL_FOODNAME, foodLibrary.getFoodName());
        contentValues.put(FoodLibTable.COL_NOTE, foodLibrary.getNote());

        //Var1. Table name, Var2. , Var3. ContentValues to be input
        //Returns -1 if data is not inserted
        //Returns row ID of newly inserted row if successful
        long result = db.insert(FoodLibTable.TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if a food item already exists.
     * @param foodId
     * @return
     */
    public boolean foodIdExsists(String foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = null;
        String exists = "SELECT " + FoodLibTable.COL_FOODID + " FROM " + FoodLibTable.TABLE_NAME +
                " WHERE " + FoodLibTable.COL_FOODID + " = " + foodId;
        Cursor cursor = db.rawQuery(exists, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the details of a food item, specified by the foodId.
     * Returns a Cursor object containing the details if they exist.
     * @param foodId
     * @return
     */
    public Cursor getFoodItemDetails(String foodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String exists = "SELECT * FROM " + FoodLibTable.TABLE_NAME +
                " WHERE " + FoodLibTable.COL_FOODID + " = "+ foodId;
        Cursor cursor = db.rawQuery(exists, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Creates an ArrayList of all food library entries.
     * @return
     */
    public ArrayList<FoodLibrary> getAllFoodLibEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<FoodLibrary> foodList = new ArrayList<>();
        //SELECT * QUERY
        String SQL_GETALLDIARY = "SELECT * FROM " + FoodLibTable.TABLE_NAME +
                " ORDER BY " + FoodLibTable.COL_FOODID + " ASC";
        Cursor cursor = db.rawQuery(SQL_GETALLDIARY, null);
        if(cursor.moveToFirst()) {
            do {
                FoodLibrary foodItem = new FoodLibrary();
                foodItem.setFoodId(cursor.getString(0));
                foodItem.setFoodName(cursor.getString(1));
                foodItem.setNote(cursor.getString(2));

                foodList.add(foodItem);
            } while (cursor.moveToNext());
        }
        return foodList;
    }


    /**
     * Update the selected food diary entry, changing the details stored in the DB
     * @param foodLibrary
     * @return
     */
    public boolean updateFoodLibEntry(FoodLibrary foodLibrary) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Var 1. column name
        //Var 2. value
        contentValues.put(FoodLibTable.COL_FOODID, foodLibrary.getFoodId());
        contentValues.put(FoodLibTable.COL_FOODNAME, foodLibrary.getFoodName());
        contentValues.put(FoodLibTable.COL_NOTE, foodLibrary.getNote());

        String food_id = FoodLibTable.COL_FOODID + " = " + foodLibrary.getFoodId();
        //Var 1. table name
        //Var 2. content value
        //Var 3. condition to pass - id = ?
        //Var 4. String array of ids
        long result = db.update(FoodLibTable.TABLE_NAME, contentValues, food_id, null);
        if(result == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Deletes the selected food library entry from the database.
     * @param foodId
     * @return
     */
    public boolean deleteFoodLibEntry(String foodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String food_id = FoodLibTable.COL_FOODID + " = " + foodId;
        //Var 1. table name
        //Var 2. where clause, asks for the diary ID
        //Var 3. String array for where clause
        return db.delete(FoodLibTable.TABLE_NAME, food_id, null) > 0;
    }
}
