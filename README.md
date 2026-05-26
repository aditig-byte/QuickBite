#  QuickBite — Restaurant Order Management Terminal
> A desktop billing and order management application for restaurant staff, built with **Java + JavaFX**, demonstrating all core Object-Oriented Software Development concepts from Encapsulation and Generics to Multithreading and Serialization.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-26-blue?style=flat-square)
![Platform](https://img.shields.io/badge/Platform-Desktop-green?style=flat-square)
![Storage](https://img.shields.io/badge/Storage-Serialization-purple?style=flat-square)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=flat-square)

---

##  What is QuickBite?

QuickBite is a Restaurant Order Management Terminal, a desktop application used by restaurant staff to take customer orders at the counter, manage billing with 18% GST, and track order status from placement through to delivery confirmation.


---

##  Features

| Feature | Description |
|---|---|
|  **Menu Tab** | Browse 10+ food items in a TableView with ID, name, category, price |
|  **Search & Filter** | Real-time search by name + filter by category via ComboBox |
|  **Add Menu Items** | Admin accordion panel to add new items dynamically |
|  **Cart System** | Add/remove items, view itemized cart with running total |
|  **GST Billing** | 18% GST applied automatically on checkout |
|  **Order Placement** | Place order → get Order ID → cart clears instantly |
|  **Live Status Updates** | Background thread simulates 5 order stages in real time |
|  **Manager Delivery** | Manager confirms delivery via button → status saved permanently |
|  **Order History** | Full history with timestamps, status, and revenue analytics |
|  **Data Persistence** | Everything saved via Java Serialization — survives app restart |

---

## Tech Stack
- Java 25
- JavaFX 26
- Java Serialization (File Handling)
- Multithreading
- Collections Framework (ArrayList, HashMap)
- Generics
## OOP Concepts Demonstrated
This project is built around OOSD principles — not just mentioned, but actively implemented:
```
UI Layer  →  calls  →  Service Layer  →  calls  →  FileHandler
CartTab           OrderService                  ObjectOutputStream
MenuTab           FoodService                   ObjectInputStream
OrderTab          (business logic)              (file internals hidden)
```
##  Project Structure
```
QuickBite/
│
├── src/
│   ├── MainApp.java              ← JavaFX entry point, Stage + TabPane setup
│   │
│   ├── model/
│   │   ├── FoodItem.java         ← Serializable | Encapsulation | id, name, price, category
│   │   ├── Order.java            ← Serializable | GST calc | status | timestamp
│   │   └── CartItem.java         ← Generic class CartItem<T> | Week 7 Generics
│   │
│   ├── service/
│   │   ├── FoodService.java      ← ArrayList + HashMap | search/filter | file save
│   │   └── OrderService.java     ← Order placement | daemon thread | Platform.runLater
│   │
│   ├── ui/
│   │   ├── MenuTab.java          ← TableView | ComboBox | TitledPane | Add to Cart
│   │   ├── CartTab.java          ← TableView | GST total | Place Order | Clear Cart
│   │   └── OrderTab.java         ← TableView | Mark Delivered | Summary bar
│   │
│   └── util/
│       └── FileHandler.java      ← Serialization | ObjectOutputStream | ObjectInputStream
│
└── data/
├── menu.dat                  ← Serialized ArrayList<FoodItem>
└── orders.dat                ← Serialized ArrayList<Order>
```
##  How to Run

**Prerequisites:**
- Java 25+ installed
- JavaFX SDK 26 extracted (e.g. at `C:\javafx-sdk-26\lib`)

**Compile:**
```bash
javac --module-path C:\javafx-sdk-26\lib --add-modules javafx.controls,javafx.fxml ^
  -d out -sourcepath src ^
  src\MainApp.java src\model\FoodItem.java src\model\Order.java ^
  src\model\CartItem.java src\service\FoodService.java ^
  src\service\OrderService.java src\util\FileHandler.java ^
  src\ui\MenuTab.java src\ui\CartTab.java src\ui\OrderTab.java
```

**Run:**
```bash
java --module-path C:\javafx-sdk-26\lib --add-modules javafx.controls,javafx.fxml -cp out MainApp
```

**Reset saved data (if needed):**
```bash
del data\menu.dat
del data\orders.dat
```

---
