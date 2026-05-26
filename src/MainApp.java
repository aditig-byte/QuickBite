import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import model.Order;
import service.FoodService;
import service.OrderService;
import ui.CartTab;
import ui.MenuTab;
import ui.OrderTab;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Services
        FoodService foodService = new FoodService();
        OrderService orderService = new OrderService();

        // TabPane
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 120px;");

        // ---------------- ORDER TAB ----------------
        OrderTab orderTabController = new OrderTab(orderService);
        Tab orderTab = orderTabController.createTab();

        // ---------------- CART TAB ----------------
        CartTab cartTabController = new CartTab(orderService, (Order order) -> {
            orderTabController.refresh();
        });
        Tab cartTab = cartTabController.createTab();

        // ---------------- MENU TAB ----------------
        MenuTab menuTabController = new MenuTab(
            foodService,
            item -> {
                cartTabController.addToCart(item);

                // Alert when item added
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Added!");
                alert.setHeaderText(null);
                alert.setContentText("✅ " + item.getName() + " added to cart!");
                alert.show();

                // Auto-close alert
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        Platform.runLater(alert::close);
                    } catch (InterruptedException ignored) {}
                }).start();
            }
        );
        Tab menuTab = menuTabController.createTab();

        // Add all tabs
        tabPane.getTabs().addAll(menuTab, cartTab, orderTab);

        // ---------------- HEADER ----------------
        Label header = new Label(" QuickBite — Food Ordering System");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 12 20;"
        );

        HBox headerBox = new HBox(header);
        headerBox.setStyle("-fx-background-color: #1e1e2f;");

        // ---------------- ROOT ----------------
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(tabPane);

        // ---------------- SCENE ----------------
        Scene scene = new Scene(root, 850, 600);

        // Optional CSS
        // scene.getStylesheets().add("file:src/style.css");

        // ---------------- STAGE ----------------
        primaryStage.setTitle("QuickBite");

        // Add app icon
        try {
            primaryStage.getIcons().add(new Image("file:src/icon.png"));
        } catch (Exception e) {
            System.out.println("Icon not found.");
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}