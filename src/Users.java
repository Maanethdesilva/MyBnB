import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Users {
    private static final Scanner sc = new Scanner(System.in);
        
    public static void createUser(Connection con){
        try {
            // Assume that user will always enter all of their data (:
            System.out.println("Enter your Username: ");
            String username = sc.next();
            
            System.out.println("Enter your Password: ");
            String password = sc.next();

            System.out.println("Enter your Name: ");
            String name = sc.next();
            
            System.out.println("Enter your Address: ");
            String address = sc.next();

            String dob;
            do {
                System.out.println("Enter your Date of Birth (yyyy-mm-dd): ");
                dob = sc.next();
            } while (!isValidDateFormat(dob));

            System.out.println("Enter your SIN I am the bank >;) : ");
            int SIN = sc.nextInt();
            sc.nextLine();
            
            System.out.println("Enter your Occupation: ");
            String occupation = sc.next();
            
            System.out.println("Enter which type of user you are (h - host or r - renter): ");
            String type = sc.next();
            
            // only collect a renter's payment info
            String payment;
            if (type.equals("r")){
                System.out.println("Enter your payment info hehehehe >:) : ");
                payment = sc.next();
            } else {
                payment = "null";
            }
            
            String query = "INSERT INTO User (`Username`, `Password`, `Name`, `Address`, `DOB`, `Occupation`, `SIN`, `Type`, `PaymentInfo`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement stmt = con.prepareStatement(query);
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setString(4, address);
            stmt.setString(5, dob);
            stmt.setString(6, occupation);
            stmt.setInt(7, SIN);
            stmt.setString(8, type);
            stmt.setString(9, payment);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {
                System.out.println("Added successfully!");
            } else {
                System.out.println("Failed");
            }
            App.startApp(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Regular expression to check the date format (yyyy-mm-dd)
    private static final Pattern DATE_FORMAT_REGEX = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    // Method to validate the date format (yyyy-mm-dd)
    private static boolean isValidDateFormat(String date) {
        Matcher matcher = DATE_FORMAT_REGEX.matcher(date);
        return matcher.matches();
    }

    public static void loginUser(Connection con) {
        System.out.println("Enter your username: ");
        String username = sc.next();
        System.out.println("Enter your password: ");
        String password = sc.next();

        try {
            // Prepare the SQL query to check if the credentials are valid in the database.
            String query = "SELECT * FROM User WHERE Username = ? AND Password = ?";
            String query2 = "SELECT * FROM User WHERE Username = ? AND Password = ? AND Type = 'h'";
            
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            PreparedStatement stmt2 = con.prepareStatement(query2);
            stmt2.setString(1, username);
            stmt2.setString(2, password);

            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();
            ResultSet rs2 = stmt2.executeQuery();

            // Check if the result set has any rows (i.e., if the credentials match any user in the database)
            boolean isValidUser = rs.next();

            int hostID = -1; // Initialize the hostID with a default value
            int renterID = -1;

            if (isValidUser){
                boolean isHost = rs2.next();
                if (isHost) {
                    hostID = rs2.getInt("UserId");
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    Menus.hostMenu(con, hostID);
                } else {
                    renterID = rs.getInt("UserId");
                    Menus.renterMenu(con, renterID); 
                }
            }else {
                System.out.println("Unsuccessful Login");
            }
            
            // Close the resources
            rs.close();
            stmt.close();
            rs2.close();
            stmt2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
