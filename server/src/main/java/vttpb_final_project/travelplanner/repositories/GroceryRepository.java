package vttpb_final_project.travelplanner.repositories;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttpb_final_project.travelplanner.models.Ingredient;

import static vttpb_final_project.travelplanner.models.Ingredient.*;

@Repository
public class GroceryRepository {

    @Autowired 
    private JdbcTemplate template;
    
    public static final String ADD_TO_GROCERY = """
            INSERT INTO grocery_list (ingredient_name, quantity, unit, username, status)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                quantity = quantity + VALUES(QUANTITY);
            """;

    public static final String GET_GROCERY_LIST = """
            SELECT ingredient_name, quantity, unit, status FROM grocery_list where username = ?
            """;

    public static final String DELETE_GROCERY = """
            delete from grocery_list where username = ?
            """;

    public static final String UPDATE_COOKED_INGREDIENTS = """
            UPDATE grocery_list g
            JOIN recipe_ingredients r
                ON g.ingredient_name = r.ingredientName
            SET g.quantity = g.quantity - r.quantity,
                g.status = 'Cooked'
            WHERE g.status = 'Pending';
            """;
    
    public static final String UPDATE_BOUGHT_INGREDIENTS = """
            UPDATE grocery_list
            SET status = 'Bought'
            WHERE ingredient_name = ?
            """;

    public void deleteGrocery (String username){
        template.update(DELETE_GROCERY, username);
    }

    public List<Ingredient> getGroceryList(String username){

        SqlRowSet rs = template.queryForRowSet(GET_GROCERY_LIST, username);
        List<Ingredient> ingredients = new LinkedList<>();
        while (rs.next()){
            ingredients.add(toIngredient(rs));
        }
        return ingredients;
    }

    public void addRecipeToGrocery (String username, List<Ingredient> ingredients){
        List<Object[]> items = ingredients.stream()
                .map(ing -> {
                    Object[] fields = new Object[5];
                    fields[0] = ing.getName();
                    fields[1] = ing.getValue();
                    fields[2] = ing.getUnit();
                    fields[3] = username;
                    fields[4] = "Pending";
                    return fields;
                })
                .toList();

        template.batchUpdate(ADD_TO_GROCERY, items);
    }
}
