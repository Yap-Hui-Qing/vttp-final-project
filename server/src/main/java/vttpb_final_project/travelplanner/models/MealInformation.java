package vttpb_final_project.travelplanner.models;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class MealInformation {

    private Recipe recipe;
    private List<Ingredient> ingredients;

    public Recipe getRecipe() {
        return recipe;
    }
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public static JsonObject toMealInformationJson(Recipe r, List<Ingredient> ingredients){

        JsonArrayBuilder ingredientsBuilder = Json.createArrayBuilder();
        for (Ingredient ingredient : ingredients) {
            ingredientsBuilder.add(Json.createObjectBuilder()
                .add("recipe_id", ingredient.getRecipe_id())
                .add("name", ingredient.getName())
                .add("value", ingredient.getValue())
                .add("unit", ingredient.getUnit())
            );
        }

        JsonObjectBuilder builder = Json.createObjectBuilder(r.toJson());
        builder.add("ingredients", ingredientsBuilder.build());
        return builder.build();
    }
}
