/**
 * IRCService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface IRCService extends java.rmi.Remote {
    org.datacontract.schemas._2004._07.IronTracking.UserTokenResult getUserToken(java.lang.String userId, java.lang.String password) throws java.rmi.RemoteException;
    org.datacontract.schemas._2004._07.IronTracking.AppointResult[] GPSAssetTracking(java.lang.String token, org.datacontract.schemas._2004._07.IronTracking.Event[] events) throws java.rmi.RemoteException;
}
