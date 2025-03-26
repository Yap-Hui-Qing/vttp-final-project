package vttpb_final_project.travelplanner.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Ingredient {

    private int recipe_id;
    private String name;
    private double value;
    private String unit;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public static Ingredient toIngredient(SqlRowSet rs){
        Ingredient ing = new Ingredient();
        ing.setName(rs.getString("ingredient_name"));
        ing.setValue(rs.getDouble("quantity"));
        ing.setUnit(rs.getString("unit"));
        ing.setStatus(rs.getString("status"));
        return ing;
    }

    public static JsonObject toJson(Ingredient ing){
        return Json.createObjectBuilder()
            .add("ingredient_name", ing.getName())
            .add("quantity", ing.getValue())
            .add("unit", ing.getUnit())
            .add("status", ing.getStatus())
            .build();
    }

}
