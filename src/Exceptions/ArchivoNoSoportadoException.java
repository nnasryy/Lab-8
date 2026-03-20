/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exceptions;

/**
 *
 * @author nasry
 */
public class ArchivoNoSoportadoException extends Exception{

    private final String nombreArchivo;

    public ArchivoNoSoportadoException(String nombreArchivo) {
        super("Extensión de archivo no soportada por el sistema: " + nombreArchivo);
        this.nombreArchivo = nombreArchivo;
    }

    public ArchivoNoSoportadoException(String nombreArchivo, String mensaje) {
        super(mensaje);
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

}
