/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("tc_passwords")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Password extends BaseModel {

    private String pass;

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    private int user;

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
