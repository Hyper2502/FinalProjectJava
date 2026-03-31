package manager;

import model.Session;
import database.SessionDAO;
import java.util.ArrayList;
import java.util.List;


public class SessionManager {
    private List<Session> sessions;
    private int nextSessionId;
    private SessionDAO sessionDAO;
    private static final double DEFAULT_RATE = 2.50;

    public SessionManager() {
        sessions = new ArrayList<>();
        this.sessionDAO = new SessionDAO();
        nextSessionId = 1000;

        this.sessions.addAll(sessionDAO.getAllSessions());

        if (!sessions.isEmpty()) {
            int maxId = 0;
            for (Session s : sessions) {
                if (s.getSessionId() > maxId) {
                    maxId = s.getSessionId();
                }
            }
            nextSessionId = maxId + 1;
        }
    }

    public Session startSession(int computerId, String
            username) {
        if (isComputerInUse(computerId)) {
            throw new IllegalStateException("The Computer "
                    + computerId + " is busy ");
        }
        Session s = new Session(nextSessionId++, computerId, username, DEFAULT_RATE);
        sessions.add(s);
        return s;
    }

    public boolean endSession(int sessionId) {
        for (Session s : sessions) {
            if (s.getSessionId() == sessionId &&
                    s.isActive()) {
                s.endSession();
                return true;
            }
        }
        return false;
    }

    public boolean isComputerInUse(int computerId) {
        for (Session s : sessions) {
            if (s.getComputerId() == computerId && s.isActive())
                return true;
        }
        return false;
    }

    public List<Session> getSessions() {
        List<Session> active = new ArrayList<>();
        for (Session s : sessions) if (s.isActive()) active.add(s);
        return active;
    }

    public List<Session> getAllSessions() {
        return new ArrayList<>(sessions);
    }

   public double getBill(int sessionId){
       for (Session s: sessions){
           if (s.getSessionId() == sessionId) return s.calculateCost();
       }
       return 0.0;
   }
}
