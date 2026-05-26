package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Order;
import service.OrderService;

public class OrderTab {

    private OrderService orderService;
    private ObservableList<Order> orderData;
    private TableView<Order> orderTable;

    public OrderTab(OrderService orderService) {
        this.orderService = orderService;
    }

    public Tab createTab() {
        Tab tab = new Tab("📜 Order History");
        tab.setClosable(false);

        orderData = FXCollections.observableArrayList(orderService.getOrderHistory());

        orderTable = new TableView<>();

        TableColumn<Order, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        idCol.setPrefWidth(80);

        TableColumn<Order, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(new PropertyValueFactory<>("itemsSummary"));
        itemsCol.setPrefWidth(220);

        TableColumn<Order, Double> totalCol = new TableColumn<>("Total (Rs.)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalCol.setPrefWidth(90);

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(160);

        TableColumn<Order, String> timeCol = new TableColumn<>("Order Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
        timeCol.setPrefWidth(130);

        // ✅ Manager "Mark Delivered" button column
        TableColumn<Order, Void> actionCol = new TableColumn<>("Manager Action");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deliverBtn = new Button("✅ Mark Delivered");
            {
                deliverBtn.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-font-size: 11px;"
                );
                deliverBtn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());

                    // Only allow if not already delivered
                    if (order.getStatus().equals("✅ Delivered!") ||
                        order.getStatus().equals("Delivered")) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("Already Delivered");
                        info.setHeaderText(null);
                        info.setContentText("Order #" + order.getOrderId() +
                            " is already marked as delivered.");
                        info.showAndWait();
                        return;
                    }

                    // Confirmation dialog
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Delivery");
                    confirm.setHeaderText("Mark Order #" + order.getOrderId() + " as Delivered?");
                    confirm.setContentText("This will update the order status to Delivered.\nThis action cannot be undone.");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            order.setStatus("✅ Delivered!");
                            orderService.saveAllOrders();
                            // Refresh table to show updated status
                            orderData.setAll(orderService.getOrderHistory());

                            Alert success = new Alert(Alert.AlertType.INFORMATION);
                            success.setTitle("Delivery Confirmed");
                            success.setHeaderText(null);
                            success.setContentText("✅ Order #" + order.getOrderId() +
                                " marked as Delivered!\nTotal collected: Rs." +
                                order.getTotalAmount());
                            success.showAndWait();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    // Grey out button if already delivered
                    if (order.getStatus().equals("✅ Delivered!") ||
                        order.getStatus().equals("Delivered")) {
                        deliverBtn.setStyle(
                            "-fx-background-color: #9E9E9E; -fx-text-fill: white; " +
                            "-fx-font-size: 11px;"
                        );
                        deliverBtn.setText("☑ Delivered");
                    } else {
                        deliverBtn.setStyle(
                            "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-font-size: 11px;"
                        );
                        deliverBtn.setText("✅ Mark Delivered");
                    }
                    setGraphic(deliverBtn);
                }
            }
        });

        orderTable.getColumns().addAll(idCol, itemsCol, totalCol, statusCol, timeCol, actionCol);
        orderTable.setItems(orderData);
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: #FF6B35; -fx-text-fill: white; -fx-font-weight: bold;"
        );
        refreshBtn.setOnAction(e -> refresh());

        Label hint = new Label(
            "💡 Use 'Mark Delivered' to confirm delivery  |  🔄 Refresh to see latest status"
        );
        hint.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        // Summary bar
        Label summaryLabel = new Label();
        summaryLabel.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;"
        );
        updateSummary(summaryLabel);

        refreshBtn.setOnAction(e -> {
            refresh();
            updateSummary(summaryLabel);
        });

        HBox toolbar = new HBox(10, refreshBtn, hint);
        toolbar.setPadding(new Insets(8));
        toolbar.setStyle("-fx-background-color: #FFF3E0; -fx-border-color: #FFE0B2; -fx-border-width: 0 0 1 0;");

        HBox summaryBar = new HBox(summaryLabel);
        summaryBar.setPadding(new Insets(6, 12, 6, 12));
        summaryBar.setStyle("-fx-background-color: #E8F5E9;");

        VBox content = new VBox(0, toolbar, summaryBar, orderTable);
        content.setPadding(new Insets(0));
        VBox.setVgrow(orderTable, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void updateSummary(Label label) {
        int total = orderService.getOrderHistory().size();
        long delivered = orderService.getOrderHistory().stream()
            .filter(o -> o.getStatus().equals("✅ Delivered!") ||
                         o.getStatus().equals("Delivered"))
            .count();
        long pending = total - delivered;
        double revenue = orderService.getOrderHistory().stream()
            .filter(o -> o.getStatus().equals("✅ Delivered!") ||
                         o.getStatus().equals("Delivered"))
            .mapToDouble(Order::getTotalAmount)
            .sum();
        label.setText(String.format(
            "📦 Total Orders: %d   |   ✅ Delivered: %d   |   ⏳ Pending: %d   |   💰 Revenue: Rs.%.2f",
            total, delivered, pending, revenue
        ));
    }

    public void refresh() {
        orderData.setAll(orderService.getOrderHistory());
    }
}