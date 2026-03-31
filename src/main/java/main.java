import database.DatabaseInitializer;
import database.UserDAO;
import manager.BillingManager;
import manager.ReportManager;
import manager.SessionManager;
import model.Session;
import model.User;
import java.util.List;

public class main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        List<String> users = UserDAO.UsergetAll();
        users.forEach(System.out::println);


        System.out.println("\n--SESSION MODULE TEST"); //Test to see if the Manager Classes works perfectly with no Issues.

        SessionManager sm = new SessionManager();
        BillingManager bm = new BillingManager();
        ReportManager rm = new ReportManager();

        Session s = sm.startSession(1, "User");
        System.out.println("Started: " + s.getSessionId());

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        sm.endSession(s.getSessionId());

        System.out.println(rm.generateRevenueReport(sm.getAllSessions()));
        System.out.println("==All modules are working==");
    }
}

