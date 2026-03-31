package manager;

import model.Session;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ReportManager {

    public String generateRevenueReport(List<Session>Sessions){
        double activeRevenue = 0.0;
        double completedRevenue = 0.0;
        int activeCount = 0;
        int completedCount = 0;

        for(Session s:  Sessions) {
            if (s.isActive()) {
                activeRevenue += s.calculateCost();
                activeCount++;
            } else {
                completedRevenue += s.calculateCost();
                completedCount++;
            }
        }
            return String.format(
                    "===\nRevenue Report Results===" +
                            "Active Sessions: %d (Current $%.2f)\n" +
                            "Completed Sessions: %d (Collected $%.2f)\n" +

                    "Total Revenue: $%.2f\n" +
                            "===================",
                    activeCount, activeRevenue,
                    completedCount,completedRevenue,
                    activeRevenue + completedRevenue
            );
        }
        public String generateUsageReport(List<Session>Sessions){
            Map<Integer, Integer> computerUsage = new HashMap<>();

            for(Session s:  Sessions) {
                int id = s.getComputerId();
                computerUsage.put(id,computerUsage.getOrDefault(id,0)+1);
            }
            StringBuilder report = new StringBuilder();
            report.append("===== Computer Usage Report Results===\n");

            for(Map.Entry<Integer, Integer>entry : computerUsage.entrySet()) {
                report.append("Computer #").append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append("Sessions\n");
            }
            return report.toString();
    }
    public String generateDailySummary(List<Session>Sessions, String date){
        int totalSessions = Sessions.size();
        double totalRevenue = 0.0;
        long totalMinutes = 0;

        for(Session s:  Sessions) {
            totalRevenue += s.calculateCost();
            totalMinutes += s.getDurationMinutes();
        }
        double avgMinutes = totalSessions > 0 ? (double) totalMinutes/totalSessions : 0;

        return String.format(
                "=====\nDaily Summary Results: ========" +
                "Total Sessions: %d\n" +
                        "Total Revenue: $%.2f\n" +
                        "Total Usage: %d minutes\n" +
                        "Average Session: %.1f minutes \n" +
                        "==================================",
                date, totalSessions, totalRevenue,totalMinutes,avgMinutes
        );
    }
}
