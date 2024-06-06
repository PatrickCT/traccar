/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tempuri;

/**
 *
 * @author K
 */
public interface Service extends javax.xml.rpc.Service {
    java.lang.String getBasicHttpBinding_IRCServiceAddress();

    org.tempuri.IRCService getBasicHttpBinding_IRCService() throws javax.xml.rpc.ServiceException;

    org.tempuri.IRCService getBasicHttpBinding_IRCService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
