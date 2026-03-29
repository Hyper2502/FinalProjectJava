package database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WorkstationDAO {

    private static final String DB_URL = "jdbc:h2:file:./myapp_data;DB_CLOSE_DELAY=-1";

    // 🔹 SAVE
    public static void save(String specifications) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO  workstation (specifications, IsAvailable, IsBroken) VALUES (?, ?, ?)")) {

            ps.setString(1, specifications);
            ps.setBoolean(2, true);
            ps.setBoolean(3, false);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 DELETE
    public static void delete(String username) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM workstation WHERE computerID = ?")) {

            ps.setString(1, username);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE specifications
    public static boolean updateSpecifications(int computerID, String specifications) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE workstation SET specifications = ? WHERE computerID = ?")) {

            st.setString(1, specifications);
            st.setInt(2, computerID);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE Availability
    public static boolean updateAvailability(int computerID, boolean Available) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE workstation SET IsAvailable = ? WHERE computerID = ?")) {

            st.setBoolean(1, Available);
            st.setInt(2, computerID);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 UPDATE Broken
    public static boolean updateBroken(int computerID, boolean broken) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE workstation SET IsBroken = ? WHERE computerID = ?")) {

            st.setBoolean(1, broken);
            st.setInt(2, computerID);

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 get all workstation
    public static List<String> getAllWorkstation() {
        List<String> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT * FROM workstation")) {

            while (rs.next()) {
                String workstation = String.format("%d: %s | Is broken: %s | Is Available? %s",
                        rs.getInt("computerID"),
                        rs.getString("specifications"),
                        rs.getBoolean("IsBroken"),
                        rs.getBoolean("IsAvailable")
                );

                list.add(workstation);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public static List<String> CheckAvailability() {
        List<String> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT computerID, specifications from workstation WHERE IsBroken = false and IsAvailable = TRUE")) {

            while (rs.next()) {
                String workstation = String.format("%d: %s",
                        rs.getInt("computerID"),
                        rs.getString("specifications")
                );

                list.add(workstation);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}