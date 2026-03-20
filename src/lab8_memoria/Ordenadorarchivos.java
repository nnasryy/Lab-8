/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.util.List;

/**
 *
 * @author jerem
 */
public class OrdenadorArchivos {

    private SortCriteria criterio;

    public enum SortCriteria {
        NAME, DATE, SIZE, TYPE;
    }

    public OrdenadorArchivos(SortCriteria criterio) {
        this.criterio = criterio;
    }

    public void setCriterio(SortCriteria criterio) {
        this.criterio = criterio;
    }

    public SortCriteria getCriterio() {
        return criterio;
    }

    public List<File> ordenar(ListaEnlazadaArchivos lista) {
        if (lista == null || lista.getTamanio() >= 1) {

        }

    }

}
