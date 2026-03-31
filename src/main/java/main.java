import database.DatabaseInitializer;
import database.SessionDAO;
import database.UserDAO;
import database.WorkstationDAO;
import model.Session;

import java.util.List;

public class main {

    private static final double DEFAULT_RATE = 5.0; // $5/hour

    public static void main(String[] args) {

        DatabaseInitializer.initialize();
        SessionDAO sessionDAO = new SessionDAO();

        // 🔹 1. Create test user
        String username = "testUser";
        int userID = UserDAO.getUserID(username);

        // 🔹 2. Assume computer exists (you should already have one in DB)
        int computerId = 1;

        // (Optional) ensure it's available
        WorkstationDAO.updateAvailability(computerId, true);

        try {
            // 🔹 3. Start session
            System.out.println("Starting session...");
            Session session = new Session(0, computerId, userID, DEFAULT_RATE);
            session = sessionDAO.saveSession(session);

            System.out.println("Session started with ID: " + session.getSessionId());

            // 🔹 4. Wait a few seconds (simulate usage)
            Thread.sleep(3000);

            // 🔹 5. Show active sessions
            System.out.println("\nActive Sessions:");
            List<Session> activeSessions = sessionDAO.getActiveSessions();

            for (Session s : activeSessions) {
                System.out.println(
                        "Session " + s.getSessionId() +
                                " | PC " + s.getComputerId() +
                                " | User: " + s.getUsername() +
                                " | Time: " + s.getDurationMinutes() + " min"
                );
            }

            // 🔹 6. End session
            System.out.println("\nEnding session...");
            session.endSession();
            sessionDAO.updateSession(session);

            WorkstationDAO.updateAvailability(computerId, true);

            // 🔹 7. Show final cost
            System.out.println("Session ended.");
            System.out.println("Total cost: $" + session.calculateCost());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}