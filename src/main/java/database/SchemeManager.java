package database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SchemeManager {

    private static final String DB_URL = "jdbc:h2:file:./myapp_data;DB_CLOSE_DELAY=-1";

    // ✅ Fixed naming + single connection
    public static void initializeDatabase() {
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

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS workstation (
                    computerID INTEGER AUTO_INCREMENT PRIMARY KEY,
                    specifications VARCHAR(200),
                    IsAvailable BOOLEAN,
                    IsBroken BOOLEAN
                )
            """);

        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    // ✅ Fixed naming
    public static void userSave(String username, String email, String password) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {

            ps.setString(1, username);
            ps.setString(2, email);

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            ps.setString(3, hashed);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ Fixed typo
    public static void deleteUser(String username) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM users WHERE username = ?")) {

            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No user was found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ Fixed braces + input handling
    public static void modifyUser(String username, int option) {

        try (Scanner sc = new Scanner(System.in)) {

            switch (option) {
                case 1:
                    //username
                    System.out.println("New Username:");
                    String newUsername = sc.nextLine();

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

                case 2:
                    //Password
                    System.out.println("New password option:");
                    String newpassword = BCrypt.hashpw(sc.nextLine(), BCrypt.gensalt());

                    try(Connection c = DriverManager.getConnection(DB_URL);
                    PreparedStatement st = c.prepareStatement(
                            "UPDATE users SET password = ? WHERE username = ?"
                    )){
                        st.setString(1, newpassword);
                        st.setString(2, username);
                        int rows = st.executeUpdate();

                        if (rows > 0){
                            System.out.println("Password Updated successfully!");
                        }else {
                            System.out.println("User not found.");
                        }
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    //Email
                    System.out.println("New email:");
                    String newemail = sc.nextLine();

                    try(Connection c = DriverManager.getConnection(DB_URL);
                        PreparedStatement st = c.prepareStatement(
                                "UPDATE users SET email = ? WHERE username = ?"
                        )){
                        st.setString(1, newemail);
                        st.setString(2, username);
                        int rows = st.executeUpdate();

                        if (rows > 0){
                            System.out.println("Email Updated succesfully!");
                        }else {
                            System.out.println("User not found.");
                        }
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    public static List<String> getAll() {
        List<String> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT id, username, created_at FROM users")) {

            while (rs.next()) {
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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}