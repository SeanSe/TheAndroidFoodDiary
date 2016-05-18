package com.project.sean.theandroidfooddiary.models;

/**
 * POJO for food items from a list of food.
 * Created by Sean on 20/02/2016.
 */
public class FoodItemModel {

    //Beginning offset into the results list for the items in the list requested
    private int offset;
    //Food group which the food belongs too
    private String group;
    //The food's name
    private String name;
    //The food's NDB Number
    private String ndbno;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNdbno() {
        return ndbno;
    }

    public void setNdbno(String ndbno) {
        this.ndbno = ndbno;
    }
}
