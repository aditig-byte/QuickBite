package service;

import model.FoodItem;
import model.Order;
import util.FileHandler;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class OrderService {
    private ArrayList<Order> orderHistory;
    private int nextOrderId = 1001;

    public OrderService() {
        orderHistory = new ArrayList<>();
        ArrayList<Order> loaded = FileHandler.loadOrders();
        if (loaded != null) {
            orderHistory = loaded;
            for (Order o : orderHistory) {
                if (o.getOrderId() >= nextOrderId)
                    nextOrderId = o.getOrderId() + 1;
            }
        }
    }

    public Order placeOrder(ArrayList<FoodItem> cartItems) {
        Order order = new Order(nextOrderId++, cartItems);
        orderHistory.add(order);
        FileHandler.saveOrders(orderHistory);
        return order;
    }

    public ArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    // NEW: called by OrderTab after manager marks delivered
    public void saveAllOrders() {
        FileHandler.saveOrders(orderHistory);
    }

    public void simulateOrderProcessing(Order order, Label statusLabel) {
        Thread processingThread = new Thread(() -> {
            String[] statuses = {
                "⏳ Order Received...",
                "👨‍🍳 Being Prepared...",
                "📦 Packed & Ready...",
                "🛵 Out for Delivery...",
                "🕐 Waiting for Manager Confirmation..."
            };
            for (String s : statuses) {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(() -> statusLabel.setText(s));
                    order.setStatus(s);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            Platform.runLater(() ->
                statusLabel.setText("🛵 Order out — Manager must confirm delivery in Orders tab")
            );
        });
        processingThread.setDaemon(true);
        processingThread.start();
    }
}