/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

/**
 *
 * @author jerem
 */
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase GestorArchivos - Operaciones sobre archivos
 * Miembro 3
 */
public class GestorArchivos {

    private List<File> portapapeles;

    public GestorArchivos() {
        portapapeles = new ArrayList<>();
    }

    // CREAR CARPETA
    public boolean crearCarpeta(File carpetaPadre, String nombre) {
        try {
            if (carpetaPadre == null || !carpetaPadre.isDirectory()) {
                throw new IllegalArgumentException("La carpeta padre no es válida.");
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            }
            File nueva = new File(carpetaPadre, nombre.trim());
            if (nueva.exists()) {
                throw new FileAlreadyExistsException(nueva.getAbsolutePath(), null, "Ya existe una carpeta con ese nombre.");
            }
            return nueva.mkdir();
        } catch (FileAlreadyExistsException e) {
            System.err.println("Carpeta ya existe: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Argumento inválido: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al crear carpeta: " + e.getMessage());
            return false;
        }
    }

    // RENOMBRAR
    public boolean renombrar(File archivo, String nuevoNombre) {
        try {
            if (archivo == null || !archivo.exists()) {
                throw new FileNotFoundException("El archivo no existe: " + archivo);
            }
            if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nuevo nombre no puede estar vacío.");
            }
            File destino = new File(archivo.getParent(), nuevoNombre.trim());
            if (destino.exists()) {
                throw new FileAlreadyExistsException(destino.getAbsolutePath(), null, "Ya existe un archivo con ese nombre.");
            }
            return archivo.renameTo(destino);
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
            return false;
        } catch (FileAlreadyExistsException e) {
            System.err.println("Nombre duplicado: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Nombre inválido: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al renombrar: " + e.getMessage());
            return false;
        }
    }

    // COPIAR AL PORTAPAPELES
    public void copiarAlPortapapeles(List<File> archivos) {
        try {
            if (archivos == null || archivos.isEmpty()) {
                throw new IllegalArgumentException("No hay archivos seleccionados para copiar.");
            }
            portapapeles.clear();
            portapapeles.addAll(archivos);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al copiar: " + e.getMessage());
        }
    }

    // PEGAR
    public List<String> pegar(File carpetaDestino) {
        List<String> errores = new ArrayList<>();
        try {
            if (carpetaDestino == null || !carpetaDestino.isDirectory()) {
                throw new IllegalArgumentException("La carpeta destino no es válida.");
            }
            if (portapapeles.isEmpty()) {
                errores.add("El portapapeles está vacío.");
                return errores;
            }
            for (File origen : portapapeles) {
                try {
                    if (!origen.exists()) {
                        throw new FileNotFoundException("No se encontró: " + origen.getName());
                    }
                    if (origen.isDirectory()) {
                        copiarCarpeta(origen, new File(carpetaDestino, origen.getName()));
                    } else {
                        File destino = new File(carpetaDestino, origen.getName());
                        if (destino.exists()) {
                            String nombre = origen.getName();
                            int punto = nombre.lastIndexOf('.');
                            String base = punto >= 0 ? nombre.substring(0, punto) : nombre;
                            String ext  = punto >= 0 ? nombre.substring(punto) : "";
                            destino = new File(carpetaDestino, base + "_copia" + ext);
                        }
                        Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (FileNotFoundException e) {
                    errores.add(e.getMessage());
                } catch (IOException e) {
                    errores.add("Error copiando " + origen.getName() + ": " + e.getMessage());
                }
            }
        } catch (IllegalArgumentException e) {
            errores.add("Destino inválido: " + e.getMessage());
        }
        return errores;
    }

    private void copiarCarpeta(File origen, File destino) throws IOException {
        if (!destino.exists() && !destino.mkdir()) {
            throw new IOException("No se pudo crear la carpeta: " + destino.getName());
        }
        File[] contenido = origen.listFiles();
        if (contenido != null) {
            for (File f : contenido) {
                if (f.isDirectory()) {
                    copiarCarpeta(f, new File(destino, f.getName()));
                } else {
                    Files.copy(f.toPath(), new File(destino, f.getName()).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    // ORGANIZAR
    public String organizar(File carpeta) {
        try {
            if (carpeta == null || !carpeta.isDirectory()) {
                throw new IllegalArgumentException("Selecciona una carpeta válida.");
            }
            File[] archivos = carpeta.listFiles();
            if (archivos == null || archivos.length == 0) {
                return "La carpeta está vacía, no hay nada que organizar.";
            }
            int movidos = 0;
            List<String> errores = new ArrayList<>();
            for (File archivo : archivos) {
                if (archivo.isFile()) {
                    String carpetaDest = obtenerCarpetaDestino(archivo.getName());
                    if (carpetaDest != null) {
                        try {
                            File sub = new File(carpeta, carpetaDest);
                            if (!sub.exists() && !sub.mkdir()) {
                                throw new IOException("No se pudo crear subcarpeta: " + carpetaDest);
                            }
                            File dest = new File(sub, archivo.getName());
                            if (!archivo.renameTo(dest)) {
                                throw new IOException("No se pudo mover: " + archivo.getName());
                            }
                            movidos++;
                        } catch (IOException e) {
                            errores.add(e.getMessage());
                        }
                    }
                }
            }
            String resultado = "Organización completada. Archivos movidos: " + movidos;
            if (!errores.isEmpty()) {
                resultado += "\nErrores:\n" + String.join("\n", errores);
            }
            return resultado;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado al organizar: " + e.getMessage();
        }
    }

    private String obtenerCarpetaDestino(String nombre) {
        String n = nombre.toLowerCase();
        if (n.endsWith(".jpg") || n.endsWith(".png") || n.endsWith(".gif")
                || n.endsWith(".jpeg") || n.endsWith(".bmp")) return "Imagenes";
        if (n.endsWith(".pdf") || n.endsWith(".docx") || n.endsWith(".txt")
                || n.endsWith(".doc") || n.endsWith(".odt")) return "Documentos";
        if (n.endsWith(".mp3") || n.endsWith(".wav") || n.endsWith(".ogg")
                || n.endsWith(".flac")) return "Musica";
        return null;
    }

    public File[] listarContenido(File carpeta) {
        try {
            if (carpeta == null || !carpeta.isDirectory()) {
                throw new IllegalArgumentException("La carpeta no es válida.");
            }
            File[] contenido = carpeta.listFiles();
            return contenido != null ? contenido : new File[0];
        } catch (IllegalArgumentException e) {
            System.err.println("Error al listar: " + e.getMessage());
            return new File[0];
        }
    }

    public boolean portapapelesVacio() { return portapapeles.isEmpty(); }
    public List<File> getPortapapeles() { return portapapeles; }
}