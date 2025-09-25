
package main;
import config.config;
import java.util.Scanner;

public class main1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String reps;
        
        do{
            System.out.println("\n-----FOOD ORDERING SYSTEM-----");
            System.out.println("1. Add User");
            System.out.println("2. View User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Exit");
            
            System.out.println("Enter Action: ");
            int action = sc.nextInt();
            sc.nextLine();
            
            main1 app = new main1();
            
            switch (action) {
            case 1:
                  app.addUser();
                  break;
            case 2:
                  app.viewUser();
                  break;
            case 3:
                  app.viewUser();
                  app.updateUser();
                  break;
            case 4:
                  app.viewUser();
                  app.deleteUser();
                  break;
            case 5:System.out.println("Thank you!");   
                  break;
            default:      
                  System.out.println("Invalid Option!");  
                  break;
        } 
        
            if (action !=5){
                System.out.println("Continue? (y/n): ");
                reps = sc.nextLine();
            }else {
                reps = "n";
            }
            
    }while (reps.equalsIgnoreCase("y"));
        
        sc.close();
    }
    public void addUser(){
    Scanner sc = new Scanner(System.in);
    config conf = new config();
    
    
    System.out.print("Enter Name: ");
    String name = sc.nextLine();
    System.out.print("Enter Email: ");
    String email = sc.nextLine();
    System.out.print("Enter Password: ");
    String pass = sc.nextLine();
    System.out.print("Enter Role (Admin | Customer): ");
    String role = sc.nextLine();
    
    String sql = "INSERT INTO tbl_users (u_name, u_email, u_password, u_role) VALUES (?,?,?,?,?)";
    conf.addRecord(sql, name, email, pass, role);
}
public void viewUser(){
    String qry = "SELECT * FROM tbl_users";
    String[] hdrs = {"ID", "Name", "Email", "Password", "Role"};
    String[] clms = {"u_id", "u_name", "u_email", "u_password", "u_role"};
    
    config conf = new config();
    conf.viewRecords(qry,hdrs,clms);
}
public void updateUser() {
    Scanner sc = new Scanner(System.in);
    
    System.out.println("Enter User ID to Update: ");
    int id = sc.nextInt();
    sc.nextLine();
    
    System.out.println("New Name: ");
    String name = sc.nextLine();
    System.out.println("New Email: ");
    String email = sc.nextLine();
    System.out.println("New Password: ");
    String pass = sc.nextLine();
    System.out.println("New Role: ");
    String role = sc.nextLine();
    
    String qry = "UPDATE tbl_users SET u_name=? ,u_email=?,u_password=?,u_role=? WHERE u_id=?" ;
    config conf = new config();
    conf.updateRecord(qry,name,email,pass,role,id);
}
    public void deleteUser() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter User ID to Delete: ");
        int id = sc.nextInt();
        
    String qry = "DELETE FROM tbl_users WHERE u_id=?";
    config conf = new config();
    conf.deleteRecord(qry, id);
        
        
    }
    
    
}



