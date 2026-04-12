package com.example.finalproject2026;

import com.example.finalproject2026.database.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestMain {
    public static void main(String[] args) throws Exception {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {

            // Test query
            ResultSet rs = s.executeQuery("SELECT * FROM workstation");
            if (rs.next()) {
                System.out.println("Workstations: " + rs.getInt(1));
            }

            // List all tables
            rs = s.executeQuery("SHOW TABLES");
            System.out.println("\nTables:");
            while (rs.next()) {
                System.out.println(" - " + rs.getString(1));
            }
        }
    }
}