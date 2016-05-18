package com.project.sean.theandroidfooddiary.Database;

import java.io.Serializable;

/**
 * Created by Sean on 16/05/2016.
 */
public class FoodDiary implements Serializable {

    private int diaryId;
    private long date;
    private int hour;
    private int minute;
    private String foodItem;
    private String foodNote;

    public FoodDiary(){
    }

    public FoodDiary(long date, String foodItem, String foodNote) {
        this.date = date;
        this.foodItem = foodItem;
        this.foodNote = foodNote;
    }

    public int getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getFoodNote() {
        return foodNote;
    }

    public void setFoodNote(String foodNote) {
        this.foodNote = foodNote;
    }
}
