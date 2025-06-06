/*
 * Copyright 2013 - 2022 Anton Tananaev (anton@traccar.org)
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

import org.traccar.storage.QueryIgnore;
import org.traccar.helper.Hashing;
import org.traccar.storage.StorageName;

import java.util.Date;

@StorageName("tc_users")
public class User extends ExtendedModel implements UserRestrictions, Disableable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private boolean readonly;

    @Override
    public boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    private boolean administrator;

    @QueryIgnore
    @JsonIgnore
    public boolean getManager() {
        return userLimit != 0;
    }

    public boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    private String map;

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private int zoom;

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    private boolean twelveHourFormat;

    public boolean getTwelveHourFormat() {
        return twelveHourFormat;
    }

    public void setTwelveHourFormat(boolean twelveHourFormat) {
        this.twelveHourFormat = twelveHourFormat;
    }

    private String coordinateFormat;

    public String getCoordinateFormat() {
        return coordinateFormat;
    }

    public void setCoordinateFormat(String coordinateFormat) {
        this.coordinateFormat = coordinateFormat;
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

    private int deviceLimit;

    public int getDeviceLimit() {
        return deviceLimit;
    }

    public void setDeviceLimit(int deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    private int userLimit;

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    private boolean deviceReadonly;

    @Override
    public boolean getDeviceReadonly() {
        return deviceReadonly;
    }

    public void setDeviceReadonly(boolean deviceReadonly) {
        this.deviceReadonly = deviceReadonly;
    }

    private boolean limitCommands;

    @Override
    public boolean getLimitCommands() {
        return limitCommands;
    }

    public void setLimitCommands(boolean limitCommands) {
        this.limitCommands = limitCommands;
    }

    private boolean disableReports;

    @Override
    public boolean getDisableReports() {
        return disableReports;
    }

    public void setDisableReports(boolean disableReports) {
        this.disableReports = disableReports;
    }

    private boolean fixedEmail;

    @Override
    public boolean getFixedEmail() {
        return fixedEmail;
    }

    public void setFixedEmail(boolean fixedEmail) {
        this.fixedEmail = fixedEmail;
    }

    private String poiLayer;

    public String getPoiLayer() {
        return poiLayer;
    }

    public void setPoiLayer(String poiLayer) {
        this.poiLayer = poiLayer;
    }

    @QueryIgnore
    public String getPassword() {
        return null;
    }

    @QueryIgnore
    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            Hashing.HashingResult hashingResult = Hashing.createHash(password);
            hashedPassword = hashingResult.getHash();
            salt = hashingResult.getSalt();
        }
    }

    private String hashedPassword;

    @JsonIgnore
    @QueryIgnore
    public String getHashedPassword() {
        return hashedPassword;
    }

    @QueryIgnore
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    private String salt;

    @JsonIgnore
    @QueryIgnore
    public String getSalt() {
        return salt;
    }

    @QueryIgnore
    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isPasswordValid(String password) {
        return Hashing.validatePassword(password, hashedPassword, salt);
    }

    private boolean main;

    public boolean getMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    private int principal;

    public int getPrincipal() {
        return principal;
    }

    public void setPrincipal(int principal) {
        this.principal = principal;
    }

    private int offlineTimeout;

    public int getOfflineTimeout() {
        return offlineTimeout;
    }

    public void setOfflineTimeout(int offlineTimeout) {
        this.offlineTimeout = offlineTimeout;
    }

    private int stopTimeout;

    public int getStopTimeout() {
        return stopTimeout;
    }

    public void setStopTimeout(int stopTimeout) {
        this.stopTimeout = stopTimeout;
    }

    private boolean debt;

    public boolean getDebt() {
        return debt;
    }

    @QueryIgnore
    public void setDebt(boolean debt) {
        this.debt = debt;
    }

    private boolean addUnits;

    public boolean getAddUnits() {
        return addUnits;
    }

    public void setAddUnits(boolean addUnits) {
        this.addUnits = addUnits;
    }

    private boolean support;

    public boolean getSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }
    
    private String pushId;

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    private Date lastLogin;

    public void setLastLogin(Date lastLogin){ this.lastLogin = lastLogin; }

    public Date getLastLogin(){ return lastLogin; }
}
