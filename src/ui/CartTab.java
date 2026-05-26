package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.FoodItem;
import model.Order;
import service.OrderService;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CartTab {

    private ArrayList<FoodItem> cartItems = new ArrayList<>();
    private ObservableList<FoodItem> cartData;
    private Label totalLabel;
    private Label statusLabel;
    private OrderService orderService;
    private Consumer<Order> onOrderPlaced;

    public CartTab(OrderService orderService, Consumer<Order> onOrderPlaced) {
        this.orderService = orderService;
        this.onOrderPlaced = onOrderPlaced;
    }

    public void addToCart(FoodItem item) {
        cartItems.add(item);
        cartData.setAll(cartItems);
        updateTotal();
    }

    public Tab createTab() {
        Tab tab = new Tab("🛒 Cart");
        tab.setClosable(false);

        cartData = FXCollections.observableArrayList();

        // TableView for cart
        TableView<FoodItem> cartTable = new TableView<>();

        TableColumn<FoodItem, String> nameCol = new TableColumn<>("Item");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<FoodItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(130);

        TableColumn<FoodItem, Double> priceCol = new TableColumn<>("Price (Rs.)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        // Remove button column
        TableColumn<FoodItem, Void> removeCol = new TableColumn<>("Remove");
        removeCol.setPrefWidth(100);
        removeCol.setCellFactory(col -> new TableCell<>() {
            private final Button removeBtn = new Button("🗑️ Remove");
            {
                removeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                removeBtn.setOnAction(e -> {
                    FoodItem item = getTableView().getItems().get(getIndex());
                    cartItems.remove(item);
                    cartData.setAll(cartItems);
                    updateTotal();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });

        cartTable.getColumns().addAll(nameCol, categoryCol, priceCol, removeCol);
        cartTable.setItems(cartData);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Total and checkout
        totalLabel = new Label("Total (incl. 18% gst): Rs0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF6B35; -fx-font-weight: bold;");

        Button checkoutBtn = new Button("✅ Place Order");
        checkoutBtn.setStyle(
            "-fx-background-color: #FF6B35; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 20;"
        );

        Button clearBtn = new Button("🗑️ Clear Cart");
        clearBtn.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");

        checkoutBtn.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert("Cart Empty", "Please add items to cart first!");
                return;
            }
            Order order = orderService.placeOrder(cartItems);
            onOrderPlaced.accept(order);

            // Start thread simulation
            orderService.simulateOrderProcessing(order, statusLabel);

            cartItems.clear();
            cartData.clear();
            updateTotal();

            showAlert("Order Placed! 🎉",
                "Order #" + order.getOrderId() +
                " placed!\nTotal: Rs." + order.getTotalAmount() +
                "\nWatch the status update below!");
        });

        clearBtn.setOnAction(e -> {
            cartItems.clear();
            cartData.clear();
            updateTotal();
        });

        HBox buttons = new HBox(10, checkoutBtn, clearBtn);
        buttons.setPadding(new Insets(10));

        VBox content = new VBox(10, cartTable, totalLabel, statusLabel, buttons);
        content.setPadding(new Insets(10));
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void updateTotal() {
        double total = 0;
        for (FoodItem item : cartItems) total += item.getPrice();
        total = Math.round(total * 1.18 * 100.0) / 100.0;
        totalLabel.setText("Total (incl. 18% gst): Rs." + total);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}