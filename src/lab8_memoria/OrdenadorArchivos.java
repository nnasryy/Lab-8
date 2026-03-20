package lab8_memoria;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


public class OrdenadorArchivos {

    private SortCriteria criterio;

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
        if (lista == null || lista.getTamanio() <= 1) {
            return lista != null ? lista.aLista() : new java.util.ArrayList<>();
        }
        mergeSort(lista);
        return lista.aLista();
    }


    public void bubbleSort(ListaEnlazadaArchivos lista) {
        if (lista == null || lista.getTamanio() <= 1) return;

        int n = lista.getTamanio();
        boolean intercambio;

        for (int i = 0; i < n - 1; i++) {
            intercambio = false;
            NodoArchivo actual = lista.getCabeza();

            for (int j = 0; j < n - i - 1; j++) {
                NodoArchivo siguiente = actual.getSiguiente();

                if (comparar(actual.getArchivo(), siguiente.getArchivo()) > 0) {
                    File temp = actual.getArchivo();
                    actual.setArchivo(siguiente.getArchivo());
                    siguiente.setArchivo(temp);
                    intercambio = true;
                }
                actual = actual.getSiguiente();
            }

            if (!intercambio) break;
        }
    }


    public void mergeSort(ListaEnlazadaArchivos lista) {
        if (lista == null || lista.getTamanio() <= 1) return;

        NodoArchivo nuevaCabeza = mergeSortRecursivo(lista.getCabeza());
        lista.setCabeza(nuevaCabeza);
    }


    private NodoArchivo mergeSortRecursivo(NodoArchivo cabeza) {
        if (cabeza == null || cabeza.getSiguiente() == null) {
            return cabeza;
        }

        NodoArchivo mitad = obtenerMitad(cabeza);
        NodoArchivo segundaMitad = mitad.getSiguiente();
        mitad.setSiguiente(null);

        NodoArchivo izquierda = mergeSortRecursivo(cabeza);
        NodoArchivo derecha = mergeSortRecursivo(segundaMitad);

        return mezclar(izquierda, derecha);
    }


    private NodoArchivo obtenerMitad(NodoArchivo cabeza) {
        if (cabeza == null) return null;

        NodoArchivo lento = cabeza;
        NodoArchivo rapido = cabeza.getSiguiente();

        while (rapido != null && rapido.getSiguiente() != null) {
            lento = lento.getSiguiente();
            rapido = rapido.getSiguiente().getSiguiente();
        }
        return lento;
    }


    private NodoArchivo mezclar(NodoArchivo izquierda, NodoArchivo derecha) {
        NodoArchivo centinela = new NodoArchivo(null);
        NodoArchivo actual = centinela;

        while (izquierda != null && derecha != null) {
            if (comparar(izquierda.getArchivo(), derecha.getArchivo()) <= 0) {
                actual.setSiguiente(izquierda);
                izquierda = izquierda.getSiguiente();
            } else {
                actual.setSiguiente(derecha);
                derecha = derecha.getSiguiente();
            }
            actual = actual.getSiguiente();
        }

        actual.setSiguiente(izquierda != null ? izquierda : derecha);

        return centinela.getSiguiente();
    }

    private int comparar(File f1, File f2) {
        if (f1 == null && f2 == null) return 0;
        if (f1 == null) return 1;
        if (f2 == null) return -1;

        if (f1.isDirectory() && !f2.isDirectory()) return -1;
        if (!f1.isDirectory() && f2.isDirectory()) return 1;

        switch (criterio) {
            case DATE:
                return Long.compare(f2.lastModified(), f1.lastModified());

            case SIZE:
                return Long.compare(f2.length(), f1.length());

            case TYPE:
                String ext1 = obtenerExtension(f1);
                String ext2 = obtenerExtension(f2);
                int cmpExt = ext1.compareToIgnoreCase(ext2);
                if (cmpExt != 0) return cmpExt;
                return f1.getName().compareToIgnoreCase(f2.getName());

            case NAME:
            default:
                return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }


    private String obtenerExtension(File archivo) {
        if (archivo.isDirectory()) return "";
        String nombre = archivo.getName();
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombre.length() - 1) {
            return nombre.substring(ultimoPunto).toLowerCase();
        }
        return "";
    }

    public int organizar(File directorio) {
        if (directorio == null || !directorio.isDirectory()) return 0;

        File[] contenidos = directorio.listFiles();
        if (contenidos == null || contenidos.length == 0) return 0;

        int movidos = 0;

        for (File archivo : contenidos) {
            if (archivo.isDirectory()) continue;

            String carpetaDestino = clasificarArchivo(archivo);
            if (carpetaDestino == null) continue;

            File destDir = new File(directorio, carpetaDestino);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            File destino = new File(destDir, archivo.getName());

            try {
                Files.move(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                movidos++;
                System.out.println("Organizado: " + archivo.getName() + " → " + carpetaDestino);
            } catch (IOException e) {
                System.err.println("Error al mover " + archivo.getName() + ": " + e.getMessage());
            }
        }

        return movidos;
    }


    public String clasificarArchivo(File archivo) {
        if (archivo == null || archivo.isDirectory()) return null;

        String ext = obtenerExtension(archivo);

        switch (ext) {
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".gif":
            case ".bmp":
            case ".webp":
                return "Imagenes";

            case ".pdf":
            case ".docx":
            case ".doc":
            case ".txt":
            case ".odt":
            case ".xlsx":
            case ".pptx":
                return "Documentos";

            case ".mp3":
            case ".wav":
            case ".ogg":
            case ".flac":
            case ".aac":
                return "Musica";

            default:
                return "Otros";
        }
    }
}
