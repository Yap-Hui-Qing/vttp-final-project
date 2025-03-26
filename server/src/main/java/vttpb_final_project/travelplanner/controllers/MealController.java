package vttpb_final_project.travelplanner.controllers;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttpb_final_project.travelplanner.models.Ingredient;
import vttpb_final_project.travelplanner.models.Meal;
import vttpb_final_project.travelplanner.models.Recipe;
import vttpb_final_project.travelplanner.repositories.GroceryRepository;
import vttpb_final_project.travelplanner.repositories.MealRepository;
import vttpb_final_project.travelplanner.repositories.RecipeRepository;
import vttpb_final_project.travelplanner.services.MealService;

import static vttpb_final_project.travelplanner.models.Recipe.*;
import static vttpb_final_project.travelplanner.models.Meal.*;
import static vttpb_final_project.travelplanner.models.MealInformation.*;
import static vttpb_final_project.travelplanner.models.Ingredient.*;

@RestController
@RequestMapping("/api")
public class MealController {

    @Autowired
    private MealService mealSvc;

    @Autowired
    private MealRepository mealRepo;

    @Autowired
    private RecipeRepository recipeRepo;

    @Autowired
    private GroceryRepository groceryRepo;

    @GetMapping(path = "/{username}/plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMeal(@PathVariable String username, @RequestParam String start, @RequestParam String end) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(start);
        Date endDate = sdf.parse(end);
        Optional<List<Meal>> opt = mealRepo.getUserMealPlan(username, startDate, endDate);
        if (opt.isEmpty()){
            return ResponseEntity.status(404).body(
                Json.createObjectBuilder()
                    .add("message", "No meal plan for this period")
                    .build().toString());
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        opt.get().stream()
            .map(m -> toJson(m))
            .forEach(builder :: add);

        return ResponseEntity.ok().body(builder.build().toString());
    }
    
    @PostMapping(path = "/{username}/plan", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postPlan(@RequestBody String meals, @PathVariable String username) throws ParseException{
        
        System.out.printf(">>> meals: %s".formatted(meals));
        System.out.println(username);

        try{
        List<Meal> mealList = new LinkedList<>();
        JsonReader reader = Json.createReader(new StringReader(meals));
        JsonArray arr = reader.readArray();

        for (int i = 0; i < arr.size(); i++){
            JsonObject j = arr.getJsonObject(i);
            Meal m = new Meal();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(j.getString("mealDate"));
            m.setMealDate(d);
            m.setMealTime(j.getString("mealTime"));
            if (j.containsKey("mealDetails") && j.get("mealDetails") instanceof JsonObject) {
                m.setMealDetails(toRecipe(j.getJsonObject("mealDetails")));
            } else {
                m.setMealDetails(null); 
            }
            mealList.add(m);
        }

        
            // extract out the recipe id
            List<Integer> recipeIds = new LinkedList<>();
            for (Meal m : mealList){
                recipeIds.add(m.getMealDetails().getId());
            }
            System.out.println(recipeIds);

            mealSvc.updateMealPlan(username, mealList, recipeIds);
            JsonObject obj = Json.createObjectBuilder()
                .add("message", "Meal plan updated successfully.")
                .build();
            return ResponseEntity.ok(obj.toString());
        } catch (Exception ex){
            ex.printStackTrace();
            JsonObject obj = Json.createObjectBuilder()
                    .add("error", "An error occurred while updating the meal plan. Please try again.")
                    .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj.toString());
        }    
    }

    @GetMapping("/meal/{id}")
    public ResponseEntity<String> getMealDetails(@PathVariable int id, @RequestParam String username){

        // get recipe details
        Recipe r = recipeRepo.getMealDetails(username, id);

        Optional<List<Ingredient>> opt = mealRepo.getIngredients(id);
        JsonObject j = toMealInformationJson(r, opt.get());
        
        System.out.println(j.toString());
        return ResponseEntity.ok(j.toString());
    }

    @GetMapping("/groceries")
    public ResponseEntity<String> getGroceryList(@RequestParam String username){
        List<Ingredient> ingredients = groceryRepo.getGroceryList(username);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        ingredients.stream()
            .map(ing -> toJson(ing))
            .forEach(builder :: add);

        JsonArray j = builder.build();
        return ResponseEntity.ok(j.toString());
    }


}
