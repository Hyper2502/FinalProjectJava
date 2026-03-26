package manager;

import model.Session;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private List<Session> sessions;
    private int nextSessionId;
    private static final double DEFAULT_RATE = 2.50;

   public SessionManager() {
       sessions = new ArrayList<>();
       nextSessionId = 1000;
   }

   public Session startSession(int computerId, String
           username){
       if (isComputerInUse(computerId)) {
           throw new IllegalStateException("The Computer "
                   +computerId+" is busy ");
       }
       Session s = new Session(nextSessionId++, computerId, username, DEFAULT_RATE);
       sessions.add(s);
       return s;
   }

   public boolean endSession(int sessionId){
       for (Session s: sessions){
           if (s.getSessionsId() == sessionId &&
                   s.isActive()){
               s.endSession();
               return true;
           }
       }
       return false;
   }
   public boolean isComputerInUse(int computerId){
       for (Session s: sessions){
           if (s.getComputerId() == computerId && s.isActive())
               return true;
       }
       return false;
   }
   public List<Session> getSessions(){
       List<Session> active = new ArrayList<>();
       for (Session s: sessions) if (s.isActive()) active.add(s);
       return active;
   }
   public double getBill(int sessionId){
       for (Session s: sessions){
           if (s.getSessionsId() == sessionId) return s.calculateCost();
       }
       return 0.0;
   }
}
