package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
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

        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}