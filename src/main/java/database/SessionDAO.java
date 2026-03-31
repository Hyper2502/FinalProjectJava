package database;

import model.Session;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public void saveSession(Session session) {
        String sql = """
                INSERT INTO sessions(session_id,computer_id, username, start_time, end_time, hourly_rate, is_active, total_cost)
                VALUES(?,?,?,?,?,?,?,?)
                """;
        try (Connection conn =
                DBConnection.getConnection();
                             PreparedStatement ps =
                conn.prepareStatement(sql)) {
            ps.setInt(1,session.getSessionId());
            ps.setInt(2,session.getComputerId());
            ps.setString(3,session.getUsername());
            ps.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(5, session.getEndTime() != null ?
                    Timestamp.valueOf(session.getEndTime()): null);
            ps.setDouble(6, session.getHourlyRate());
            ps.setBoolean(7, session.isActive());
            ps.setDouble(8, session.calculateCost());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save Session", e);
        }
    }
    public void updateSession(Session session) {
        String sql = """
                UPDATE sessions SET end_time = ?, is_active = ?, total_cost = ? WHERE session_id = ?
        """;

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, session.getEndTime() != null ?
                    Timestamp.valueOf(session.getEndTime()): null);
            ps.setBoolean(2, session.isActive());
            ps.setDouble(3, session.calculateCost());
            ps.setInt(4, session.getSessionId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Session", e);
        }
    }
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "Select * from sessions";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Session s = new Session(
                        rs.getInt("session_id"),
                        rs.getInt("computer_id"),
                        rs.getString("username"),
                        rs.getDouble("hourly_rate")
                );
                if (!rs.getBoolean("is_active")) {
                    s.endSession();
                }
                sessions.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get sessions", e);
        }
        return sessions;
    }
    public void deleteSession(int sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, sessionId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Session", e);
        }
    }
}
