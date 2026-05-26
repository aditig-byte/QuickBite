package service;

import model.FoodItem;
import util.FileHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodService {
    // ArrayList — Week 8 requirement
    private ArrayList<FoodItem> menuItems;
    // HashMap — Week 8 requirement
    private HashMap<Integer, FoodItem> menuMap;
    private int nextId = 1;

    public FoodService() {
        menuItems = new ArrayList<>();
        menuMap = new HashMap<>();
        loadMenu();
        if (menuItems.isEmpty()) {
            loadDefaultMenu();
        }
    }

    private void loadDefaultMenu() {
        addItem(new FoodItem(nextId++, "Burger", 120.0, "Fast Food"));
        addItem(new FoodItem(nextId++, "Pizza", 250.0, "Italian"));
        addItem(new FoodItem(nextId++, "Pasta", 180.0, "Italian"));
        addItem(new FoodItem(nextId++, "Sandwich", 90.0, "Fast Food"));
        addItem(new FoodItem(nextId++, "Noodles", 140.0, "Chinese"));
        addItem(new FoodItem(nextId++, "Ramen", 160.0, "Japenese"));
        addItem(new FoodItem(nextId++, "Sushi", 80.0, "Japenese"));
        addItem(new FoodItem(nextId++, "Dim Sum", 60.0, "Chinese"));
        addItem(new FoodItem(nextId++, "Tacos", 220.0, "Mexican"));
        addItem(new FoodItem(nextId++, "Tamales", 200.0, "Mexican"));
        saveMenu();
    }

    public void addItem(FoodItem item) {
        menuItems.add(item);
        menuMap.put(item.getId(), item);
    }

    public void addNewItem(String name, double price, String category) {
        FoodItem item = new FoodItem(nextId++, name, price, category);
        addItem(item);
        saveMenu();
    }

    public ArrayList<FoodItem> getMenuItems() {
        return menuItems;
    }

    public ArrayList<FoodItem> searchByName(String keyword) {
        ArrayList<FoodItem> result = new ArrayList<>();
        for (FoodItem item : menuItems) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<FoodItem> filterByCategory(String category) {
        if (category.equals("All")) return menuItems;
        ArrayList<FoodItem> result = new ArrayList<>();
        for (FoodItem item : menuItems) {
            if (item.getCategory().equals(category)) {
                result.add(item);
            }
        }
        return result;
    }

    private void saveMenu() {
        FileHandler.saveMenu(menuItems);
    }

    private void loadMenu() {
        ArrayList<FoodItem> loaded = FileHandler.loadMenu();
        if (loaded != null && !loaded.isEmpty()) {
            menuItems = loaded;
            for (FoodItem item : menuItems) {
                menuMap.put(item.getId(), item);
                if (item.getId() >= nextId) nextId = item.getId() + 1;
            }
        }
    }
}