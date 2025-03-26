package vttpb_final_project.travelplanner.models;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Meal {

    private Date mealDate;
    private String mealTime;
    private Recipe mealDetails;

    public Date getMealDate() {
        return mealDate;
    }
    public void setMealDate(Date mealDate) {
        this.mealDate = mealDate;
    }
    public String getMealTime() {
        return mealTime;
    }
    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }
    public Recipe getMealDetails() {
        return mealDetails;
    }
    public void setMealDetails(Recipe mealDetails) {
        this.mealDetails = mealDetails;
    }

    public static JsonObject toJson(Meal m){
        return Json.createObjectBuilder()
            .add("mealDate", m.getMealDate().toString())
            .add("mealTime", m.getMealTime())
            .add("mealDetails", m.getMealDetails().toJson().toString())
            .build();
    }
}
