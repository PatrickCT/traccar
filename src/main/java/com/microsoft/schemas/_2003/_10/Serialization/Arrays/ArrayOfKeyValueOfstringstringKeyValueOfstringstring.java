/**
 * ArrayOfKeyValueOfstringstringKeyValueOfstringstring.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.microsoft.schemas._2003._10.Serialization.Arrays;

public class ArrayOfKeyValueOfstringstringKeyValueOfstringstring implements java.io.Serializable {

    private String key;

    private String value;

    public ArrayOfKeyValueOfstringstringKeyValueOfstringstring() {
    }

    public ArrayOfKeyValueOfstringstringKeyValueOfstringstring(
            String key,
            String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key value for this
     * ArrayOfKeyValueOfstringstringKeyValueOfstringstring.
     *
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }

    /**
     * Sets the key value for this
     * ArrayOfKeyValueOfstringstringKeyValueOfstringstring.
     *
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }

    /**
     * Gets the value value for this
     * ArrayOfKeyValueOfstringstringKeyValueOfstringstring.
     *
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }

    /**
     * Sets the value value for this
     * ArrayOfKeyValueOfstringstringKeyValueOfstringstring.
     *
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfKeyValueOfstringstringKeyValueOfstringstring)) {
            return false;
        }
        ArrayOfKeyValueOfstringstringKeyValueOfstringstring other = (ArrayOfKeyValueOfstringstringKeyValueOfstringstring) obj;
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
        _equals
                = ((this.key == null && other.getKey() == null)
                || (this.key != null
                && this.key.equals(other.getKey())))
                && ((this.value == null && other.getValue() == null)
                || (this.value != null
                && this.value.equals(other.getValue())));
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
        if (getKey() != null) {
            _hashCode += getKey().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc
            = new org.apache.axis.description.TypeDesc(ArrayOfKeyValueOfstringstringKeyValueOfstringstring.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", ">ArrayOfKeyValueOfstringstring>KeyValueOfstringstring"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "Value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

    @Override
    public String toString() {
        return "ArrayOfKeyValueOfstringstringKeyValueOfstringstring{" + "key=" + key + ", value=" + value + '}';
    }


}
