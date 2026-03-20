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
import java.util.ArrayList;
import java.util.List;


public class ListaEnlazadaArchivos {

    private NodoArchivo cabeza;
    private int tamanio;

    public ListaEnlazadaArchivos() {
        cabeza = null;
        tamanio = 0;
    }

    public void agregar(File archivo) {
        try {
            NodoArchivo nuevo = new NodoArchivo(archivo);
            if (cabeza == null) {
                cabeza = nuevo;
            } else {
                NodoArchivo actual = cabeza;
                while (actual.getSiguiente() != null) {
                    actual = actual.getSiguiente();
                }
                actual.setSiguiente(nuevo);
            }
            tamanio++;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al agregar nodo: " + e.getMessage());
        }
    }

    public void limpiar() {
        cabeza = null;
        tamanio = 0;
    }

    public int getTamanio() {
        return tamanio;
    }

    public List<File> aLista() {
        List<File> lista = new ArrayList<>();
        try {
            NodoArchivo actual = cabeza;
            while (actual != null) {
                lista.add(actual.getArchivo());
                actual = actual.getSiguiente();
            }
        } catch (Exception e) {
            System.err.println("Error al convertir lista: " + e.getMessage());
        }
        return lista;
    }

    public void ordenarPorNombreBubble() {
        try {
            if (cabeza == null || cabeza.getSiguiente() == null) return;
            boolean intercambio;
            do {
                intercambio = false;
                NodoArchivo actual = cabeza;
                while (actual.getSiguiente() != null) {
                    String n1 = actual.getArchivo().getName().toLowerCase();
                    String n2 = actual.getSiguiente().getArchivo().getName().toLowerCase();
                    if (n1.compareTo(n2) > 0) {
                        File temp = actual.getArchivo();
                        actual.archivo = actual.getSiguiente().getArchivo();
                        actual.getSiguiente().archivo = temp;
                        intercambio = true;
                    }
                    actual = actual.getSiguiente();
                }
            } while (intercambio);
        } catch (Exception e) {
            System.err.println("Error en Bubble Sort por nombre: " + e.getMessage());
        }
    }

    public void ordenarPorTamanioBubble() {
        try {
            if (cabeza == null || cabeza.getSiguiente() == null) return;
            boolean intercambio;
            do {
                intercambio = false;
                NodoArchivo actual = cabeza;
                while (actual.getSiguiente() != null) {
                    if (actual.getArchivo().length() > actual.getSiguiente().getArchivo().length()) {
                        File temp = actual.getArchivo();
                        actual.archivo = actual.getSiguiente().getArchivo();
                        actual.getSiguiente().archivo = temp;
                        intercambio = true;
                    }
                    actual = actual.getSiguiente();
                }
            } while (intercambio);
        } catch (Exception e) {
            System.err.println("Error en Bubble Sort por tamaño: " + e.getMessage());
        }
    }

    public void ordenarPorFechaBubble() {
        try {
            if (cabeza == null || cabeza.getSiguiente() == null) return;
            boolean intercambio;
            do {
                intercambio = false;
                NodoArchivo actual = cabeza;
                while (actual.getSiguiente() != null) {
                    if (actual.getArchivo().lastModified() > actual.getSiguiente().getArchivo().lastModified()) {
                        File temp = actual.getArchivo();
                        actual.archivo = actual.getSiguiente().getArchivo();
                        actual.getSiguiente().archivo = temp;
                        intercambio = true;
                    }
                    actual = actual.getSiguiente();
                }
            } while (intercambio);
        } catch (Exception e) {
            System.err.println("Error en Bubble Sort por fecha: " + e.getMessage());
        }
    }

    public void ordenarPorTipoBubble() {
        try {
            if (cabeza == null || cabeza.getSiguiente() == null) return;
            boolean intercambio;
            do {
                intercambio = false;
                NodoArchivo actual = cabeza;
                while (actual.getSiguiente() != null) {
                    String e1 = obtenerExtension(actual.getArchivo());
                    String e2 = obtenerExtension(actual.getSiguiente().getArchivo());
                    if (e1.compareTo(e2) > 0) {
                        File temp = actual.getArchivo();
                        actual.archivo = actual.getSiguiente().getArchivo();
                        actual.getSiguiente().archivo = temp;
                        intercambio = true;
                    }
                    actual = actual.getSiguiente();
                }
            } while (intercambio);
        } catch (Exception e) {
            System.err.println("Error en Bubble Sort por tipo: " + e.getMessage());
        }
    }

    public void ordenarMergeSortPorNombre() {
        try {
            cabeza = mergeSort(cabeza);
            recalcularTamanio();
        } catch (Exception e) {
            System.err.println("Error en Merge Sort: " + e.getMessage());
        }
    }

    private NodoArchivo mergeSort(NodoArchivo inicio) {
        if (inicio == null || inicio.getSiguiente() == null) return inicio;
        NodoArchivo mitad = obtenerMitad(inicio);
        NodoArchivo segunda = mitad.getSiguiente();
        mitad.setSiguiente(null);
        NodoArchivo izq = mergeSort(inicio);
        NodoArchivo der = mergeSort(segunda);
        return mezclar(izq, der);
    }

    private NodoArchivo obtenerMitad(NodoArchivo inicio) {
        NodoArchivo lento = inicio;
        NodoArchivo rapido = inicio.getSiguiente();
        while (rapido != null && rapido.getSiguiente() != null) {
            lento = lento.getSiguiente();
            rapido = rapido.getSiguiente().getSiguiente();
        }
        return lento;
    }

    private NodoArchivo mezclar(NodoArchivo izq, NodoArchivo der) {
        if (izq == null) return der;
        if (der == null) return izq;
        NodoArchivo resultado;
        if (izq.getArchivo().getName().toLowerCase()
                .compareTo(der.getArchivo().getName().toLowerCase()) <= 0) {
            resultado = izq;
            resultado.setSiguiente(mezclar(izq.getSiguiente(), der));
        } else {
            resultado = der;
            resultado.setSiguiente(mezclar(izq, der.getSiguiente()));
        }
        return resultado;
    }

    public void recalcularTamanio() {
        tamanio = 0;
        NodoArchivo actual = cabeza;
        while (actual != null) {
            tamanio++;
            actual = actual.getSiguiente();
        }
    }

    private String obtenerExtension(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        if (punto >= 0) return nombre.substring(punto + 1).toLowerCase();
        return archivo.isDirectory() ? "carpeta" : "";
    }
}
