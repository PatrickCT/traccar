/*
 * Copyright 2012 - 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.traccar.storage.QueryIgnore;
import org.traccar.storage.StorageName;

import java.util.Date;

@StorageName("tc_devices")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device extends GroupedModel implements Disableable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String uniqueId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public static final String STATUS_UNKNOWN = "unknown";
    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_OFFLINE = "offline";

    private String status;

    @QueryIgnore
    public String getStatus() {
        return status != null ? status : STATUS_OFFLINE;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim() : null;
    }

    private Date lastUpdate;

    @QueryIgnore
    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    private long positionId;

    @QueryIgnore
    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String model;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private boolean disabled;

    @Override
    public boolean getDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private Date expirationTime;

    @Override
    public Date getExpirationTime() {
        return expirationTime;
    }

    @Override
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    private boolean motionStreak;

    @QueryIgnore
    @JsonIgnore
    public boolean getMotionStreak() {
        return motionStreak;
    }

    @JsonIgnore
    public void setMotionStreak(boolean motionStreak) {
        this.motionStreak = motionStreak;
    }

    private boolean motionState;

    @QueryIgnore
    @JsonIgnore
    public boolean getMotionState() {
        return motionState;
    }

    @JsonIgnore
    public void setMotionState(boolean motionState) {
        this.motionState = motionState;
    }

    private Date motionTime;

    @QueryIgnore
    @JsonIgnore
    public Date getMotionTime() {
        return motionTime;
    }

    @JsonIgnore
    public void setMotionTime(Date motionTime) {
        this.motionTime = motionTime;
    }

    private double motionDistance;

    @QueryIgnore
    @JsonIgnore
    public double getMotionDistance() {
        return motionDistance;
    }

    @JsonIgnore
    public void setMotionDistance(double motionDistance) {
        this.motionDistance = motionDistance;
    }

    private boolean overspeedState;

    @QueryIgnore
    @JsonIgnore
    public boolean getOverspeedState() {
        return overspeedState;
    }

    @JsonIgnore
    public void setOverspeedState(boolean overspeedState) {
        this.overspeedState = overspeedState;
    }

    private Date overspeedTime;

    @QueryIgnore
    @JsonIgnore
    public Date getOverspeedTime() {
        return overspeedTime;
    }

    @JsonIgnore
    public void setOverspeedTime(Date overspeedTime) {
        this.overspeedTime = overspeedTime;
    }

    private long overspeedGeofenceId;

    @QueryIgnore
    @JsonIgnore
    public long getOverspeedGeofenceId() {
        return overspeedGeofenceId;
    }

    @JsonIgnore
    public void setOverspeedGeofenceId(long overspeedGeofenceId) {
        this.overspeedGeofenceId = overspeedGeofenceId;
    }

    private String carPlate;
    private String serie;
    private String year;
    private String maker;

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    private String policy;
    private Date insuranceExpiration;

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public Date getInsuranceExpiration() {
        return insuranceExpiration;
    }

    public void setInsuranceExpiration(Date insuranceExpiration) {
        this.insuranceExpiration = insuranceExpiration;
    }

    private int simType;

    public int getSimType() {
        return simType;
    }

    public void setSimType(int simType) {
        this.simType = simType;
    }

    private String simKey;

    public String getSimKey() {
        return simKey;
    }

    public void setSimKey(String simKey) {
        this.simKey = simKey;
    }
    
    private int insuranceCompanyId;

    public int getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(int insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }       

    @Override
    public String toString() {
        return "Device{" + "id=" + getId() + ", name=" + name + ", uniqueId=" + uniqueId + ", status=" + status + ", lastUpdate=" + lastUpdate + ", positionId=" + positionId + ", phone=" + phone + ", model=" + model + ", contact=" + contact + ", category=" + category + ", disabled=" + disabled + ", expirationTime=" + expirationTime + ", motionStreak=" + motionStreak + ", motionState=" + motionState + ", motionTime=" + motionTime + ", motionDistance=" + motionDistance + ", overspeedState=" + overspeedState + ", overspeedTime=" + overspeedTime + ", overspeedGeofenceId=" + overspeedGeofenceId + ", carPlate=" + carPlate + ", serie=" + serie + ", year=" + year + ", maker=" + maker + ", policy=" + policy + ", insuranceExpiration=" + insuranceExpiration + ", simType=" + simType + ", simKey=" + simKey + ", insuranceCompanyId=" + insuranceCompanyId + '}';
    }    

    public String toJson() {
        return "{ name=" + name + ", uniqueId=" + uniqueId + ", status=" + status + ", lastUpdate=" + lastUpdate + ", positionId=" + positionId + ", phone=" + phone + ", model=" + model + ", contact=" + contact + ", category=" + category + ", disabled=" + disabled + ", expirationTime=" + expirationTime + ", motionStreak=" + motionStreak + ", motionState=" + motionState + ", motionTime=" + motionTime + ", motionDistance=" + motionDistance + ", overspeedState=" + overspeedState + ", overspeedTime=" + overspeedTime + ", overspeedGeofenceId=" + overspeedGeofenceId + ", carPlate=" + carPlate + ", serie=" + serie + ", year=" + year + ", maker=" + maker + ", policy=" + policy + ", insuranceExpiration=" + insuranceExpiration + ", simType=" + simType + ", simKey=" + simKey + ", insuranceCompanyId=" + insuranceCompanyId + '}';
    }

    public String toDeviceReport() {
        String report = "Device state" +
                "Current group "+getGroupId()+"" +
                "";

        return report;
    }
}
