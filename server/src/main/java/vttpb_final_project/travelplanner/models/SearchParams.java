package vttpb_final_project.travelplanner.models;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class SearchParams  {

    private String diet;
    private String intolerances;
    private int maxServings;

    public String getDiet() {
        return diet;
    }
    public void setDiet(String diet) {
        this.diet = diet;
    }
    public String getIntolerances() {
        return intolerances;
    }
    public void setIntolerances(String intolerances) {
        this.intolerances = intolerances;
    }
    public int getMaxServings() {
        return maxServings;
    }
    public void setMaxServings(int maxServings) {
        this.maxServings = maxServings;
    }
   
    // public static SearchParams toSearchParams(String params){

    //     JsonReader reader = Json.createReader(new StringReader(params));
    //     JsonObject j = reader.readObject();

    //     SearchParams p = new SearchParams();
    //     p.setDiet(j.getString("diet"));
    //     p.setIntolerances(j.getString("allergies"));
    //     p.setMaxServings(j.getInt("serving"));
    //     return p;
    // }
}
