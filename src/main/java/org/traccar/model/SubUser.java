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
@StorageName("tc_subusers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubUser extends BaseModel {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
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
