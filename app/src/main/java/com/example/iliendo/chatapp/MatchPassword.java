package com.example.iliendo.chatapp;

/**
 * Created by iliendo on 6/3/19.
 */

public class MatchPassword {

    /**
     * Checks whether the password match with each other
     * @param password provided by the user
     * @param passwordRepeat provided by the user
     * @return
     */
    public boolean matchPassword(String password, String passwordRepeat){
        if(password.equals(passwordRepeat)){
            return true;
        }
        return false;
    }
}
