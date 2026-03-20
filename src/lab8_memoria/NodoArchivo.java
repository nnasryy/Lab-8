/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

/**
 *
 * @author jerem
 */
import java.io.File;

/**
 * Clase NodoArchivo - Nodo para la lista enlazada personalizada
 * Miembro 1: Estructura de datos base
 */
import java.io.File;

/**
 * Clase NodoArchivo - Nodo de la lista enlazada
 * Miembro 1
 */
public class NodoArchivo {

    File archivo;
    NodoArchivo siguiente;

    public NodoArchivo(File archivo) {
        if (archivo == null) {
            throw new IllegalArgumentException("El archivo no puede ser null");
        }
        this.archivo = archivo;
        this.siguiente = null;
    }

    public File getArchivo() {
        return archivo;
    }

    public NodoArchivo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoArchivo siguiente) {
        this.siguiente = siguiente;
    }
}