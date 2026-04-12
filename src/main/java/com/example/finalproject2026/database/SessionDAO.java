package com.example.finalproject2026.database;

import com.example.finalproject2026.model.Session;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {


    // For "Today: $X.XX" on Dashboard
    public double getTodayRevenue() {
        String sql = "SELECT SUM(total_cost) FROM sessions WHERE DATE(start_time) = CURRENT_DATE AND is_active = FALSE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    // For "All Month: $X.XX" on Dashboard
    public double getMonthlyRevenue() {
        String sql = "SELECT SUM(total_cost) FROM sessions WHERE start_time >= DATE_TRUNC('month', CURRENT_DATE) AND is_active = FALSE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    public Session saveSession(Session session) {
        String sql = """
        INSERT INTO sessions
        (computer_id, user_id, start_time, end_time, hourly_rate, is_active, total_cost)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1️⃣ computer_id
            ps.setInt(1, session.getComputerId());
            // 2️⃣ user_id
            ps.setInt(2, session.getUserID());

            // 3️⃣ start_time ✅ ALWAYS set
            ps.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));

            // 4️⃣ end_time
            if (session.getEndTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            // 5️⃣ hourly_rate
            ps.setDouble(5, session.getHourlyRate());

            // 6️⃣ is_active
            ps.setBoolean(6, session.isActive());

            // 7️⃣ total_cost
            if (session.isActive()) {
                ps.setNull(7, Types.DOUBLE);
            } else {
                ps.setDouble(7, session.calculateCost());
            }

            ps.executeUpdate();

            // 🔑 Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                session.setSessionId(rs.getInt(1));
            }

            return session;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save Session", e);
        }
    }
    public void updateSession(Session session) {
        String sql = """
                UPDATE sessions
                SET end_time = ?, is_active = ?, total_cost = ?
                WHERE session_id = ?
                """;

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {


            if(session.getEndTime() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(session.getEndTime()));
            }else{
                ps.setNull(1, Types.TIMESTAMP);
            }
            ps.setBoolean(2, session.isActive());

            if (!session.isActive()) {
                ps.setDouble(3, session.calculateCost());
            }else{
                ps.setNull(3, Types.DOUBLE);
            }
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

                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();

                Timestamp endTs = rs.getTimestamp("end_time");
                LocalDateTime end = (endTs != null) ? endTs.toLocalDateTime() : null;

                Session s = new Session(
                        rs.getInt("session_id"),
                        rs.getInt("computer_id"),
                        rs.getInt("user_id"),
                        start,
                        end,
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_active")
                );

                sessions.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get sessions", e);
        }
        return sessions;
    }

    public List<Session> getActiveSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = """
    SELECT s.*, u.username
    FROM sessions as s
    JOIN users as u ON s.user_id = u.id
    WHERE s.is_active = TRUE
    ORDER BY s.start_time ASC
""";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();

                Timestamp endTs = rs.getTimestamp("end_time");
                LocalDateTime end = (endTs != null) ? endTs.toLocalDateTime() : null;

                Session s = new Session(
                        rs.getInt("session_id"),
                        rs.getInt("computer_id"),
                        rs.getInt("user_id"),
                        start,
                        end,
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_active")
                );

                s.setUsername(rs.getString("username"));

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

    public Session getSessionById(int sessionId) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();

                Timestamp endTs = rs.getTimestamp("end_time");
                LocalDateTime end = (endTs != null) ? endTs.toLocalDateTime() : null;

                return new Session(
                        rs.getInt("session_id"),
                        rs.getInt("computer_id"),
                        rs.getInt("user_id"),
                        start,
                        end,
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_active")
                );
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get session", e);
        }
    }
}
