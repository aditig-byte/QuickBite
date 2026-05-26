package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements Serializable {
    private static final long serialVersionUID = 2L;

    private int orderId;
    private ArrayList<FoodItem> items;
    private double totalAmount;
    private String status;
    private String orderTime;

    public Order(int orderId, ArrayList<FoodItem> items) {
        this.orderId = orderId;
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotal();
        this.status = "Placed";
        this.orderTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    private double calculateTotal() {
        double total = 0;
        for (FoodItem item : items) {
            total += item.getPrice();
        }
        // Add 5% tax
        return Math.round(total * 1.18 * 100.0) / 100.0;
    }

    // Getters
    public int getOrderId() { return orderId; }
    public ArrayList<FoodItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getOrderTime() { return orderTime; }

    // Setter
    public void setStatus(String status) { this.status = status; }

    public String getItemsSummary() {
        StringBuilder sb = new StringBuilder();
        for (FoodItem item : items) {
            sb.append(item.getName()).append(", ");
        }
        if (sb.length() > 2)
            sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}