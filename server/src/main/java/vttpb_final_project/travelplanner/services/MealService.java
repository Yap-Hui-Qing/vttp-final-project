package vttpb_final_project.travelplanner.services;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import vttpb_final_project.travelplanner.models.Ingredient;
import vttpb_final_project.travelplanner.models.Meal;
import vttpb_final_project.travelplanner.models.Recipe;
import vttpb_final_project.travelplanner.repositories.GroceryRepository;
import vttpb_final_project.travelplanner.repositories.MealRepository;
import vttpb_final_project.travelplanner.repositories.RecipeRepository;

@Service
public class MealService {

    @Value("${spoonacular.apiKey}")
    private String apiKey;

    @Autowired
    private MealRepository mealRepo;

    @Autowired
    private RecipeRepository recipeRepo;

    @Autowired 
    private GroceryRepository groceryRepo;

    public static final String INGREDIENT_URL = "https://api.spoonacular.com/recipes/{id}/ingredientWidget.json";

    private static final Logger logger = Logger.getLogger(MealService.class.getName());

    public List<Ingredient> getIngredients(int id) {
        String url = UriComponentsBuilder
                    .fromUriString(INGREDIENT_URL)
                    .queryParam("apiKey", apiKey)
                    .buildAndExpand(id)
                    .toUriString();

        System.out.println(url);

            RequestEntity<Void> req = RequestEntity
                    .get(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .build();

            try {
                RestTemplate template = new RestTemplate();
                ResponseEntity<String> resp = template.exchange(req, String.class);

                String payload = resp.getBody();

                List<Ingredient> ingredientSearch = getIngredientsSearch(id, payload);
                return ingredientSearch; 
            } catch (Exception ex) {
                ex.printStackTrace();
                return List.of();
            }
    }

    private List<Ingredient> getIngredientsSearch(int id, String payload){

        List<Ingredient> ingredients = new LinkedList<>();

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject j = reader.readObject();
        JsonArray arr = j.getJsonArray("ingredients");

        for (int i = 0; i < arr.size(); i++){
            Ingredient ing = new Ingredient();
            JsonObject metric = arr.getJsonObject(i).getJsonObject("amount").getJsonObject("metric");
            ing.setRecipe_id(id);
            ing.setValue(metric.getJsonNumber("value").doubleValue());
            ing.setUnit(metric.getString("unit"));
            ing.setName(arr.getJsonObject(i).getString("name"));
            ingredients.add(ing);
        }
        return ingredients;
    }

    
    // insert meals 
    // insert meal details into redis
    // insert recipe ingredients
    // insert grocery
    @Transactional
    public void updateMealPlan(String username, List<Meal> meals, List<Integer> recipeIds) {
        try {
            // mealRepo.deleteMealPlan(username);
            mealRepo.updateMealPlan(username, meals);
            recipeRepo.updateMealPlan(username, meals);
            
            List<Ingredient> ingredients = new LinkedList<>();
            for (int id : recipeIds){
                List<Ingredient> items = new LinkedList<>();
                // check if its in database
                Optional<List<Ingredient>> opt = mealRepo.getIngredients(id);
                if (opt.isEmpty()){
                    logger.info("calling api");
                    items = getIngredients(id);
                } else {
                    logger.info("from database");
                    items = opt.get();
                }
                for (Ingredient ing : items){
                    ingredients.add(ing);
                }
            }
            // mealRepo.deleteIngredients(username);
            // groceryRepo.deleteGrocery(username);
            mealRepo.updateIngredients(username, ingredients);
            logger.info(">>> inserting ingredients");
            groceryRepo.addRecipeToGrocery(username, ingredients);
            logger.info(">>> inserting groceries");

        } catch (Exception ex){
            throw new RuntimeException("Error updating meal plan", ex);
        }
    }

    // public String addMealPlanToCalendar(MealPlan mealPlan, HttpServletRequest request) {
    // // Convert MealPlan to calendar events
    // // Call googleCalendarController.addMealEventToCalendar for each meal

    // }
}
