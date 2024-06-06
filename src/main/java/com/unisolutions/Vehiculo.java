/**
 * Vehiculo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.unisolutions;

public class Vehiculo implements java.io.Serializable {

    private java.lang.String dominio;

    private java.lang.String nroSerie;

    private java.util.Calendar fechaUltimoReporte;

    private java.util.Calendar fechaRegistracion;

    public Vehiculo() {
    }

    public Vehiculo(
            java.lang.String dominio,
            java.lang.String nroSerie,
            java.util.Calendar fechaUltimoReporte,
            java.util.Calendar fechaRegistracion) {
        this.dominio = dominio;
        this.nroSerie = nroSerie;
        this.fechaUltimoReporte = fechaUltimoReporte;
        this.fechaRegistracion = fechaRegistracion;
    }

    /**
     * Gets the dominio value for this Vehiculo.
     *
     * @return dominio
     */
    public java.lang.String getDominio() {
        return dominio;
    }

    /**
     * Sets the dominio value for this Vehiculo.
     *
     * @param dominio
     */
    public void setDominio(java.lang.String dominio) {
        this.dominio = dominio;
    }

    /**
     * Gets the nroSerie value for this Vehiculo.
     *
     * @return nroSerie
     */
    public java.lang.String getNroSerie() {
        return nroSerie;
    }

    /**
     * Sets the nroSerie value for this Vehiculo.
     *
     * @param nroSerie
     */
    public void setNroSerie(java.lang.String nroSerie) {
        this.nroSerie = nroSerie;
    }

    /**
     * Gets the fechaUltimoReporte value for this Vehiculo.
     *
     * @return fechaUltimoReporte
     */
    public java.util.Calendar getFechaUltimoReporte() {
        return fechaUltimoReporte;
    }

    /**
     * Sets the fechaUltimoReporte value for this Vehiculo.
     *
     * @param fechaUltimoReporte
     */
    public void setFechaUltimoReporte(java.util.Calendar fechaUltimoReporte) {
        this.fechaUltimoReporte = fechaUltimoReporte;
    }

    /**
     * Gets the fechaRegistracion value for this Vehiculo.
     *
     * @return fechaRegistracion
     */
    public java.util.Calendar getFechaRegistracion() {
        return fechaRegistracion;
    }

    /**
     * Sets the fechaRegistracion value for this Vehiculo.
     *
     * @param fechaRegistracion
     */
    public void setFechaRegistracion(java.util.Calendar fechaRegistracion) {
        this.fechaRegistracion = fechaRegistracion;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Vehiculo)) {
            return false;
        }
        Vehiculo other = (Vehiculo) obj;
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
                && ((this.dominio == null && other.getDominio() == null)
                || (this.dominio != null
                && this.dominio.equals(other.getDominio())))
                && ((this.nroSerie == null && other.getNroSerie() == null)
                || (this.nroSerie != null
                && this.nroSerie.equals(other.getNroSerie())))
                && ((this.fechaUltimoReporte == null && other.getFechaUltimoReporte() == null)
                || (this.fechaUltimoReporte != null
                && this.fechaUltimoReporte.equals(other.getFechaUltimoReporte())))
                && ((this.fechaRegistracion == null && other.getFechaRegistracion() == null)
                || (this.fechaRegistracion != null
                && this.fechaRegistracion.equals(other.getFechaRegistracion())));
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
        if (getDominio() != null) {
            _hashCode += getDominio().hashCode();
        }
        if (getNroSerie() != null) {
            _hashCode += getNroSerie().hashCode();
        }
        if (getFechaUltimoReporte() != null) {
            _hashCode += getFechaUltimoReporte().hashCode();
        }
        if (getFechaRegistracion() != null) {
            _hashCode += getFechaRegistracion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc
            = new org.apache.axis.description.TypeDesc(Vehiculo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Vehiculo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dominio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Dominio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroSerie");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "NroSerie"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaUltimoReporte");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "FechaUltimoReporte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaRegistracion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "FechaRegistracion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
