/**
 * UserTokenResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package org.datacontract.schemas._2004._07.IronTracking;

public class UserTokenResult implements java.io.Serializable {

    private com.microsoft.schemas._2003._10.Serialization.Arrays.ArrayOfKeyValueOfstringstringKeyValueOfstringstring[] exception;

    private java.lang.String token;

    public UserTokenResult() {
    }

    public UserTokenResult(
            com.microsoft.schemas._2003._10.Serialization.Arrays.ArrayOfKeyValueOfstringstringKeyValueOfstringstring[] exception,
            java.lang.String token) {
        this.exception = exception;
        this.token = token;
    }

    /**
     * Gets the exception value for this UserTokenResult.
     *
     * @return exception
     */
    public com.microsoft.schemas._2003._10.Serialization.Arrays.ArrayOfKeyValueOfstringstringKeyValueOfstringstring[] getException() {
        return exception;
    }

    /**
     * Sets the exception value for this UserTokenResult.
     *
     * @param exception
     */
    public void setException(com.microsoft.schemas._2003._10.Serialization.Arrays.ArrayOfKeyValueOfstringstringKeyValueOfstringstring[] exception) {
        this.exception = exception;
    }

    /**
     * Gets the token value for this UserTokenResult.
     *
     * @return token
     */
    public java.lang.String getToken() {
        return token;
    }

    /**
     * Sets the token value for this UserTokenResult.
     *
     * @param token
     */
    public void setToken(java.lang.String token) {
        this.token = token;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserTokenResult)) {
            return false;
        }
        UserTokenResult other = (UserTokenResult) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = ((this.exception == null && other.getException() == null)
                || (this.exception != null
                && java.util.Arrays.equals(this.exception, other.getException())))
                && ((this.token == null && other.getToken() == null)
                || (this.token != null
                && this.token.equals(other.getToken())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getException() != null) {
            for (int i = 0;
                    i < java.lang.reflect.Array.getLength(getException());
                    i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getException(), i);
                if (obj != null
                        && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc
            = new org.apache.axis.description.TypeDesc(UserTokenResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/IronTracking", "UserTokenResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exception");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/IronTracking", "exception"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", ">ArrayOfKeyValueOfstringstring>KeyValueOfstringstring"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "KeyValueOfstringstring"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("token");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/IronTracking", "token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(
                _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(
                _javaType, _xmlType, typeDesc);
    }

}
