# Mini ERP System

An Enterprise Resource Planning (ERP) system built with Java Swing and PostgreSQL for managing customers, products, orders, and inventory.

## Features

### Modules

- **Customer Management** - View and add customers using SQL stored procedures
- **Product Catalog** - Browse products with category filtering and search
- **Order Management** - Create orders with shopping cart functionality
- **Order History** - View customer order history and order details
- **Inventory Management** - Track stock levels and manage restocking

## Technical Stack

- **Language**: Java
- **GUI Framework**: Swing
- **Database**: PostgreSQL
- **JDBC Driver**: PostgreSQL JDBC 42.7.5
- **Environment**: dotenv-java for configuration

## Project Structure

```
erp-swing/
├── lib/
│   ├── postgresql-42.7.5.jar
│   └── dotenv-java-3.0.0.jar
├── src/main/java/
│   ├── dao/
│   │   ├── CustomerDAO.java
│   │   ├── ProductDAO.java
│   │   ├── OrderDAO.java
│   │   └── InventoryDAO.java
│   ├── model/
│   │   ├── Customer.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Order.java
│   │   ├── OrderLine.java
│   │   └── Inventory.java
│   ├── util/
│   │   └── DatabaseManager.java
│   └── view/
│       ├── CustomerView.java
│       ├── AddCustomerForm.java
│       ├── ProductView.java
│       ├── OrderView.java
│       ├── OrderHistoryView.java
│       ├── InventoryView.java
│       └── MainMenuView.java
└── README.md
```

## 🔧 Database Configuration

### JDBC Configuration

The application connects to PostgreSQL using the following default settings:

```java
// Default configuration (can be overridden with .env file)
DB_URL=jdbc:postgresql://localhost:5432/etl_database
DB_USER=postgres
DB_PASSWORD=root
```

### Environment Setup

Create a `.env` file in the project root:

```env
DB_URL=jdbc:postgresql://localhost:5432/(your_database)
DB_USER=(your_username)
DB_PASSWORD=(your_password)
```

### Compilation

```bash
# Compile all sources
javac -cp "lib\postgresql-42.7.5.jar;lib\dotenv-java-3.0.0.jar" -d . src\main\java\util\*.java src\main\java\model\*.java src\main\java\dao\*.java src\main\java\view\*.java
```

### Running the Application

#### Individual Modules

```bash
# Customer Management
java -cp ".;lib\postgresql-42.7.5.jar;lib\dotenv-java-3.0.0.jar" main.java.view.CustomerView

# Product Catalog
java -cp ".;lib\postgresql-42.7.5.jar;lib\dotenv-java-3.0.0.jar" main.java.view.ProductView

# Create Orders
java -cp ".;lib\postgresql-42.7.5.jar;lib\dotenv-java-3.0.0.jar" main.java.view.OrderView

# Order History
java -cp ".;lib\postgresql-42.7.5.jar;lib\dotenv-java-3.0.0.jar" main.java.view.OrderHistoryView

# Inventory Management
java -cp ".;lib\postgresql-42.7.5.jar;lib\dotenv-java-3.0.0.jar" main.java.view.InventoryView
```

## Interface Screenshots

### Customer Management

- View customer list
- Add new customers
- Customer information display

![Customer Management](/src/screenshots/Customer.jpg)

### Product Catalog

- Browse products with category filtering
- Search functionality by product title
- Real-time product information display

![Product Catalog](/src/screenshots/Product.jpg)

### Order Management

- 4-step order process: Customer → Products → Cart → Order
- Shopping cart with add/remove functionality
- Real-time tax and total calculations
- Transaction safety with rollback support

![Order Management](/src/screenshots/Order.jpg)

### Order History

- Customer order history with detailed views
- Order details showing purchased products
- Customer spending analytics

![Order History](/src/screenshots/OrderHistory.jpg)

### Inventory Management

- Stock level monitoring
- Low stock alerts with customizable thresholds
- Restock functionality
- Status indicators (OK/LOW STOCK/OUT OF STOCK)

![Inventory Management](/src/screenshots/inventory.jpg)

**Built with ❤️**
