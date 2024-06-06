/**
 * Event.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package org.datacontract.schemas._2004._07.IronTracking;

import java.util.Calendar;

public class Event {

    private String altitude;

    private String asset;

    private String battery;

    private String code;

    private String course;

    private org.datacontract.schemas._2004._07.IronTracking.Customer customer;

    private Calendar date;

    private String direction;

    private String humidity;

    private String ignition;

    private String latitude;

    private String longitude;

    private String odometer;

    private String serialNumber;

    private String shipment;

    private String speed;

    private String temperature;

    private String vehicleType;

    private String vehicleBrand;

    private String vehicleModel;

    public Event() {
    }

    public Event(String msg) {
        System.out.println(msg);
    }

    public Event(
            String altitude,
            String asset,
            String battery,
            String code,
            String course,
            org.datacontract.schemas._2004._07.IronTracking.Customer customer,
            Calendar date,
            String direction,
            String humidity,
            String ignition,
            String latitude,
            String longitude,
            String odometer,
            String serialNumber,
            String shipment,
            String speed,
            String temperature,
            String vehicleType,
            String vehicleBrand,
            String vehicleModel) {
        this.altitude = altitude;
        this.asset = asset;
        this.battery = battery;
        this.code = code;
        this.course = course;
        this.customer = customer;
        this.date = date;
        this.direction = direction;
        this.humidity = humidity;
        this.ignition = ignition;
        this.latitude = latitude;
        this.longitude = longitude;
        this.odometer = odometer;
        this.serialNumber = serialNumber;
        this.shipment = shipment;
        this.speed = speed;
        this.temperature = temperature;
        this.vehicleType = vehicleType;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
    }

    /**
     * Gets the altitude value for this Event.
     *
     * @return altitude
     */
    public String getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude value for this Event.
     *
     * @param altitude
     */
    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    /**
     * Gets the asset value for this Event.
     *
     * @return asset
     */
    public String getAsset() {
        return asset;
    }

    /**
     * Sets the asset value for this Event.
     *
     * @param asset
     */
    public void setAsset(String asset) {
        this.asset = asset;
    }

    /**
     * Gets the battery value for this Event.
     *
     * @return battery
     */
    public String getBattery() {
        return battery;
    }

    /**
     * Sets the battery value for this Event.
     *
     * @param battery
     */
    public void setBattery(String battery) {
        this.battery = battery;
    }

    /**
     * Gets the code value for this Event.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code value for this Event.
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the course value for this Event.
     *
     * @return course
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the course value for this Event.
     *
     * @param course
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Gets the customer value for this Event.
     *
     * @return customer
     */
    public org.datacontract.schemas._2004._07.IronTracking.Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer value for this Event.
     *
     * @param customer
     */
    public void setCustomer(org.datacontract.schemas._2004._07.IronTracking.Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the date value for this Event.
     *
     * @return date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Sets the date value for this Event.
     *
     * @param date
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * Gets the direction value for this Event.
     *
     * @return direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the direction value for this Event.
     *
     * @param direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Gets the humidity value for this Event.
     *
     * @return humidity
     */
    public String getHumidity() {
        return humidity;
    }

    /**
     * Sets the humidity value for this Event.
     *
     * @param humidity
     */
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    /**
     * Gets the ignition value for this Event.
     *
     * @return ignition
     */
    public String getIgnition() {
        return ignition;
    }

    /**
     * Sets the ignition value for this Event.
     *
     * @param ignition
     */
    public void setIgnition(String ignition) {
        this.ignition = ignition;
    }

    /**
     * Gets the latitude value for this Event.
     *
     * @return latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude value for this Event.
     *
     * @param latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude value for this Event.
     *
     * @return longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude value for this Event.
     *
     * @param longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the odometer value for this Event.
     *
     * @return odometer
     */
    public String getOdometer() {
        return odometer;
    }

    /**
     * Sets the odometer value for this Event.
     *
     * @param odometer
     */
    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    /**
     * Gets the serialNumber value for this Event.
     *
     * @return serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serialNumber value for this Event.
     *
     * @param serialNumber
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the shipment value for this Event.
     *
     * @return shipment
     */
    public String getShipment() {
        return shipment;
    }

    /**
     * Sets the shipment value for this Event.
     *
     * @param shipment
     */
    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    /**
     * Gets the speed value for this Event.
     *
     * @return speed
     */
    public String getSpeed() {
        return speed;
    }

    /**
     * Sets the speed value for this Event.
     *
     * @param speed
     */
    public void setSpeed(String speed) {
        this.speed = speed;
    }

    /**
     * Gets the temperature value for this Event.
     *
     * @return temperature
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature value for this Event.
     *
     * @param temperature
     */
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    /**
     * Gets the vehicleType value for this Event.
     *
     * @return vehicleType
     */
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * Sets the vehicleType value for this Event.
     *
     * @param vehicleType
     */
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Gets the vehicleBrand value for this Event.
     *
     * @return vehicleBrand
     */
    public String getVehicleBrand() {
        return vehicleBrand;
    }

    /**
     * Sets the vehicleBrand value for this Event.
     *
     * @param vehicleBrand
     */
    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    /**
     * Gets the vehicleModel value for this Event.
     *
     * @return vehicleModel
     */
    public String getVehicleModel() {
        return vehicleModel;
    }

    /**
     * Sets the vehicleModel value for this Event.
     *
     * @param vehicleModel
     */
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    @Override
    public String toString() {
        return "Event{" + ""
                + "altitude=" + altitude + ", "
                + "asset=" + asset + ", "
                + "battery=" + battery + ", "
                + "code=" + code + ", "
                + "course=" + course + ", "
                + "customer=" + customer + ", "
                + "date=" + date + ", "
                + "direction=" + direction + ", "
                + "humidity=" + humidity + ", "
                + "ignition=" + ignition + ", "
                + "latitude=" + latitude + ", "
                + "longitude=" + longitude + ", "
                + "odometer=" + odometer + ", "
                + "serialNumber=" + serialNumber + ", "
                + "shipment=" + shipment + ", "
                + "speed=" + speed + ", "
                + "temperature=" + temperature + ", "
                + "vehicleType=" + vehicleType + ", "
                + "vehicleBrand=" + vehicleBrand + ", "
                + "vehicleModel=" + vehicleModel + '}';
    }

}
