import database.DatabaseInitializer;
import database.UserDAO;
import model.User;

import java.util.List;

public class main {
    public static void main(String[] args){
        DatabaseInitializer.initialize();

        List<String> users = UserDAO.UsergetAll();
        users.forEach(System.out::println);
    }
}
