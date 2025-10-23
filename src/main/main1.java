package main;
import config.config;
import java.util.*;

public class main1 {
    static Scanner sc = new Scanner(System.in);
    static config conf = new config();

    public static void main(String[] args) {
        String reps;
        do {
            System.out.println("\n-----FOOD ORDERING SYSTEM-----");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter Choice: ");
            int mainChoice = sc.nextInt();
            sc.nextLine();

            switch (mainChoice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    System.out.println("Thank you!");
                    return;
                default:
                    System.out.println("Invalid Option!");
            }

            System.out.print("Do you want to continue? (y/n): ");
            reps = sc.nextLine();
        } while (reps.equalsIgnoreCase("y"));
    }

    public static void registerUser() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        while (true) {
            String check = "SELECT * FROM tbl_users WHERE u_email = ?";
            List<Map<String, Object>> res = conf.fetchRecords(check, email);
            if (res.isEmpty()) break;

            System.out.print("Email already exists. Enter another: ");
            email = sc.nextLine();
        }

        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        System.out.print("Enter Role (1 - Admin, 2 - Customer): ");
        int roleNum = sc.nextInt();
        sc.nextLine();

        while (roleNum != 1 && roleNum != 2) {
            System.out.print("Invalid. Enter 1 for Admin or 2 for Customer: ");
            roleNum = sc.nextInt();
            sc.nextLine();
        }

        String role = roleNum == 1 ? "Admin" : "Customer";
        String role_status = roleNum == 1 ? "Approved" : "Pending";

        String sql = "INSERT INTO tbl_users (u_name, u_email, u_password, u_role, u_status) VALUES (?, ?, ?, ?, ?)";
        conf.addRecord(sql, name, email, pass, role, role_status);
        System.out.println("Registered successfully. Waiting for approval.");
    }

    public static void loginUser() {
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

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

        System.out.println("Login successful!");

        if (role.equalsIgnoreCase("Admin")) {
            adminDashboard();
        } else if (role.equalsIgnoreCase("Customer")) {
            customerDashboard();
        }
    }

    public static void adminDashboard() {
    int action;
    do {
        System.out.println("\n--- ADMIN DASHBOARD ---");
        System.out.println("1. View Users");
        System.out.println("2. Approve User");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. Manage Products");
        System.out.println("6. Logout");
        System.out.print("Enter Action: ");
        action = sc.nextInt();
        sc.nextLine();

        switch (action) {
            case 1:
                viewUser();
                break;
            case 2:
                viewUser();
                System.out.print("Enter User ID to Approve: ");
                int id = sc.nextInt();
                sc.nextLine();
                String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                conf.updateRecord(sql, "Approved", id);
                break;
            case 3:
                viewUser();
                updateUser();
                break;
            case 4:
                viewUser();
                deleteUser();
                break;
            case 5:
                manageProducts(); // 👈 Call your new method here
                break;
            case 6:
                System.out.println("Logging out...");
                break;
            default:
                System.out.println("Invalid option.");
        }
    } while (action != 6);
}

    public static void customerDashboard() {
        System.out.println("\n--- CUSTOMER DASHBOARD ---");
        System.out.println("Welcome! More customer features coming soon.");
        
        viewProducts();
        System.out.println("Enter Id to select products: ");
        int id  = sc.nextInt();
        sc.nextLine();
        
        String qry = "SELECT * FROM tbl_products WHERE p_id = ?";
        conf.addRecord(qry, id);
    }

    public static void viewUser() {
        String qry = "SELECT * FROM tbl_users";
        String[] hdrs = {"ID", "Name", "Email", "Password", "Role", "Status"};
        String[] clms = {"u_id", "u_name", "u_email", "u_password", "u_role", "u_status"};

        conf.viewRecords(qry, hdrs, clms);
    }

    public static void updateUser() {
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

        String qry = "UPDATE tbl_users SET u_name=?, u_email=?, u_password=?, u_role=? WHERE u_id=?";
        conf.updateRecord(qry, name, email, pass, role, id);
    }

    public static void deleteUser() {
        System.out.print("Enter User ID to Delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String qry = "DELETE FROM tbl_users WHERE u_id=? VALUES (?)";
        conf.deleteRecord(qry, id);
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
                break;
            case 2:
                viewProducts();
                break;
            case 3:
                updateProduct();
                break;
            case 4:
                deleteProduct();
                break;
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
}

public static void viewProducts() {
    String qry = "SELECT * FROM tbl_products";
    String[] hdrs = {"ID", "Name", "Price", "Stock"};
    String[] clms = {"p_id", "p_name", "p_price", "p_stock"};
    conf.viewRecords(qry, hdrs, clms);
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
}

public static void deleteProduct() {
    viewProducts();
    System.out.print("Enter Product ID to Delete: ");
    int id = sc.nextInt();
    sc.nextLine();

    String sql = "DELETE FROM tbl_products WHERE p_id=?";
    conf.deleteRecord(sql, id);
}

}
