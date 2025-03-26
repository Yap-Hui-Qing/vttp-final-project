package vttpb_final_project.travelplanner.services;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.beans.factory.annotation.Value;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttpb_final_project.travelplanner.models.Recipe;
import vttpb_final_project.travelplanner.models.SearchParams;
import vttpb_final_project.travelplanner.repositories.PreferenceRepository;
import vttpb_final_project.travelplanner.repositories.RecipeRepository;

import static vttpb_final_project.travelplanner.models.Recipe.*;

@Service
public class RecipeService {

    private static final Logger logger = Logger.getLogger(RecipeService.class.getName());

    public static final String SEARCH_URL = "https://api.spoonacular.com/recipes/complexSearch";

    @Value("${spoonacular.apiKey}")
    private String apiKey;

    @Autowired
    private RecipeRepository recipeRepo;

    public List<Recipe> search(SearchParams params) {

        String cacheKey = params.getDiet() + "," + params.getIntolerances() + "," + String.valueOf(params.getMaxServings());
        System.out.println(cacheKey);

        Optional<String> opt = recipeRepo.getSearchResults(cacheKey);
        if (opt.isPresent()) {

            logger.info(">>> retrieving from redis");
            String result = opt.get();
            List<Recipe> recipes = new LinkedList<>();

            // convert to JsonObjects
            JsonReader reader = Json.createReader(new StringReader(result));
            JsonArray arr = reader.readArray();
            for (int i = 0; i < arr.size(); i++) {
                recipes.add(toRecipe(arr.getJsonObject(i)));
            }
            return recipes;
        } else {

            logger.info(">>> calling api");

            String url = UriComponentsBuilder
                    .fromUriString(SEARCH_URL)
                    .queryParam("apiKey", apiKey)
                    .queryParam("addRecipeInformation", true)
                    .queryParam("addRecipeInstructions", true)
                    .queryParam("diet", params.getDiet())
                    .queryParam("intolerances", params.getIntolerances())
                    .queryParam("maxServings", params.getMaxServings())
                    .queryParam("number", 50)
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

                List<Recipe> recipeSearch = getRecipeInfo(payload);
                recipeRepo.saveSearchResults(cacheKey, recipeSearch);
                logger.info("Saving search results");
                return recipeSearch; 
            } catch (Exception ex) {
                ex.printStackTrace();
                return List.of();
            }
        }

    }

    private List<Recipe> getRecipeInfo(String json) {

        List<Recipe> recipeSearch = new LinkedList<>();
        JsonArray results = Json.createReader(new StringReader(json))
                .readObject()
                .getJsonArray("results");

        for (int i = 0; i < results.size(); i++) {
            JsonObject r  = results.getJsonObject(i);

            Recipe recipe = new Recipe();
            recipe.setId(r.getInt("id"));
            recipe.setTitle(r.getString("title"));
            recipe.setReadyInMinutes(r.getInt("readyInMinutes"));
            recipe.setServings(r.getInt("servings"));
            recipe.setImageUrl(r.getString("image"));

            List<String> instructions = new LinkedList<>();
            List<String> ingredients = new LinkedList<>();
            List<String> cuisines = new LinkedList<>();

            JsonArray analyzedInstructions = r.getJsonArray("analyzedInstructions");
            if (analyzedInstructions != null && analyzedInstructions.size() > 0){
                JsonArray steps = analyzedInstructions.getJsonObject(0)
                    .getJsonArray("steps");            
                for (int j = 0; j < steps.size(); j ++){
                    JsonObject obj = steps.getJsonObject(j);
                    String instruction = obj.getString("step");
                    instructions.add(instruction);
                    JsonArray ingredientsArray = obj.getJsonArray("ingredients");
                    for (int k = 0; k < ingredientsArray.size(); k ++){
                        String ingredient = ingredientsArray.getJsonObject(k)
                            .getString("name");
                        ingredients.add(ingredient);
                    }
                }
            } else {
                System.out.println("No analyzed instructions available");
            }
    
            JsonArray cuisine = r.getJsonArray("cuisines");
            if (cuisine.size() <= 0){
                cuisines = List.of();
            } else {
                for (int k = 0; k < cuisine.size(); k++){
                    cuisines.add(cuisine.getString(k));
                }
            }
            
            recipe.setInstructions(instructions);
            recipe.setIngredients(ingredients);
            recipe.setCuisines(cuisines);
            recipeSearch.add(recipe);
        }
        return recipeSearch;

    }
}
