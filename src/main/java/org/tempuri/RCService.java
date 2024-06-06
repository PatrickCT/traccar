/**
 * RCService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface RCService extends javax.xml.rpc.Service {
    java.lang.String getBasicHttpBinding_IRCServiceAddress();

    org.tempuri.IRCService getBasicHttpBinding_IRCService() throws javax.xml.rpc.ServiceException;

    org.tempuri.IRCService getBasicHttpBinding_IRCService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
