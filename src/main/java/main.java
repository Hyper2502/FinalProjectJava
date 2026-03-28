import database.SchemeManager;

import java.util.List;

public class main {
    public static void main(String[] args){
        SchemeManager.InitializeDatabase();

        List<String> users = SchemeManager.getAll();
        users.forEach(System.out::println);
    }
}
