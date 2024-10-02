/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@StorageName("tc_links")
public class Link extends BaseModel {

    private Date limitDate;
    private int userId;
    private String pass;
    private boolean enabled;
    private String code;
    private String name;

    public Date getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(Date limitDate) {
        this.limitDate = limitDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Link{" + "limitDate=" + limitDate + ", userId=" + userId + ", pass=" + pass + ", enabled=" + enabled + ", code=" + code + ", name=" + name + '}';
    }    

}
