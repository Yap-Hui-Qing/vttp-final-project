package vttpb_final_project.travelplanner.repositories;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.JsonObject;
import jakarta.transaction.TransactionScoped;
import vttpb_final_project.travelplanner.models.Ingredient;
import vttpb_final_project.travelplanner.models.Meal;
import vttpb_final_project.travelplanner.models.Preference;
import vttpb_final_project.travelplanner.models.Recipe;
import static vttpb_final_project.travelplanner.models.Recipe.*;

@Repository
public class MealRepository {

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private RecipeRepository recipeRepo;

    public static final String UPSERT_MEAL = """
            insert into meals (username, mealDate, mealTime, mealId, mealName)
            values (?, ?, ?, ?, ?)
            on duplicate key update mealId = ?, mealName = ?
                """;
    public static final String DELETE_MEAL_PLAN = """
            delete from meals where username = ?
            """;
    public static final String GET_MEAL_PLAN = """
            select * from meals
            where username = ?
            and mealDate >= ?
            and mealDate <= ?
            """;
    public static final String GET_RECIPE_ID_BY_MEAL = """
            SELECT mealId FROM meals
            WHERE mealDate = ? AND mealTime = ? AND USERNAME = ?
            """;
    public static final String GET_MEAL_PLAN_FOR_USER = """
            select * from meals
            where username = ?
            """;
    public static final String DELETE_MEAL = """
            delete from meals where mealDate = ? and mealTime = ? and username = ?
            """;

    public static final String DELETE_INGREDIENT = """
            DELETE FROM recipe_ingredients WHERE username = ? and recipe_id = ?
            """;

    // update each time user save their meal plan 
    public static final String INSERT_INGREDIENT = """
            insert into recipe_ingredients (recipe_id, username, ingredientName, quantity, unit) values (?, ?, ?, ?, ?)
            """;

    public static final String GET_RECIPE_INGREDIENT = """
            select recipe_id, ingredientName, quantity, unit from recipe_ingredients where recipe_id = ?
            """;


    @Transactional
    public void deleteMealPlan(Date mealDate, String mealTime, String username){
        Integer recipeId = template.queryForObject(GET_RECIPE_ID_BY_MEAL, Integer.class, mealDate, mealTime, username);
        template.update(DELETE_MEAL, mealDate, mealTime, username);
        if (recipeId != null){
            template.update(DELETE_INGREDIENT, username, recipeId);
        }
    }

    public void updateMealPlan(String username, List<Meal> meals) {

        List<Object[]> mList = meals.stream()
                .map(m -> {
                    Recipe r = m.getMealDetails();
                    Object[] fields = new Object[7];
                    fields[0] = username;
                    fields[1] = m.getMealDate();
                    fields[2] = m.getMealTime();
                    fields[3] = r.getId();
                    fields[4] = r.getTitle();
                    fields[5] = r.getId();
                    fields[6] = r.getTitle();
                    return fields;
                })
                .toList();

        template.batchUpdate(UPSERT_MEAL, mList);
    }

    public void updateIngredients(String username, List<Ingredient> ingredients) {
        List<Object[]> items = ingredients.stream()
                .map(ing -> {
                    Object[] fields = new Object[5];
                    fields[0] = ing.getRecipe_id();
                    fields[1] = username;
                    fields[2] = ing.getName();
                    fields[3] = ing.getValue();
                    fields[4] = ing.getUnit();
                    return fields;
                })
                .toList();

        template.batchUpdate(INSERT_INGREDIENT, items);
    }

    public Optional<List<Meal>> getUserMealPlan(String username, Date startDate, Date endDate) {
        SqlRowSet rs = template.queryForRowSet(GET_MEAL_PLAN, username, startDate, endDate);
        if (!rs.next())
            return Optional.empty();
        List<Meal> meals = new LinkedList<>();
        while (rs.next()) {
            Meal m = new Meal();
            m.setMealDate(rs.getDate("mealDate"));
            m.setMealTime(rs.getString("mealTime"));
            Recipe r = recipeRepo.getMealDetails(username, rs.getInt("mealId"));
            m.setMealDetails(r);
            meals.add(m);
        }
        return Optional.of(meals);
    }

    public List<Meal> getMealPlanForUser(String username) {
        SqlRowSet rs = template.queryForRowSet(GET_MEAL_PLAN_FOR_USER, username);
        List<Meal> meals = new LinkedList<>();
        while (rs.next()) {
            Meal m = new Meal();
            m.setMealDate(rs.getDate("mealDate"));
            m.setMealTime(rs.getString("mealTime"));
            Recipe r = recipeRepo.getMealDetails(username, rs.getInt("mealId"));
            m.setMealDetails(r);
            meals.add(m);
        }
        return meals;
    }

    public List<Integer> getRecipeIds(String username){
        SqlRowSet rs = template.queryForRowSet(GET_MEAL_PLAN_FOR_USER, username);
        List<Integer> ids = new LinkedList<>();
        while (rs.next()) {
            ids.add(rs.getInt("mealId"));
        }
        return ids;
    }

    public void deleteMeal(Date mealDate, String mealTime) {
        template.update(DELETE_MEAL, mealDate, mealTime);
    }

    public void deleteIngredients(String username){
        template.update(DELETE_INGREDIENT, username);
    }

    public Optional<List<Ingredient>> getIngredients(int id){
        SqlRowSet rs = template.queryForRowSet(GET_RECIPE_INGREDIENT, id);
        List<Ingredient> ingredients = new LinkedList<>();
        if (!rs.next())
            return Optional.empty();
        while (rs.next()){
            Ingredient ing = new Ingredient();
            ing.setRecipe_id(rs.getInt("recipe_id"));
            ing.setName(rs.getString("ingredientName"));
            ing.setValue(rs.getDouble("quantity"));
            ing.setUnit(rs.getString("unit"));
            ingredients.add(ing);
        }
        return Optional.of(ingredients);
    }

    

}
