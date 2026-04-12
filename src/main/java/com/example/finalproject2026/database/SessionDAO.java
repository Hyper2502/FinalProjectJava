package database;

import model.Session;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection", "unused", "DuplicatedCode"})

public class SessionDAO {
    public Session saveSession(Session session) {
        String sql = """
        INSERT INTO sessions
        (computer_id, user_id, start_time, end_time, hourly_rate, is_active, total_cost)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1 computer_id
            ps.setInt(1, session.getComputerId());

            // 2️ user_id FIXED
            ps.setInt(2, session.getUserId());

            // 3️ start_time ✅ ALWAYS set
            ps.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));

            // 4️ end_time
            if (session.getEndTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            // 5️ hourly_rate FIXED
            ps.setDouble(5, session.gethourlyRate());

            // 6️ is_active FIXED
            ps.setBoolean(6, session.isActive());

            // 7️ total_cost FIXED
            if (session.isActive()) {
                ps.setNull(7, Types.DOUBLE);
            } else {
                ps.setDouble(7, session.gettotalCost());
            }

            ps.executeUpdate();

            // Get generated ID FIXED
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                session.setsessionId(rs.getInt(1));
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
                ps.setDouble(3, session.gettotalCost());
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

                double totalCost = rs.getDouble("total_cost");
                if(rs.wasNull()) totalCost = 0.0;

                Session s = new Session(
                        rs.getInt("session_id"),
                        rs.getInt("computer_id"),
                        rs.getInt("user_id"),
                        null,

                        start,
                        end,
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_active"),
                        totalCost
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
                double totalCost = rs.getDouble("total_cost");
                if(rs.wasNull()) totalCost = 0.0;

                Session s = new Session(
                    rs.getInt("session_id"),
                    rs.getInt("computer_id") ,
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    start,
                    end,
                    rs.getDouble("hourly_rate"),
                    rs.getBoolean("is_active"),
                    totalCost
                );
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

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1,sessionId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                Timestamp endTs = rs.getTimestamp("end_time");
                LocalDateTime end = (endTs != null) ? endTs.toLocalDateTime() : null;

                double totalCost = rs.getDouble("total_cost");
                if(rs.wasNull()) totalCost = 0.0;

                return new Session(
                        rs.getInt("session_id"),
                        rs.getInt("computer_id"),
                        rs.getInt("user_id"),
                        null,
                        start,
                        end,
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_active"),
                        totalCost
                );
            }
            return null;
        } catch(SQLException e){
            throw new RuntimeException("Failed to get session", e);
        }
    }
}
