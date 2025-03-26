package vttpb_final_project.travelplanner.models.Request;

import java.util.List;

public class CheckoutRequest {

    private List<ProductRequest> productRequests;
    private String email;
    
    public List<ProductRequest> getProductRequests() {
        return productRequests;
    }
    public void setProductRequests(List<ProductRequest> productRequests) {
        this.productRequests = productRequests;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
}
