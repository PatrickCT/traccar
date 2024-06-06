/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import java.util.Date;
import org.traccar.storage.StorageName;
/**
 *
 * @author K
 */
@StorageName("tc_salidas")
public class Salida extends BaseModel {

    private boolean finished;
    private Date date;
    private long deviceId;
    private long scheduleId;
    private Date endingDate;
    private long groupId;
    private long subrouteId;
    private boolean valid;
    private long geofenceId;
    private int modifiedBy;
    private Date modifiedWhen;

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {        
        this.finished = finished;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }    

    public long getSubrouteId() {
        return subrouteId;
    }

    public void setSubrouteId(long subrouteId) {
        this.subrouteId = subrouteId;
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedWhen() {
        return modifiedWhen;
    }

    public void setModifiedWhen(Date modifiedWhen) {
        this.modifiedWhen = modifiedWhen;
    }        
            
    @Override
    public String toString() {
        return "Salida{" + "id=" + getId() + ", finished=" + finished + ", date=" + date + ", deviceId=" + deviceId + ", scheduleId=" + scheduleId + ", endingDate=" + endingDate + ", groupId=" + groupId + ", subrouteId=" + subrouteId + '}';
    }          
}
