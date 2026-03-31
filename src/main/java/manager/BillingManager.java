package manager;

import model.Session;
import java.time.format.DateTimeFormatter;

public class  BillingManager {
    public String generateReceipt(Session session) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder receipt = new StringBuilder();
        receipt.append("---Cyber Cafe Receipt : ---\n");
        receipt.append("Session ID:").append(session.getSessionId()).append("\n");
        receipt.append("Computer ID:").append(session.getComputerId()).append("\n");
        receipt.append("User:").append(session.getUserID()).append("\n");
        receipt.append("Start:").append(fmt.format(session.getStartTime())).append("\n");

        if (!session.isActive()){
            receipt.append("End:").append(session.getEndTime().format(fmt)).append("\n");
        }

        receipt.append("Duration:").append(session.getDurationMinutes()).append("Minutes\n");
        receipt.append("Hourly Rate: $").append(session.getHourlyRate()).append("Minutes\n");
        receipt.append("Total: $").append(String.format("%.2f",session.calculateCost())).append("\n");
        receipt.append("===========================");
        return receipt.toString();
    }
}
