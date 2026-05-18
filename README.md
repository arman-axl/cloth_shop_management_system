# THREADORA OUTFIT
## Cloth Shop Management System
### Project Documentation & Technical Report

**Language:** Java (Swing GUI)  
**Storage:** Flat-file (Text Database)  
**Pattern:** 3-Layer Architecture (Entity / FileIO / GUI)  

Prepared for University Defense

---

# 3.5 Database.txt — Flat File Database Structure

The `database.txt` file acts as the primary storage system for all product information in the application. Instead of using MySQL or Oracle, the project stores records in a plain-text format where each line represents one product.

```txt
10000001|Shirt|1499|50
10000002|Pant|1699|50
10000003|T-Shirt|1199|50
```

## Structure of One Record

Each line follows this pattern:

```txt
ProductID | ProductName | Price | Stock
```

| Field | Description |
|---|---|
| ProductID | Unique 8-digit product identifier |
| ProductName | Name of the clothing item |
| Price | Product price in Taka |
| Stock | Available quantity |

The pipe symbol `|` is used as the separator because it is less likely to appear in normal product names.

---

# 3.6 Billing System — Invoice Generation Process

The billing module allows users to add products into a cart, calculate totals automatically, and save invoices permanently into `billing_history.txt`.

## Billing Workflow

1. User selects a product card.
2. User chooses size and quantity.
3. Clicking **Add to Cart** inserts the item into the billing list.
4. Total cost is calculated dynamically.
5. Clicking **Confirm & Save** generates the final invoice.
6. The invoice is appended into `billing_history.txt`.

### Example Saved Invoice

```txt
Date: 2026-05-19 04:34:07

Shirt (M) x2 = 2998 TK
Pant (L) x1 = 1699 TK

Total: 4697 TK
-----------------------
```

The invoice history file works as a permanent sales log because the system always opens the file in append mode.

---

# 3.7 GUI Design & User Interaction

The graphical interface is developed entirely using Java Swing components. The goal of the interface is to provide a modern, visually organized shopping experience.

## Main Swing Components Used

| Component | Purpose |
|---|---|
| JFrame | Main application window |
| JPanel | Section containers |
| JLabel | Text and image display |
| JButton | User actions |
| JTable | Product management table |
| JList | Billing cart display |
| JComboBox | Size selection |
| JTextField | User input |
| JTabbedPane | Multi-tab navigation |

---

# 3.8 Product Card System

The Shop tab displays products using a card-based layout.

Each card contains:

- Product image
- Product name
- Price
- Stock quantity

Cards are dynamically generated using product data loaded from `database.txt`.

## Card Selection Logic

When the user clicks a product card:

- The selected product name is stored.
- The card border changes color.
- The selected product becomes available for cart operations.

This improves user interaction and provides clear visual feedback.

---

# 3.9 Search Functionality

The Manage Products tab contains a search system that allows admins to quickly locate products.

## How Search Works

The `search(String query)` method:

1. Reads all products from `database.txt`
2. Compares the query with:
   - Product ID
   - Product Name
3. Returns matching results.

The comparison is case-insensitive, making the search more user-friendly.

### Example

```java
Product[] results = fileIO.search("shirt");
```

This returns all products whose names contain the word `shirt`.

---

# 3.10 Event-Driven Programming in Swing

The project follows the event-driven programming model used by Java Swing.

Different listeners are attached to UI components to handle user actions.

| Listener | Purpose |
|---|---|
| ActionListener | Handles button clicks |
| MouseListener | Detects product card clicks |
| ListSelectionListener | Detects JTable row selection |
| ChangeListener | Detects tab switching |
| KeyAdapter | Handles keyboard-based search |

## Example

```java
addButton.addActionListener(e -> addToCart());
```

This means when the button is clicked, the `addToCart()` method executes automatically.

---

# 3.11 Why This Project Uses 3-Layer Architecture

The project separates responsibilities into three independent layers.

| Layer | Responsibility |
|---|---|
| Entity Layer | Stores data models |
| FileIO Layer | Handles file reading/writing |
| GUI Layer | Handles user interaction and display |

## Advantages of This Architecture

- Cleaner code organization
- Easier debugging
- Better maintainability
- Easier future upgrades
- Reusable business logic

For example, if the storage system changes from text files to MySQL, only the FileIO layer needs modification.

---

# 3.12 Future Improvements

Although the system is fully functional, several improvements can make it more powerful.

## Planned Enhancements

- Add user authentication system
- Reduce stock automatically after purchase
- Replace flat-file storage with SQLite/MySQL
- Add invoice export to PDF
- Add billing history viewer
- Package images inside the application JAR
- Add dark/light mode themes

---

# 3.13 Conclusion

THREADORA OUTFIT demonstrates how a complete desktop business application can be developed using pure Java without any external frameworks or databases.

The project successfully implements:

- Product inventory management
- Cart and billing system
- Persistent file-based storage
- CRUD operations
- Event-driven GUI interaction
- Layered software architecture

The system is lightweight, portable, beginner-friendly, and suitable for academic demonstration purposes.

