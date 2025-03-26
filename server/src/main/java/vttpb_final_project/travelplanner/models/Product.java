package vttpb_final_project.travelplanner.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Product {
    
    private int id;
    private String name;
    private double price;
    private String image;
    private String category;
    private String description;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public static Product toProduct(SqlRowSet rs){
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setImage(rs.getString("image"));
        p.setCategory(rs.getString("category"));
        p.setDescription(rs.getString("description"));
        return p;
    }

    public static JsonObject toJson(Product p){
        return Json.createObjectBuilder()
            .add("id", p.getId())
            .add("name", p.getName())
            .add("price", p.getPrice())
            .add("image", p.getImage())
            .add("category", p.getCategory())
            .add("description", p.getDescription())
            .build();
    }
}
