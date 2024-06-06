/**
 * ServicePositionsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package org.tempuri2;

public class ServicePositionsLocator extends org.apache.axis.client.Service implements org.tempuri2.ServicePositions {

    public ServicePositionsLocator() {
    }

    public ServicePositionsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServicePositionsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_IServicePositions
    private java.lang.String BasicHttpBinding_IServicePositions_address = "http://ws4.altotrack.com/WSPosiciones_Chep/WSPosiciones_Chep.svc";

    public java.lang.String getBasicHttpBinding_IServicePositionsAddress() {
        return BasicHttpBinding_IServicePositions_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_IServicePositionsWSDDServiceName = "BasicHttpBinding_IServicePositions";

    public java.lang.String getBasicHttpBinding_IServicePositionsWSDDServiceName() {
        return BasicHttpBinding_IServicePositionsWSDDServiceName;
    }

    public void setBasicHttpBinding_IServicePositionsWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_IServicePositionsWSDDServiceName = name;
    }

    public org.tempuri2.IServicePositions getBasicHttpBinding_IServicePositions() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_IServicePositions_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_IServicePositions(endpoint);
    }

    public org.tempuri2.IServicePositions getBasicHttpBinding_IServicePositions(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri2.BasicHttpBinding_IServicePositionsStub _stub = new org.tempuri2.BasicHttpBinding_IServicePositionsStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IServicePositionsWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_IServicePositionsEndpointAddress(java.lang.String address) {
        BasicHttpBinding_IServicePositions_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has
     * no port for the given interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri2.IServicePositions.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri2.BasicHttpBinding_IServicePositionsStub _stub = new org.tempuri2.BasicHttpBinding_IServicePositionsStub(new java.net.URL(BasicHttpBinding_IServicePositions_address), this);
                _stub.setPortName(getBasicHttpBinding_IServicePositionsWSDDServiceName());
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
        if ("BasicHttpBinding_IServicePositions".equals(inputPortName)) {
            return getBasicHttpBinding_IServicePositions();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "ServicePositions");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_IServicePositions"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("BasicHttpBinding_IServicePositions".equals(portName)) {
            setBasicHttpBinding_IServicePositionsEndpointAddress(address);
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
