package vttpb_final_project.travelplanner.repositories;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttpb_final_project.travelplanner.models.Product;
import static vttpb_final_project.travelplanner.models.Product.*;

@Repository
public class ProductsRepository {
    
    @Autowired
    private JdbcTemplate template;

    private static final String GET_PRODUCTS = """
            select * from products
            """;

    public List<Product> getProducts(){
        List<Product> products = new LinkedList<>();
        SqlRowSet rs = template.queryForRowSet(GET_PRODUCTS);
        while(rs.next()){
            products.add(toProduct(rs));
        }
        return products;
    }
}
