package database;
import org.mindrot.jbcrypt.BCrypt;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SchemeManager {
    private static final String DB_URL = "jdbc:h2:file:./myapp_data;DB_CLOSE_DELAY=-1";

    public static void InitializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
    
                    CREATE TABLE IF NOT EXISTS users (
        id INTEGER AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50),
        email VARCHAR(100) UNIQUE,
        password VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
    """);

        }
        catch (SQLException e){
            throw new RuntimeException("Database -> users Failed to initializate", e);
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
        CREATE TABLE IF NOT EXISTS workstation (
            computerID INTEGER AUTO_INCREMENT PRIMARY KEY,
            specifications VARCHAR(200),
            IsAvailable BOOLEAN,
            IsBroken BOOLEAN
        )
    """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   public static void UserSave(String username, String email, String password){
    try(Connection c = DriverManager.getConnection(DB_URL);
    PreparedStatement ps = c.prepareStatement("INSERT INTO users (username, email, password) values (?, ?, ?)")){
        ps.setString(1, username);
        ps.setString(2, email);
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        ps.setString(3, hashed);
        ps.executeUpdate();
    }catch (SQLException e){
        throw new RuntimeException(e);
    }
    }


    public static void DeleteUser(String username){
        try(Connection c = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE username = ?")){
            ps.setString(1, username);
            int rowsAffectted = ps.executeUpdate();
            if(rowsAffectted == 0){
                System.out.println("No user was found.");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static void ModifyUser(String username){
        System.out.println("Which Option would you like? \n1-Change Username \n2-Change Password\n3-Change Email");
        try(Scanner sc = new Scanner(System.in)){
            int option = 0;
            try{
                option = sc.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Error! not valid data!");
        }

            switch(option){
                    case 1:
                        System.out.println("New Username:\n");
                        String newUsername = sc.nextLine();
                        switch(option){
                            case 1:
                                sc.nextLine(); // 🧹 clear buffer

                                System.out.println("New Username:");


                                try (Connection c = DriverManager.getConnection(DB_URL);
                                     PreparedStatement st = c.prepareStatement(
                                             "UPDATE users SET username = ? WHERE username = ?")) {

                                    st.setString(1, newUsername);
                                    st.setString(2, username);

                                    int rows = st.executeUpdate();

                                    if (rows > 0) {
                                        System.out.println("Username updated successfully!");
                                    } else {
                                        System.out.println("User not found.");
                                    }

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }


            }


        }


    }

    public static List<String> getAll() {
        List<String> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Fixed: added space

        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, username, created_at FROM users")) {

            while (rs.next()) {
                // ✅ INSIDE the loop, after rs.next()
                String formattedTime = rs.getTimestamp("created_at")
                        .toLocalDateTime()
                        .format(formatter);

                String user = String.format("%d: %s - %s",
                        rs.getInt("id"),
                        rs.getString("username"),
                        formattedTime
                );
                list.add(user);
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
