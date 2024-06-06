/**
 * Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.unisolutions;

public interface Service extends javax.xml.rpc.Service {

    java.lang.String getServiceSoapAddress();

    com.unisolutions.ServiceSoap getServiceSoap() throws javax.xml.rpc.ServiceException;

    com.unisolutions.ServiceSoap getServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;

    java.lang.String getServiceSoap12Address();

    com.unisolutions.ServiceSoap getServiceSoap12() throws javax.xml.rpc.ServiceException;

    com.unisolutions.ServiceSoap getServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
