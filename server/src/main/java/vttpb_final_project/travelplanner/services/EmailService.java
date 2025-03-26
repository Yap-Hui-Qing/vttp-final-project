package vttpb_final_project.travelplanner.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import vttpb_final_project.travelplanner.models.Request.ProductRequest;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPaymentConfirmation(String toEmail, String subject, List<ProductRequest> products, double total) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
    
        StringBuilder text = new StringBuilder();
        text.append("🛍️ Thank You for Your Purchase! 🛍️\n\n");
        text.append("Order Confirmation\n");
        text.append("--------------------\n\n");
        
        text.append("Your Order Details:\n");
        for (ProductRequest product : products) {
            text.append(String.format("  • %-25s | Qty: %-3d | Subtotal: $%.2f\n", 
                product.getName(), 
                product.getQuantity(), 
                product.getAmount()/100.0));
        }
        
        text.append("\n--------------------\n");
        text.append(String.format("Total Amount Paid: $%.2f\n\n", total/100.0));
        
        text.append("Payment Status: Confirmed ✅\n");
        text.append("Thank you for shopping with us!\n");
        text.append("Questions? Contact our support team.");
    
        message.setText(text.toString());
        mailSender.send(message);
    }
}

