/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jerem
 */
public class ListaEnlazadaArchivos {
    
    private NodoArchivo cabeza;
    private int tamanio;
    
    public ListaEnlazadaArchivos(){
        this.cabeza = null;
        this.tamanio = 0;
    }
    
    public void agregar(File archivo){
        NodoArchivo nuevo = new NodoArchivo(archivo);
        if(cabeza == null){
            cabeza = nuevo;
        } else{
            NodoArchivo actual = cabeza;
            while(actual.getSiguiente()!= null){
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }
    
    public void agregarAlFrente(File archivo){
        NodoArchivo nuevo = new NodoArchivo(archivo);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamanio++;
    }
    
    public File obtener(int indice){
        if(indice<0 || indice>=tamanio){
            return null;
        }
        NodoArchivo actual = cabeza;
        for(int i = 0; i < indice; i++){
            actual = actual.getSiguiente();
        }
        return actual.getArchivo();
    }
    
    public void establecer(int indice, File archivo){
        if(indice<0 || indice>=tamanio){
            return;
        }
        NodoArchivo actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        actual.setArchivo(archivo);
    }
    
    public void limpiar(){
        cabeza = null;
        tamanio = 0;
    }
    
    public boolean estVacia(){
        return tamanio == 0;
    }
    
    public int getTamanio(){
        return tamanio;
    }
    
    public NodoArchivo getCabeza(){
        return cabeza;
    }
    
    public void setCabeza(NodoArchivo cabeza){
        this.cabeza = cabeza;
        int count = 0;
        NodoArchivo actual = cabeza;
        while(actual!=null){
            count++;
            actual = actual.getSiguiente();
        }
        this.tamanio = count;
    }
    
    public List<File> aLista(){
        List<File> resultado = new ArrayList<>();
        NodoArchivo actual = cabeza;
        while(actual!=null){
            resultado.add(actual.getArchivo());
            actual = actual.getSiguiente();
        }
        return resultado;
    }
    
    public void desdeLista(List<File> archivos){
        limpiar();
        for(File f : archivos){
            agregar(f);
        }
    }
    
    public void imprimir(){
        NodoArchivo actual = cabeza;
        System.out.print("Lista: [");
        while(actual!=null){
            System.out.print(actual.getArchivo().getName());
            if(actual.getSiguiente()!=null){
                System.out.println(" -> ");
                actual = actual.getSiguiente();
            }
            System.out.println("]");
        }
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder("ListaEnlazada(").append(tamanio).append(") [");
        NodoArchivo actual = cabeza;
        while(actual!=null){
            sb.append(actual.getArchivo().getName());
            if(actual.getSiguiente()!=null){
                sb.append(", ");
                actual = actual.getSiguiente();
            }
        }
        return sb.append("]").toString();
    }
}
