package com.example.finalproject2026.database;

import com.example.finalproject2026.model.WorkStation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkstationDAO {

    private static final String DB_URL = "jdbc:h2:file:./myapp_data;DB_CLOSE_DELAY=-1";

    // 🔹 SAVE - returns WorkStation with generated ID
    public static WorkStation save(String specifications) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO workstation (specifications, IsAvailable, IsBroken) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, specifications);
            ps.setBoolean(2, true);
            ps.setBoolean(3, false);

            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            // Return as WorkStation object
            return new WorkStation(id, specifications, true, false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 DELETE - fixed parameter type
    public static void delete(int computerID) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM workstation WHERE computerID = ?")) {

            ps.setInt(1, computerID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 GET BY ID - new method needed
    public static WorkStation getById(int computerID) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM workstation WHERE computerID = ?")) {

            ps.setInt(1, computerID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapToWorkStation(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 GET ALL - renamed and returns List<WorkStation>
    public static List<WorkStation> getAll() {
        List<WorkStation> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM workstation")) {

            while (rs.next()) {
                list.add(mapToWorkStation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // 🔹 Helper method to convert ResultSet to WorkStation
    private static WorkStation mapToWorkStation(ResultSet rs) throws SQLException {
        int id = rs.getInt("computerID");
        String specs = rs.getString("specifications");
        boolean available = rs.getBoolean("IsAvailable");
        boolean broken = rs.getBoolean("IsBroken");

        return new WorkStation(id, specs, available, broken);
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
    public static boolean updateAvailability(int computerID, boolean available) {
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement st = c.prepareStatement(
                     "UPDATE workstation SET IsAvailable = ? WHERE computerID = ?")) {

            st.setBoolean(1, available);
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

    // 🔹 Validation methods (keep existing)
    public static boolean ComputerIDValidation(int computerID) {
        return getById(computerID) != null;
    }

    public static boolean ReturnUsage(int computerID) {
        WorkStation ws = getById(computerID);
        return ws != null && ws.isAvailable() && !ws.isBroken();
    }
}