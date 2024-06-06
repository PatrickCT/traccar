/**
 * Comando.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.unisolutions;

public class Comando implements java.io.Serializable {

    private java.lang.String CMD;

    private java.lang.String UNIDAD;

    private java.lang.String EQUIPO;

    private int NUMERO;

    public Comando() {
    }

    public Comando(
            java.lang.String CMD,
            java.lang.String UNIDAD,
            java.lang.String EQUIPO,
            int NUMERO) {
        this.CMD = CMD;
        this.UNIDAD = UNIDAD;
        this.EQUIPO = EQUIPO;
        this.NUMERO = NUMERO;
    }

    /**
     * Gets the CMD value for this Comando.
     *
     * @return CMD
     */
    public java.lang.String getCMD() {
        return CMD;
    }

    /**
     * Sets the CMD value for this Comando.
     *
     * @param CMD
     */
    public void setCMD(java.lang.String CMD) {
        this.CMD = CMD;
    }

    /**
     * Gets the UNIDAD value for this Comando.
     *
     * @return UNIDAD
     */
    public java.lang.String getUNIDAD() {
        return UNIDAD;
    }

    /**
     * Sets the UNIDAD value for this Comando.
     *
     * @param UNIDAD
     */
    public void setUNIDAD(java.lang.String UNIDAD) {
        this.UNIDAD = UNIDAD;
    }

    /**
     * Gets the EQUIPO value for this Comando.
     *
     * @return EQUIPO
     */
    public java.lang.String getEQUIPO() {
        return EQUIPO;
    }

    /**
     * Sets the EQUIPO value for this Comando.
     *
     * @param EQUIPO
     */
    public void setEQUIPO(java.lang.String EQUIPO) {
        this.EQUIPO = EQUIPO;
    }

    /**
     * Gets the NUMERO value for this Comando.
     *
     * @return NUMERO
     */
    public int getNUMERO() {
        return NUMERO;
    }

    /**
     * Sets the NUMERO value for this Comando.
     *
     * @param NUMERO
     */
    public void setNUMERO(int NUMERO) {
        this.NUMERO = NUMERO;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Comando)) {
            return false;
        }
        Comando other = (Comando) obj;
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
        _equals = true
                && ((this.CMD == null && other.getCMD() == null)
                || (this.CMD != null
                && this.CMD.equals(other.getCMD())))
                && ((this.UNIDAD == null && other.getUNIDAD() == null)
                || (this.UNIDAD != null
                && this.UNIDAD.equals(other.getUNIDAD())))
                && ((this.EQUIPO == null && other.getEQUIPO() == null)
                || (this.EQUIPO != null
                && this.EQUIPO.equals(other.getEQUIPO())))
                && this.NUMERO == other.getNUMERO();
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
        if (getCMD() != null) {
            _hashCode += getCMD().hashCode();
        }
        if (getUNIDAD() != null) {
            _hashCode += getUNIDAD().hashCode();
        }
        if (getEQUIPO() != null) {
            _hashCode += getEQUIPO().hashCode();
        }
        _hashCode += getNUMERO();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc
            = new org.apache.axis.description.TypeDesc(Comando.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Comando"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CMD");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "CMD"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("UNIDAD");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "UNIDAD"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EQUIPO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "EQUIPO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NUMERO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "NUMERO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
