/**
 * ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.unisolutions;

public interface ServiceSoap extends java.rmi.Remote {

    /**
     * Autenticacion necesaria para la operacion por parte de los prestadores
     */
    boolean login(java.lang.String systemUser, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * Cierre de sesion explicito por parte del prestador
     */
    boolean logout() throws java.rmi.RemoteException;

    /**
     * Inserta un evento, reconoce el prestador por medio del login previo
     */
    int insertarEvento(java.lang.String dominio, java.lang.String nroSerie, java.lang.String codigo, double latitud, double longitud, double altitud, double velocidad, double rumbo, java.util.Calendar fechaHoraEvento, java.util.Calendar fechaHoraRecepcion, java.lang.String crudo) throws java.rmi.RemoteException;

    /**
     * Loguea e inserta multiples eventos en modo batch
     */
    int[] loginYInsertarEventos(java.lang.String systemUser, java.lang.String password, com.unisolutions.PEvento[] eventos) throws java.rmi.RemoteException;

    /**
     * Loguea e inserta un evento
     */
    int loginYInsertarEvento(java.lang.String systemUser, java.lang.String password, java.lang.String dominio, java.lang.String nroSerie, java.lang.String codigo, double latitud, double longitud, double altitud, double velocidad, java.util.Calendar fechaHoraEvento, java.util.Calendar fechaHoraRecepcion) throws java.rmi.RemoteException;

    /**
     * Loguea e inserta un evento indicando ademas de incorporar los sensores
     */
    int loginYInsertarEvento2(java.lang.String systemUser, java.lang.String password, java.lang.String dominio, java.lang.String nroSerie, java.lang.String codigo, double latitud, double longitud, double altitud, double velocidad, java.util.Calendar fechaHoraEvento, java.util.Calendar fechaHoraRecepcion, boolean valido, com.unisolutions.PSensor[] sensores) throws java.rmi.RemoteException;

    boolean inSession() throws java.rmi.RemoteException;

    /**
     * Retorna el primer comando de la lista
     */
    com.unisolutions.Comando obtenerComando() throws java.rmi.RemoteException;

    /**
     * Indica que el comando ya fue procesado
     */
    boolean comandoProcesado(int NUMERO, boolean correcto) throws java.rmi.RemoteException;

    /**
     * Metodo para informar el estado del comando, retorna verdadero su fue
     * insertado correctamente
     */
    boolean estadoComando(int NUMERO, java.lang.String MSG) throws java.rmi.RemoteException;

    /**
     * Metodo para el cambio de clave, retorna verdadero si la clave es
     * realmente cambiada.
     */
    boolean cambiarClave(java.lang.String antiguaClave, java.lang.String nuevaClave) throws java.rmi.RemoteException;

    /**
     * Loguea cambia la clave, retorna verdadero si la clave es realmente
     * cambiada.
     */
    boolean loginYCambiarClave(java.lang.String systemUser, java.lang.String password, java.lang.String nuevaClave) throws java.rmi.RemoteException;

    /**
     * Realiza el login y retorna el primer comando de la lista
     */
    com.unisolutions.Comando loginYObtenerComando(java.lang.String systemUser, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * Realiza el login y finaliza un comando
     */
    boolean loginYComandoProcesado(java.lang.String systemUser, java.lang.String password, int NUMERO, boolean correcto) throws java.rmi.RemoteException;

    /**
     * Realiza el login y actualiza el estado de un comando en proceso
     */
    boolean loginYEstadoComando(java.lang.String systemUser, java.lang.String password, int NUMERO, java.lang.String MSG) throws java.rmi.RemoteException;

    /**
     * Realiza el login y retorna el primer comando de la lista
     */
    com.unisolutions.Vehiculo[] consultarVehiculosEnViaje(java.lang.String systemUser, java.lang.String password) throws java.rmi.RemoteException;
}
