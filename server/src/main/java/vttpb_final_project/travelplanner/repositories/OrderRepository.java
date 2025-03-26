package vttpb_final_project.travelplanner.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttpb_final_project.travelplanner.models.Order;
import vttpb_final_project.travelplanner.models.OrderItem;

import static vttpb_final_project.travelplanner.models.Order.*;
@Repository
public class OrderRepository {
    
    @Autowired 
    private JdbcTemplate template;

    private static final String INSERT_ORDER = """
        INSERT INTO orders (id, stripeSessionId, customerEmail, orderDate, status, totalAmount, currency)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
    
    private static final String INSERT_ORDER_ITEM = """
        INSERT INTO order_items (order_id, productName, price, quantity)
        VALUES (?, ?, ?, ?)
        """;
    
    private static final String GET_ORDER_BY_ID = """
        SELECT * FROM orders WHERE stripeSessionId = ?
        """;
    
    private static final String GET_ITEMS_FOR_ORDER = """
        SELECT * FROM order_items WHERE order_id = ?
        """;

    private static final String UPDATE_STATUS = """
            UPDATE orders SET status = ? WHERE id = ?
            """;
    
    @Transactional
    public Order save(Order order){
        String id = UUID.randomUUID().toString().substring(0, 8);
        template.update(INSERT_ORDER, 
            id,
            order.getStripeSessionId(),
            order.getCustomerEmail(),
            order.getOrderDate(),
            order.getStatus(),
            order.getTotal(),
            order.getCurrency());

        String orderId = id;
        if (order.getItems() != null && !order.getItems().isEmpty()){
            for (OrderItem item : order.getItems()){
                template.update(INSERT_ORDER_ITEM, orderId, item.getProductName(), (long) (item.getPrice() * 100), item.getQuantity());
            }
        }
        order.setId(orderId);
        return order;
    }

    public Optional<Order> findById(String id){
        SqlRowSet rs = template.queryForRowSet(GET_ORDER_BY_ID, id);
        if (!rs.next()){
            return Optional.empty();
        } else {
            Order order = toOrder(rs);
            if (order != null){
                List<OrderItem> items = template.query(GET_ITEMS_FOR_ORDER,
                    (rowSet, rowNum) -> {
                        OrderItem item = new OrderItem();
                        item.setProductName(rowSet.getString("productName"));
                        item.setPrice(rowSet.getLong("price") / 100.0); // Convert from cents
                        item.setQuantity(rowSet.getInt("quantity"));
                        return item;
                    },
                    order.getId());
                order.setItems(items);
                return Optional.of(order);
            }
            return Optional.empty();
        }
        
    }

    public void updateStatus(String orderId, String status){
        template.update(UPDATE_STATUS, status, orderId);
    }
}
