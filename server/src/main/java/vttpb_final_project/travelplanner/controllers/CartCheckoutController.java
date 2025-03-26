package vttpb_final_project.travelplanner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import vttpb_final_project.travelplanner.models.Order;
import vttpb_final_project.travelplanner.models.OrderItem;
import vttpb_final_project.travelplanner.models.Product;
import vttpb_final_project.travelplanner.models.Request.CheckoutRequest;
import vttpb_final_project.travelplanner.models.Request.ProductRequest;
import vttpb_final_project.travelplanner.models.Response.StripeResponse;
import vttpb_final_project.travelplanner.repositories.OrderRepository;
import vttpb_final_project.travelplanner.repositories.ProductsRepository;
import vttpb_final_project.travelplanner.services.StripeService;

import static vttpb_final_project.travelplanner.models.Product.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CartCheckoutController {
    
    @Autowired
    private StripeService stripeSvc;

    @Autowired
    private ProductsRepository productsRepo;

    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/products")
    public ResponseEntity<String> getProducts(){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<Product> products = productsRepo.getProducts();
        products.stream()
            .map(p -> toJson(p))
            .forEach(builder :: add);
        return ResponseEntity.ok(builder.build().toString());
    }

    @PostMapping(path = "/checkout")
    public ResponseEntity<StripeResponse> postCheckout(@RequestBody CheckoutRequest checkout){
        checkout.getProductRequests().forEach(req -> req.setAmount((long)(req.getAmount() * 100)));
        StripeResponse resp = stripeSvc.checkoutCart(
            checkout.getProductRequests(),
            checkout.getEmail()
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping(path = "/orders")
    public ResponseEntity<String> getOrderBySessionId(@RequestParam("session_id") String sessionId) {
        
        Optional<Order> opt = orderRepo.findById(sessionId);
        if (opt.isEmpty()){
            return ResponseEntity.status(404).body(
                Json.createObjectBuilder()
                    .add("error", "Order not found")
                    .build().toString());
        }
        
        Order o = opt.get();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<OrderItem> items = o.getItems();
        for (OrderItem it : items){
            builder.add(Json.createObjectBuilder()
                .add("productName", it.getProductName())
                .add("price", it.getPrice())
                .add("quantity", it.getQuantity())
                .build());
        }

        JsonObject obj = Json.createObjectBuilder()
            .add("id", o.getId())
            .add("stripeSessionId", o.getStripeSessionId())
            .add("customerEmail", o.getCustomerEmail())
            .add("orderDate", o.getOrderDate().toString())
            .add("status", o.getStatus())
            .add("totalAmount", o.getTotal())
            .add("currency", o.getCurrency())
            .add("items", builder.build())
            .build();
                
        
        return ResponseEntity.ok(obj.toString());
    }
}
