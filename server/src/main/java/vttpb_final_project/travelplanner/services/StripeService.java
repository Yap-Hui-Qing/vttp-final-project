package vttpb_final_project.travelplanner.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import vttpb_final_project.travelplanner.models.Order;
import vttpb_final_project.travelplanner.models.OrderItem;
import vttpb_final_project.travelplanner.models.Request.ProductRequest;
import vttpb_final_project.travelplanner.models.Response.StripeResponse;
import vttpb_final_project.travelplanner.repositories.OrderRepository;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Autowired
    private EmailService emailSvc;

    @Autowired
    private OrderRepository orderRepo;

    public StripeResponse checkoutCart(List<ProductRequest> productRequests, String customerEmail) {

        Stripe.apiKey = secretKey;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (ProductRequest productRequest : productRequests) {
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                    .builder()
                    .setName(productRequest.getName())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(productRequest.getCurrency() == null ? "SGD" : productRequest.getCurrency())
                    .setUnitAmount(productRequest.getAmount())
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(productRequest.getQuantity())
                    .setPriceData(priceData)
                    .build();

            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .setCustomerEmail(customerEmail)
                .build();

        Session session = null;

        try {
            session = Session.create(params);

            long totalAmount = productRequests.stream()
                    .mapToLong(p -> p.getAmount() * p.getQuantity())
                    .sum();

            emailSvc.sendPaymentConfirmation(
                    customerEmail,
                    "Your Order Confirmation",
                    productRequests,
                    totalAmount);

            Order order = new Order();
            order.setStripeSessionId(session.getId());
            order.setCustomerEmail(customerEmail);
            order.setOrderDate(new Date());
            order.setStatus("Paid");
            order.setCurrency(productRequests.get(0).getCurrency());

            long total = productRequests.stream()
                    .mapToLong(p -> p.getAmount() * p.getQuantity())
                    .sum();
            order.setTotal(total);

            List<OrderItem> items = productRequests.stream()
                    .map(p -> {
                        OrderItem item = new OrderItem();
                        item.setProductName(p.getName());
                        item.setPrice(p.getAmount());
                        item.setQuantity(p.getQuantity());
                        item.setOrder(order);
                        return item;
                    })
                    .collect(Collectors.toList());

            order.setItems(items);
            orderRepo.save(order);

            StripeResponse resp = new StripeResponse();
            resp.setStatus("SUCCESS");
            resp.setMessage("Payment session created");
            resp.setSessionId(session.getId());
            resp.setSessionUrl(session.getUrl());
            return resp;
        } catch (StripeException ex) {
            // log
            System.out.println(ex.getMessage());
            StripeResponse resp = new StripeResponse();
            resp.setStatus("ERROR");
            resp.setMessage(ex.getMessage());
            return resp;
        }

    }
}
