/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
/**
 *
 * @author nasry
 */
public class TextLogic {

    public void abrirExterno(TextoPanel panel, JTextComponent txtPane, File archivo) {
        if (archivo == null || !archivo.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe o no es accesible.",
                "Error al abrir", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null,
                "Solo se pueden abrir archivos de texto (.txt).",
                "Formato no soportado", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        try {
            String contenido = new String(
                Files.readAllBytes(archivo.toPath()),
                StandardCharsets.UTF_8
            );
 
            txtPane.setText(contenido);
            txtPane.setCaretPosition(0); 
 
            if (panel != null) {
                panel.setNombreArchivo(archivo.getName());
            }
 
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error al leer el archivo: " + e.getMessage(),
                "Error de E/S", JOptionPane.ERROR_MESSAGE);
        }
    }
 
  
    public void guardarExterno(JTextComponent txtPane, File archivo) {
        if (txtPane == null || archivo == null) return;
 
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
 
            writer.write(txtPane.getText());
            JOptionPane.showMessageDialog(null,
                "Archivo guardado exitosamente: " + archivo.getName(),
                "Guardado", JOptionPane.INFORMATION_MESSAGE);
 
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar el archivo: " + e.getMessage(),
                "Error de E/S", JOptionPane.ERROR_MESSAGE);
        }
    }
 
 
    public File nuevoArchivo(String ruta) {
        File archivo = new File(ruta);
        try {
            if (archivo.createNewFile()) {
                return archivo;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "No se pudo crear el archivo: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}