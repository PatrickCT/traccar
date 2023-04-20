package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("extramails")
public class ExtraMail extends BaseModel {

    private int iduser;

    public int getUserid() {
        return this.iduser;
    }

    public void setUserid(int iduser) {
        this.iduser = iduser;
    }

    private String email;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
