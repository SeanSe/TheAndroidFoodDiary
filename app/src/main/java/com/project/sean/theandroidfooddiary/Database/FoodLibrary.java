package com.project.sean.theandroidfooddiary.Database;

import java.io.Serializable;

/**
 * Created by Sean on 23/05/2016.
 */
public class FoodLibrary implements Serializable {

    private String foodId;
    private String foodName;
    private String note;

    public FoodLibrary() {
    }

    public FoodLibrary(String foodId, String foodName, String note) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.note = note;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
