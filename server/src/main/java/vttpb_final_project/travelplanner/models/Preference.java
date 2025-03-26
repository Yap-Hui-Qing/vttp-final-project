package vttpb_final_project.travelplanner.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Preference {

    private String diet;
    private String allergies;
    private int serving;

    public String getDiet() {
        return diet;
    }
    public void setDiet(String diet) {
        this.diet = diet;
    }
    public String getAllergies() {
        return allergies;
    }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    public int getServing() {
        return serving;
    }
    public void setServing(int serving) {
        this.serving = serving;
    }

    public static Preference toPreference(SqlRowSet rs){
        Preference p = new Preference();
        p.setDiet(rs.getString("diet"));
        p.setAllergies(rs.getString("allergies"));
        p.setServing(rs.getInt("serving"));
        return p;
    }

    public static JsonObject toJson(Preference p){
        return Json.createObjectBuilder()
            .add("diet", p.getDiet())
            .add("allergies", p.getAllergies())
            .add("serving", p.getServing())
            .build();
    }
}
