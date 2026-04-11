package com.example.finalproject2026.model;

import com.example.finalproject2026.database.UserDAO;

public class User {
    private String username;
    private String password;
    private String email;
    private boolean IsAdmin;

    public User(String username, String password, String email, Boolean IsAdmin){
        this.username = username;
        this.password = password;
        this.email = email;
        this.IsAdmin = IsAdmin;
    }

    public String SaveUser(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;

        try {
            UserDAO.save(username, email, password, IsAdmin);
            return "User saved!";
        }catch (Exception e){
            e.printStackTrace();
            return "Error Saving User";
        }
    }

    public String DeleteUser(String username) {
        this.username = username;

        try{
            UserDAO.delete(username);
            return "User Deleted!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Deleting User!";
        }
    }

    public String UpdateUser(String oldusername, String newusername){
        this.username = oldusername;

        try {
            UserDAO.updateUsername(oldusername, newusername);
            return "Updated Username!";
        }catch (Exception e){
            e.printStackTrace();
            return "Error updating username!";
        }
    }
    public String UpdatePassword(String username, String password){
        this.password = password;

        try {
            UserDAO.updatePassword(username, password);
            return "Updated password!";
        }catch (Exception e){
            e.printStackTrace();
            return "Error updating password!";
        }
    }

    public String UpdateEmail(String username, String email){
        this.email = email;

        try {
            UserDAO.updateEmail(username, email);
            return "Updated Email!";
        }catch (Exception e){
            e.printStackTrace();
            return "Error updating Emmail!";
        }
    }

    public String getPassword(){
        return password;
    }

    public boolean getAdmin(){
        return IsAdmin;
    }
}
