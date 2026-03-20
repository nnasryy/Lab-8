/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exceptions;

/**
 *
 * @author nasry
 */
public class OperacionNoPermitidaException extends Exception {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }

    public OperacionNoPermitidaException(String mensaje, Throwable causa){
    super(mensaje, causa);
    }
    
    
}
