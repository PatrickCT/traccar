/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tempuri;

/**
 *
 * @author K
 */
public class ServiceLocator extends org.apache.axis.client.Service implements org.tempuri.Service {

    public ServiceLocator() {
    }

    public 
        ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_IRCService
    private java.lang.String BasicHttpBinding_IRCService_address = "http://gps.rcontrol.com.mx/Tracking/wcf/RCService.svc";

    public java.lang.String getBasicHttpBinding_IRCServiceAddress() {
        return BasicHttpBinding_IRCService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_IRCServiceWSDDServiceName = "BasicHttpBinding_IRCService";

    public java.lang.String getBasicHttpBinding_IRCServiceWSDDServiceName() {
        return BasicHttpBinding_IRCServiceWSDDServiceName;
    }

    public void setBasicHttpBinding_IRCServiceWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_IRCServiceWSDDServiceName = name;
    }

    public org.tempuri.IRCService getBasicHttpBinding_IRCService() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_IRCService_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_IRCService(endpoint);
    }

    public org.tempuri.IRCService getBasicHttpBinding_IRCService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.BasicHttpBinding_IRCServiceStub _stub = new org.tempuri.BasicHttpBinding_IRCServiceStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IRCServiceWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_IRCServiceEndpointAddress(java.lang.String address) {
        BasicHttpBinding_IRCService_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has
     * no port for the given interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.IRCService.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.BasicHttpBinding_IRCServiceStub _stub = new org.tempuri.BasicHttpBinding_IRCServiceStub(new java.net.URL(BasicHttpBinding_IRCService_address), this);
                _stub.setPortName(getBasicHttpBinding_IRCServiceWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation. If this service has
     * no port for the given interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BasicHttpBinding_IRCService".equals(inputPortName)) {
            return getBasicHttpBinding_IRCService();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "RCService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_IRCService"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("BasicHttpBinding_IRCService".equals(portName)) {
            setBasicHttpBinding_IRCServiceEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
