package vttpb_final_project.travelplanner.repositories;

import static vttpb_final_project.travelplanner.models.Recipe.toRecipe;

import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttpb_final_project.travelplanner.models.Meal;
import vttpb_final_project.travelplanner.models.Recipe;

@Repository
public class RecipeRepository {
    
    @Autowired @Qualifier("redis-0")
    private RedisTemplate<String, String> template;

    // save results from spoonacular
    // query Recipe[]
    public void saveSearchResults(String query, List<Recipe> recipes){

        ValueOperations<String, String> valueOps = template.opsForValue();

        JsonArrayBuilder builder = Json.createArrayBuilder();
        recipes.stream()
            .map(r -> r.toJson())
            .forEach(builder :: add);

        valueOps.set(query, builder.build().toString(), Duration.ofMinutes(60));
    }

    // fred mealId recipe
    public void updateMealPlan(String username, List<Meal> meals){
        HashOperations<String, String, String> hashOps = template.opsForHash();
        for (Meal m : meals){
            JsonObject obj = m.getMealDetails().toJson();
            hashOps.put(username, String.valueOf(m.getMealDetails().getId()), obj.toString());
        }
    }

    // hvals -> get the Recipe stored in redis
    public Recipe getMealDetails(String username, int id){
        HashOperations<String, String, String> hashOps = template.opsForHash();
        String value = hashOps.get(username, String.valueOf(id));
        JsonReader reader = Json.createReader(new StringReader(value));
        JsonObject obj = reader.readObject();
        return toRecipe(obj);
    }

    // check if results are in cache or get from api
    public Optional<String> getSearchResults(String query){

        ValueOperations<String, String> valueOps = template.opsForValue();

        String results = valueOps.get(query);
        if(results == null){
            return Optional.empty();
        } else {
            return Optional.of(results);
        }
    }
}
