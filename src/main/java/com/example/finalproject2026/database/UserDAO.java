package com.example.finalproject2026.database;

import com.example.finalproject2026.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final String DB_URL = "jdbc:h2:file:./myapp_data;DB_CLOSE_DELAY=-1";

    // 🔹 CREATE
    public static void save(String username, String email, String password, boolean is_admin) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (username, email, password, is_admin) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setBoolean(4, is_admin);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 DELETE
    public static void delete(String username) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM users WHERE username = ?")) {

            ps.setString(1, username);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getUserID(String username) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id FROM users WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("id");
                return  userID;
            }else {
                return -1;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE USERNAME
    public static boolean updateUsername(String oldUsername, String newUsername) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE users SET username = ? WHERE username = ?")) {

            st.setString(1, newUsername);
            st.setString(2, oldUsername);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE PASSWORD
    public static boolean updatePassword(String username, String newPassword) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE users SET password = ? WHERE username = ?")) {

            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            st.setString(1, hashed);
            st.setString(2, username);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE PRIVILEGES
    public static boolean updatePrivileges(String username, boolean is_admin) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE users SET is_admin = ? WHERE username = ?")) {

            st.setBoolean(1, is_admin);
            st.setString(2, username);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE EMAIL
    public static boolean updateEmail(String username, String newEmail) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE users SET email = ? WHERE username = ?")) {

            st.setString(1, newEmail);
            st.setString(2, username);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean ValidateUsername(String username) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "SELECT username from users WHERE username = ?")) {

            st.setString(1, username);



            ResultSet rs = st.executeQuery();

            if (rs.next()){
                String foundUsername = rs.getString("username");
                System.out.println("Username found!: " + foundUsername);
                return  true;
            }else {
                System.out.println("Username was not found.");
                return  false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User lookByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fix: setString needs index (1) and value
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getBoolean("is_admin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // User not found
    }

    // 🔹 READ ALL USERS
    public static List<String> UsergetAll() {
        List<String> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT id, username, created_at, is_admin FROM users")) {

            while (rs.next()) {
                String formattedTime = rs.getTimestamp("created_at")
                        .toLocalDateTime()
                        .format(formatter);

                String user = String.format("%d: %s - %s -> %s",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getBoolean("is_admin"),
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