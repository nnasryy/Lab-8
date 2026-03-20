package lab8_memoria;

import java.io.File;

public class NodoArchivo {

    private File archivo;
    private NodoArchivo siguiente;


    public NodoArchivo(File archivo) {
        this.archivo = archivo;
        this.siguiente = null;
    }


    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public NodoArchivo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoArchivo siguiente) {
        this.siguiente = siguiente;
    }

    @Override
        public String toString() {
        return archivo != null ? archivo.getName() : "null";
    }
}
