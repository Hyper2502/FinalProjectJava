package com.example.finalproject2026.manager;

import com.example.finalproject2026.model.Session;
import java.time.format.DateTimeFormatter;


//This Appears because it haven't been connected to the GIU YET so this is Temporarily
@SuppressWarnings({"FieldCanBeLocal", "unused", "FieldMayBeFinal"})

public class  BillingManager {
    public String generateReceipt(Session session) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder receipt = new StringBuilder();
        receipt.append("---Cyber Cafe Receipt : ---\n");
        receipt.append("Session ID:").append(session.getSessionId()).append("\n");
        receipt.append("Computer ID:").append(session.getComputerId()).append("\n");
        receipt.append("User:").append(session.getUserId()).append("\n");
        receipt.append("Start:").append(fmt.format(session.getStartTime())).append("\n");

        if (!session.isActive()){
            receipt.append("End:").append(session.getEndTime().format(fmt)).append("\n");
        }

        receipt.append("Duration:").append(session.getDurationMinutes()).append("Minutes\n");
        receipt.append("Hourly Rate: $").append(session.gethourlyRate()).append("/hour\n");
        receipt.append("Total: $").append(String.format("%.2f",session.calculateCost())).append("\n");
        receipt.append("===========================");
        return receipt.toString();
    }
}
