package com.project.sean.theandroidfooddiary.Database;

/**
 * Created by Sean on 16/05/2016.
 */
public class FoodDiary {

    private int diaryId;
    private String date;
    private String foodItem;
    private String foodNote;

    public FoodDiary(String date, String foodItem, String foodNote) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
