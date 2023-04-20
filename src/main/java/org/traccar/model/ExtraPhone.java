package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("extraphones")
public class ExtraPhone extends BaseModel {
    private int iduser;

    public int getUserid() {
        return this.iduser;
    }

    public void setUserid(int iduser) {
        this.iduser = iduser;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
}
