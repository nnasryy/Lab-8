/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;
import javax.swing.*;
import java.io.File;
/**
 *
 * @author nasry
 */
public class ImagenesLogic {

    private static final String[] EXTENSIONES_SOPORTADAS = {".png", ".jpg", ".jpeg", ".bmp", ".gif"};
 
    public void ImportarImagenesExterno(VisorImagenes visor, File archivo) {
        if (visor == null) return;
 
        if (archivo == null || !archivo.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe o no es accesible.",
                "Error al cargar imagen", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (!esExtensionSoportada(archivo.getName())) {
            JOptionPane.showMessageDialog(null,
                "El archivo \"" + archivo.getName() + "\" no es una imagen soportada.\n" +
                "Formatos válidos: PNG, JPG, JPEG, BMP, GIF",
                "Formato no soportado", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        visor.mostrarImagen(archivo);
    }
 
    private boolean esExtensionSoportada(String nombreArchivo) {
        if (nombreArchivo == null) return false;
        String lower = nombreArchivo.toLowerCase();
        for (String ext : EXTENSIONES_SOPORTADAS) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }
}
