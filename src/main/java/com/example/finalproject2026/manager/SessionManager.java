package com.example.finalproject2026.manager;

import com.example.finalproject2026.database.UserDAO;
import com.example.finalproject2026.database.WorkstationDAO;
import com.example.finalproject2026.model.Session;
import com.example.finalproject2026.database.SessionDAO;
import java.util.ArrayList;
import java.util.List;

//This Appears because it haven't been connected to the GIU YET so this is Temporarily
@SuppressWarnings({"FieldCanBeLocal", "unused", "FieldMayBeFinal"})

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

    public Session startSession(int computerId, String username) {

        // Validate inputs
        if (!WorkstationDAO.ComputerIDValidation(computerId)) {
            throw new IllegalStateException("Computer does not exist.");
        }

        if (!UserDAO.ValidateUsername(username)) {
            throw new IllegalStateException("User does not exist.");
        }

        // Check availability
        if (!WorkstationDAO.ReturnUsage(computerId)) {
            throw new IllegalStateException("Computer is not available.");
        }

        int userID = UserDAO.getUserID(username);

        // Create session (ID will be assigned by DB) FIXED
        if(userID >= 0) {
            Session session = new Session(nextSessionId++, computerId, userID,DEFAULT_RATE);
            Session savedSession = sessionDAO.saveSession(session);
            WorkstationDAO.updateAvailability(computerId, false);

            return savedSession;
        }else{
            throw new IllegalStateException("Not Valid User ID");
        }

    }

    public boolean endSession(int sessionId) {
        Session session = sessionDAO.getSessionById(sessionId);

        if (session == null){
            return false;
        }
        if (!session.isActive()){
            return false;
        }
        session.endSession();
        sessionDAO.updateSession(session);

        WorkstationDAO.updateAvailability(session.getComputerId(), true);

        System.out.println("Session " + sessionId + " ended. Total: $" + session.calculateCost());

        return true;
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
