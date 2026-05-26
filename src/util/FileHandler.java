package util;

import model.FoodItem;
import model.Order;

import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    private static final String MENU_FILE = "data/menu.dat";
    private static final String ORDERS_FILE = "data/orders.dat";

    // Serialization — Week 6 requirement
    @SuppressWarnings("unchecked")
    public static ArrayList<FoodItem> loadMenu() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(MENU_FILE))) {
            return (ArrayList<FoodItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveMenu(ArrayList<FoodItem> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(MENU_FILE))) {
            oos.writeObject(items);
        } catch (IOException e) {
            System.out.println("Error saving menu: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Order> loadOrders() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(ORDERS_FILE))) {
            return (ArrayList<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveOrders(ArrayList<Order> orders) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ORDERS_FILE))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }
}