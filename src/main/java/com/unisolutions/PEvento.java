/**
 * PEvento.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.unisolutions;

public class PEvento implements java.io.Serializable {

    private java.lang.String dominio;

    private java.lang.String nroSerie;

    private java.lang.String codigo;

    private double latitud;

    private double longitud;

    private double altitud;

    private double velocidad;

    private java.util.Calendar fechaHoraEvento;

    private java.util.Calendar fechaHoraRecepcion;

    public PEvento() {
    }

    public PEvento(
            java.lang.String dominio,
            java.lang.String nroSerie,
            java.lang.String codigo,
            double latitud,
            double longitud,
            double altitud,
            double velocidad,
            java.util.Calendar fechaHoraEvento,
            java.util.Calendar fechaHoraRecepcion) {
        this.dominio = dominio;
        this.nroSerie = nroSerie;
        this.codigo = codigo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
        this.velocidad = velocidad;
        this.fechaHoraEvento = fechaHoraEvento;
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    /**
     * Gets the dominio value for this PEvento.
     *
     * @return dominio
     */
    public java.lang.String getDominio() {
        return dominio;
    }

    /**
     * Sets the dominio value for this PEvento.
     *
     * @param dominio
     */
    public void setDominio(java.lang.String dominio) {
        this.dominio = dominio;
    }

    /**
     * Gets the nroSerie value for this PEvento.
     *
     * @return nroSerie
     */
    public java.lang.String getNroSerie() {
        return nroSerie;
    }

    /**
     * Sets the nroSerie value for this PEvento.
     *
     * @param nroSerie
     */
    public void setNroSerie(java.lang.String nroSerie) {
        this.nroSerie = nroSerie;
    }

    /**
     * Gets the codigo value for this PEvento.
     *
     * @return codigo
     */
    public java.lang.String getCodigo() {
        return codigo;
    }

    /**
     * Sets the codigo value for this PEvento.
     *
     * @param codigo
     */
    public void setCodigo(java.lang.String codigo) {
        this.codigo = codigo;
    }

    /**
     * Gets the latitud value for this PEvento.
     *
     * @return latitud
     */
    public double getLatitud() {
        return latitud;
    }

    /**
     * Sets the latitud value for this PEvento.
     *
     * @param latitud
     */
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    /**
     * Gets the longitud value for this PEvento.
     *
     * @return longitud
     */
    public double getLongitud() {
        return longitud;
    }

    /**
     * Sets the longitud value for this PEvento.
     *
     * @param longitud
     */
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    /**
     * Gets the altitud value for this PEvento.
     *
     * @return altitud
     */
    public double getAltitud() {
        return altitud;
    }

    /**
     * Sets the altitud value for this PEvento.
     *
     * @param altitud
     */
    public void setAltitud(double altitud) {
        this.altitud = altitud;
    }

    /**
     * Gets the velocidad value for this PEvento.
     *
     * @return velocidad
     */
    public double getVelocidad() {
        return velocidad;
    }

    /**
     * Sets the velocidad value for this PEvento.
     *
     * @param velocidad
     */
    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    /**
     * Gets the fechaHoraEvento value for this PEvento.
     *
     * @return fechaHoraEvento
     */
    public java.util.Calendar getFechaHoraEvento() {
        return fechaHoraEvento;
    }

    /**
     * Sets the fechaHoraEvento value for this PEvento.
     *
     * @param fechaHoraEvento
     */
    public void setFechaHoraEvento(java.util.Calendar fechaHoraEvento) {
        this.fechaHoraEvento = fechaHoraEvento;
    }

    /**
     * Gets the fechaHoraRecepcion value for this PEvento.
     *
     * @return fechaHoraRecepcion
     */
    public java.util.Calendar getFechaHoraRecepcion() {
        return fechaHoraRecepcion;
    }

    /**
     * Sets the fechaHoraRecepcion value for this PEvento.
     *
     * @param fechaHoraRecepcion
     */
    public void setFechaHoraRecepcion(java.util.Calendar fechaHoraRecepcion) {
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PEvento)) {
            return false;
        }
        PEvento other = (PEvento) obj;
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
                && ((this.codigo == null && other.getCodigo() == null)
                || (this.codigo != null
                && this.codigo.equals(other.getCodigo())))
                && this.latitud == other.getLatitud()
                && this.longitud == other.getLongitud()
                && this.altitud == other.getAltitud()
                && this.velocidad == other.getVelocidad()
                && ((this.fechaHoraEvento == null && other.getFechaHoraEvento() == null)
                || (this.fechaHoraEvento != null
                && this.fechaHoraEvento.equals(other.getFechaHoraEvento())))
                && ((this.fechaHoraRecepcion == null && other.getFechaHoraRecepcion() == null)
                || (this.fechaHoraRecepcion != null
                && this.fechaHoraRecepcion.equals(other.getFechaHoraRecepcion())));
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
        if (getCodigo() != null) {
            _hashCode += getCodigo().hashCode();
        }
        _hashCode += new Double(getLatitud()).hashCode();
        _hashCode += new Double(getLongitud()).hashCode();
        _hashCode += new Double(getAltitud()).hashCode();
        _hashCode += new Double(getVelocidad()).hashCode();
        if (getFechaHoraEvento() != null) {
            _hashCode += getFechaHoraEvento().hashCode();
        }
        if (getFechaHoraRecepcion() != null) {
            _hashCode += getFechaHoraRecepcion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc
            = new org.apache.axis.description.TypeDesc(PEvento.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "pEvento"));
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
        elemField.setFieldName("codigo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Codigo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latitud");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Latitud"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longitud");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Longitud"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altitud");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Altitud"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("velocidad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "Velocidad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaHoraEvento");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "FechaHoraEvento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaHoraRecepcion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://unisolutions.com.ar/", "FechaHoraRecepcion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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

    @Override
    public String toString() {
        return "PEvento{" + "dominio=" + dominio + ", nroSerie=" + nroSerie + ", codigo=" + codigo + ", latitud=" + latitud + ", longitud=" + longitud + ", altitud=" + altitud + ", velocidad=" + velocidad + ", fechaHoraEvento=" + fechaHoraEvento + ", fechaHoraRecepcion=" + fechaHoraRecepcion + ", __equalsCalc=" + __equalsCalc + ", __hashCodeCalc=" + __hashCodeCalc + '}';
    }
}
