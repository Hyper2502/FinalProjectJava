package com.example.finalproject2026.database;

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
                    is_admin BOOLEAN DEFAULT FALSE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
ALTER TABLE users
ADD COLUMN IF NOT EXISTS is_admin BOOLEAN DEFAULT FALSE
""");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS workstation (
                    computerID INTEGER AUTO_INCREMENT PRIMARY KEY,
                    specifications VARCHAR(200),
                    IsAvailable BOOLEAN,
                    IsBroken BOOLEAN
                )
            """);

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS sessions (
        session_id INTEGER AUTO_INCREMENT PRIMARY KEY,
        computer_id INTEGER NOT NULL,
        user_id INTEGER NOT NULL,
        start_time TIMESTAMP NOT NULL,
        end_time TIMESTAMP,
        hourly_rate DOUBLE NOT NULL,
        is_active BOOLEAN DEFAULT TRUE,
        total_cost DOUBLE,
        FOREIGN KEY (computer_id) REFERENCES workstation(computerID),
        FOREIGN KEY (user_id) REFERENCES users(id)
    )
""");

        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static void main(String[] args){

    }
}