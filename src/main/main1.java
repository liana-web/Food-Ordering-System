package main;

import config.config;
import java.util.*;

public class main1 {
    static Scanner sc = new Scanner(System.in);
    static config conf = new config();
    static int loggedInUserId = -1;

    public static void main(String[] args) {
        String reps;
        do {
            System.out.println("\n===== FOOD ORDERING SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    registerUser();
                case 2:
                    loginUser();
                case 3:
                {
                    System.out.println("Thank you! Exiting system...");
                    return;
                }
                default:
                    System.out.println("Invalid Option!");
            }

            System.out.print("Do you want to continue? (y/n): ");
            reps = sc.nextLine();
        } while (reps.equalsIgnoreCase("y"));
    }

    public static void registerUser() {
        System.out.println("\n--- USER REGISTRATION ---");
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = sc.nextLine();
            String check = "SELECT * FROM tbl_users WHERE u_email = ?";
            List<Map<String, Object>> res = conf.fetchRecords(check, email);
            if (res.isEmpty()) break;
            System.out.println("Email already exists. Try another.");
        }

        System.out.print("Enter Password: ");
        String pass = conf.hashPassword(sc.nextLine());
        

        System.out.print("Enter Role (1 - Admin, 2 - Customer): ");
        int roleNum = sc.nextInt();
        sc.nextLine();

        while (roleNum != 1 && roleNum != 2) {
            System.out.print("Invalid. Enter 1 for Admin or 2 for Customer: ");
            roleNum = sc.nextInt();
            sc.nextLine();
        }

        String role = roleNum == 1 ? "Admin" : "Customer";
        String status = roleNum == 1 ? "Approved" : "Pending";

