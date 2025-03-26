package vttpb_final_project.travelplanner.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import vttpb_final_project.travelplanner.models.Recipe;
import vttpb_final_project.travelplanner.models.SearchParams;
import vttpb_final_project.travelplanner.services.RecipeService;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private static final Logger logger = Logger.getLogger(RecipeController.class.getName());

    @Autowired
    private RecipeService recipeSvc;
    
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSearchRecipes(@RequestParam(defaultValue = "") String diet, @RequestParam(defaultValue = "") String intolerances, @RequestParam(defaultValue = "2") int maxServings ){

        logger.info(">>> query: %s, %s, %d".formatted(diet, intolerances, maxServings));

        SearchParams p = new SearchParams();
        p.setDiet(diet);
        p.setIntolerances(intolerances);
        p.setMaxServings(maxServings);

        List<Recipe> recipes = recipeSvc.search(p);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        recipes.stream()
            .map(r -> r.toJson())
            .forEach(builder :: add);
        // logger.info(builder.build().toString());
        return ResponseEntity.ok(builder.build().toString());
    }
}
