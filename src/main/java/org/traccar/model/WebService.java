/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("tc_webservices")
public class WebService extends BaseModel {

    private String user;
    private String password;
    private String tableName;
    private String extra;
    private boolean enabled;

    public WebService() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getExtra() {
        return extra;
    }       

    @Override
    public String toString() {
        return "WebService{" + "user=" + user + ", password=" + password + ", tableName=" + tableName + ", enabled=" + enabled + '}';
    }    
}