        String sql = "INSERT INTO tbl_users (u_name, u_email, u_password, u_role, u_status) VALUES (?, ?, ?, ?, ?)";
        conf.addRecord(sql, name, email, pass, role, status);
        System.out.println("Registration successful. " + (status.equals("Pending") ? "Please wait for admin approval." : "You can now log in."));
    }

    public static void loginUser() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = conf.hashPassword(sc.nextLine());

        String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_password = ?";
        List<Map<String, Object>> res = conf.fetchRecords(qry, email, pass);

        if (res.isEmpty()) {
            System.out.println("Invalid credentials.");
            return;
        }

        Map<String, Object> user = res.get(0);
        String status = user.get("u_status").toString();
        String role = user.get("u_role").toString();

        if (!status.equalsIgnoreCase("Approved")) {
            System.out.println("Account is not approved yet. Please wait for admin approval.");
            return;
        }

        loggedInUserId = Integer.parseInt(user.get("u_id").toString());
        System.out.println("Login successful! Welcome " + user.get("u_name"));

        if (role.equalsIgnoreCase("Admin")) {
            adminDashboard();
        } else {
            customerDashboard();
        }
    }

    public static void adminDashboard() {
        int action;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Products");
            System.out.println("3. Logout");
            System.out.print("Enter Choice: ");
            action = sc.nextInt();
            sc.nextLine();

            switch (action) {
                case 1:
                    manageUsers();
                case 2:
                    manageProducts();
                case 3:
                    System.out.println("Logging out...");
                default:
                    System.out.println("Invalid option.");
            }
        } while (action != 3);
    }

    public static void customerDashboard() {
        int choice;
        do {
            System.out.println("\n===== CUSTOMER DASHBOARD =====");
            System.out.println("1. View Products");
            System.out.println("2. Make an Order");
            System.out.println("3. View My Orders");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewProducts();
                case 2:
                    makeOrder();
                case 3:
                    viewOrderHistory();
                case 4:
                    System.out.println("Logging out...");
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    public static void manageUsers() {
        int choice;
        do {
            System.out.println("\n--- USER MANAGEMENT ---");
            System.out.println("1. View Users");
            System.out.println("2. Approve User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewUsers();
                case 2:
                    approveUser();
                case 3:
                    updateUser();
                case 4 :
                    deleteUser();
                case 5:
                    System.out.println("Returning...");
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

    public static void viewUsers() {
        String qry = "SELECT * FROM tbl_users";
        String[] headers = {"ID", "Name", "Email", "Role", "Status"};
        String[] fields = {"u_id", "u_name", "u_email", "u_role", "u_status"};
        conf.viewRecords(qry, headers, fields);
    }

    public static void approveUser() {
        viewUsers();
        System.out.print("Enter User ID to Approve: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
        conf.updateRecord(sql, "Approved", id);
        System.out.println("User approved successfully.");
    }

    public static void updateUser() {
        viewUsers();
        System.out.print("Enter User ID to Update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("New Name: ");
        String name = sc.nextLine();
        System.out.print("New Email: ");
        String email = sc.nextLine();
        System.out.print("New Password: ");
        String pass = sc.nextLine();
        System.out.print("New Role: ");
        String role = sc.nextLine();

        String sql = "UPDATE tbl_users SET u_name=?, u_email=?, u_password=?, u_role=? WHERE u_id=?";
        conf.updateRecord(sql, name, email, pass, role, id);
        System.out.println("User updated successfully.");
    }

    public static void deleteUser() {
        viewUsers();
        System.out.print("Enter User ID to Delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_users WHERE u_id=?";
        conf.deleteRecord(sql, id);
        System.out.println("User deleted successfully.");
    }

    public static void manageProducts() {
        int choice;
        do {
            System.out.println("\n--- PRODUCT MANAGEMENT ---");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addProduct();
                case 2:
                    viewProducts();
                case 3:
                    updateProduct();
                case 4:
                    deleteProduct();
                case 5:
                    System.out.println("Returning...");
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

    public static void addProduct() {
        System.out.print("Product Name: ");
        String name = sc.nextLine();
        System.out.print("Price: ");
        double price = sc.nextDouble();
        System.out.print("Stock: ");
        int stock = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO tbl_products (p_name, p_price, p_stock) VALUES (?, ?, ?)";
        conf.addRecord(sql, name, price, stock);
        System.out.println("Product added successfully.");
    }

    public static void viewProducts() {
        String qry = "SELECT * FROM tbl_products";
        String[] headers = {"ID", "Name", "Price", "Stock"};
        String[] fields = {"p_id", "p_name", "p_price", "p_stock"};
        conf.viewRecords(qry, headers, fields);
    }

    public static void updateProduct() {
        viewProducts();
        System.out.print("Enter Product ID to Update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("New Name: ");
        String name = sc.nextLine();
        System.out.print("New Price: ");
        double price = sc.nextDouble();
        System.out.print("New Stock: ");
        int stock = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE tbl_products SET p_name=?, p_price=?, p_stock=? WHERE p_id=?";
        conf.updateRecord(sql, name, price, stock, id);
        System.out.println("Product updated successfully.");
    }

    public static void deleteProduct() {
        viewProducts();
        System.out.print("Enter Product ID to Delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_products WHERE p_id=?";
        conf.deleteRecord(sql, id);
        System.out.println("Product deleted successfully.");
    }

    public static void makeOrder() {
        viewProducts();

        System.out.print("\nEnter Product ID to Order: ");
        int productId = sc.nextInt();
        sc.nextLine();

        String productQry = "SELECT * FROM tbl_products WHERE p_id = ?";
        List<Map<String, Object>> productRes = conf.fetchRecords(productQry, productId);

        if (productRes.isEmpty()) {
            System.out.println("No product found with ID: " + productId);
            return;
        }

        Map<String, Object> product = productRes.get(0);
        String productName = product.get("p_name").toString();
        double price = Double.parseDouble(product.get("p_price").toString());
        int stock = Integer.parseInt(product.get("p_stock").toString());

        System.out.println("\n--- PRODUCT DETAILS ---");
        System.out.println("Name: " + productName);
        System.out.println("Price: ₱" + price);
        System.out.println("Available Stock: " + stock);

        System.out.print("\nEnter Quantity: ");
        int qty = sc.nextInt();
        sc.nextLine();

        if (qty > stock) {
            System.out.println("❌ Not enough stock available!");
            return;
        }

        String insertOrder = "INSERT INTO tbl_orders (u_id, o_date, o_status) VALUES (?, DATETIME('now'), ?)";
        conf.addRecord(insertOrder, loggedInUserId, "Pending");

        String getOrderId = "SELECT last_insert_rowid() AS order_id";
        List<Map<String, Object>> orderRes = conf.fetchRecords(getOrderId);
        int orderId = Integer.parseInt(orderRes.get(0).get("order_id").toString());

        String insertDetail = "INSERT INTO tbl_order_detail (order_id, product_id, ord_quantity) VALUES (?, ?, ?)";
        conf.addRecord(insertDetail, orderId, productId, qty);

        String updateStock = "UPDATE tbl_products SET p_stock = p_stock - ? WHERE p_id = ?";
        conf.updateRecord(updateStock, qty, productId);

        System.out.println("✅ Order placed successfully!");
        System.out.println("🧾 You ordered " + qty + "x " + productName + " (₱" + (price * qty) + ")");
    }

    public static void viewOrderHistory() {
        System.out.println("\n--- MY ORDER HISTORY ---");
        String qry ="SELECT o.o_id, o.o_date, o.o_status, p.p_name, p.p_price, d.ord_quantity\n" +
            "FROM tbl_orders o\n" +"JOIN tbl_order_detail d ON o.o_id = d.o_id\n" +
            "JOIN tbl_products p ON d.product_id = p.p_id\n" +
            "WHERE o.u_id = ?\n" +"ORDER BY o.order_date DESC";
            
        List<Map<String, Object>> orders = conf.fetchRecords(qry, loggedInUserId);

        if (orders.isEmpty()) {
            System.out.println("You have no past orders.");
            return;
        }

        double total = 0;
        for (Map<String, Object> order : orders) {
            double price = Double.parseDouble(order.get("p_price").toString());
            int qty = Integer.parseInt(order.get("ord_quantity").toString());
            double subtotal = price * qty;
            total += subtotal;

            System.out.println("\nOrder ID: " + order.get("order_id"));
            System.out.println("Date: " + order.get("order_date"));
            System.out.println("Status: " + order.get("order_status"));
            System.out.println("Product: " + order.get("p_name"));
            System.out.println("Quantity: " + qty);
            System.out.println("Price: ₱" + price);
            System.out.println("Subtotal: ₱" + subtotal);
        }

        System.out.println("\nTOTAL SPENT: ₱" + total);
    }
}
