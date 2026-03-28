package model;

import database.SchemeManager;

public class User {
    private String username;
    private String password;
    private String email;

    public String SaveUser(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;

        try {
            SchemeManager.UserSave(username, email, password);
            return "User saved!";
        }catch (Exception e){
            e.printStackTrace();
            return "Error Saving User";
        }
    }

    public String DeleteUser(String username) {
        this.username = username;

        try{
            SchemeManager.DeleteUser(username);
            return "User Deleted!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Deleting User!";
        }
    }
}
