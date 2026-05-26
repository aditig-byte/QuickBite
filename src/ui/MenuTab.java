package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.FoodItem;
import service.FoodService;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MenuTab {

    private FoodService foodService;
    private Consumer<FoodItem> addToCartCallback;
    private TableView<FoodItem> menuTable;
    private ObservableList<FoodItem> menuData;

    public MenuTab(FoodService foodService, Consumer<FoodItem> addToCartCallback) {
        this.foodService = foodService;
        this.addToCartCallback = addToCartCallback;
    }

    public Tab createTab() {
        Tab tab = new Tab("🍽️ Menu");
        tab.setClosable(false);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search food");
        searchField.setPrefWidth(200);

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll(
                "All", "Fast Food", "Italian", "Chinese", "South Indian", "Indian");
        categoryFilter.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #FF6B35; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox searchBar = new HBox(10, new Label("Category:"),
                categoryFilter, searchField, searchBtn);
        searchBar.setPadding(new Insets(10));

        // TableView — Required by manual
        menuTable = new TableView<>();
        menuData = FXCollections.observableArrayList(foodService.getMenuItems());

        TableColumn<FoodItem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<FoodItem, String> nameCol = new TableColumn<>("Food Item");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<FoodItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);

        TableColumn<FoodItem, Double> priceCol = new TableColumn<>("Price (Rs.)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        // Add to Cart button column
        TableColumn<FoodItem, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button addBtn = new Button("➕ Add to Cart");
            {
                addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                addBtn.setOnAction(e -> {
                    FoodItem item = getTableView().getItems().get(getIndex());
                    addToCartCallback.accept(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addBtn);
            }
        });

        menuTable.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, actionCol);
        menuTable.setItems(menuData);
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Search event
        searchBtn.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            String cat = categoryFilter.getValue();
            ArrayList<FoodItem> results;
            if (!keyword.isEmpty()) {
                results = foodService.searchByName(keyword);
            } else {
                results = foodService.filterByCategory(cat);
            }
            menuData.setAll(results);
        });

        // Reset on clear
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty())
                menuData.setAll(foodService.getMenuItems());
        });

        // Add new item section
        TitledPane addItemPane = createAddItemPane();

        VBox content = new VBox(10, searchBar, menuTable, addItemPane);
        content.setPadding(new Insets(10));
        VBox.setVgrow(menuTable, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private TitledPane createAddItemPane() {
        TextField nameField = new TextField();
        nameField.setPromptText("Food name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll(
                "Fast Food", "Italian", "Chinese", "Japenese", "Mexican");
        catBox.setValue("Fast Food");

        Button addBtn = new Button("Add Item");
        addBtn.setStyle("-fx-background-color: #FF6B35; -fx-text-fill: white; -fx-font-weight: bold;");
        Label msg = new Label();

        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String cat = catBox.getValue();
                if (name.isEmpty()) {
                    msg.setText("❌ Enter item name!");
                    return;
                }
                foodService.addNewItem(name, price, cat);
                menuData.setAll(foodService.getMenuItems());
                nameField.clear();
                priceField.clear();
                msg.setStyle("-fx-text-fill: green;");
                msg.setText("✅ Item added!");
            } catch (NumberFormatException ex) {
                msg.setStyle("-fx-text-fill: red;");
                msg.setText("❌ Invalid price!");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Price:"), 2, 0);
        form.add(priceField, 3, 0);
        form.add(new Label("Category:"), 0, 1);
        form.add(catBox, 1, 1);
        form.add(addBtn, 3, 1);
        form.add(msg, 1, 2);

        TitledPane pane = new TitledPane("➕ Add New Menu Item", form);
        pane.setExpanded(false);
        return pane;
    }
}