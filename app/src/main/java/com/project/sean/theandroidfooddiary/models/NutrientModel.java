package com.project.sean.theandroidfooddiary.models;

import java.util.List;

/**
 * POJO for a list of nutrients of food from a report.
 * Created by Sean on 21/02/2016.
 */
public class NutrientModel {

    //NDB food number
    private String ndbno;
    //Food name
    private String name;
    //might not need
    private String weight;
    //The household measure represented by the nutrient value element
    private String measure;
    //The list of nutrients and their values for a food
    private List<Nutrients> nutrients;

    public String getNdbno() {
        return ndbno;
    }

    public void setNdbno(String ndbno) {
        this.ndbno = ndbno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public List<Nutrients> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrients> nutrients) {
        this.nutrients = nutrients;
    }

    public static class Nutrients {
        //Nutrient Number
        private String nutrient_id;
        //Description of the nutrient
        private String nutrient;
        //Unit in which the nutrient value is expressed
        private String unit;
        //Value of the nutrient for this food
        private String value;
        //The 100 gram equivalent for the nutrient
        private String gm;

        public String getNutrient_id() {
            return nutrient_id;
        }

        public void setNutrient_id(String nutrient_id) {
            this.nutrient_id = nutrient_id;
        }

        public String getNutrient() {
            return nutrient;
        }

        public void setNutrient(String nutrient) {
            this.nutrient = nutrient;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getGm() {
            return gm;
        }

        public void setGm(String gm) {
            this.gm = gm;
        }
    }

}
