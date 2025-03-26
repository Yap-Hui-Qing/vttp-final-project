package vttpb_final_project.travelplanner.models;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Order {

    private String id;
    private String stripeSessionId;
    private String customerEmail;
    private Date orderDate;
    private String status;
    private String currency;
    private long total;
    private List<OrderItem> items;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getStripeSessionId() {
        return stripeSessionId;
    }
    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static Order toOrder(SqlRowSet rs){
        Order order = new Order();
        order.setId(rs.getString("id"));
        order.setStripeSessionId(rs.getString("stripeSessionId"));
        order.setCustomerEmail(rs.getString("customerEmail"));
        order.setOrderDate(rs.getDate("orderDate"));
        order.setStatus(rs.getString("status"));
        order.setCurrency(rs.getString("currency"));
        order.setTotal(rs.getLong("totalAmount"));
        return order;
    }

}
