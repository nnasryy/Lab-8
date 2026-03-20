/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 *
 * @author nasry
 */
public class audioLogic {
     private audioPlayer panelUI;
    private Clip clip;
    private boolean playing = false;
    private File archivoActual;

    public audioLogic(audioPlayer panel) {
        this.panelUI = panel;
    }
 
    public void load(File archivo) {
        if (archivo == null || !archivo.exists()) return;
 
        stop(); 
        archivoActual = archivo;
 
        String nombre = archivo.getName().toLowerCase();
 
        if (nombre.endsWith(".mp3")) {

            JOptionPane.showMessageDialog(null,
                "Reproducción de MP3 requiere librería adicional (JLayer).\n" +
                "Solo se soporta .wav de forma nativa.",
                "Formato no soportado", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        if (nombre.endsWith(".wav")) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
 
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        playing = false;
                        if (panelUI != null) panelUI.setEstado("Detenido");
                    }
                    if (event.getType() == LineEvent.Type.START) {
                        playing = true;
                        if (panelUI != null) panelUI.setEstado("Reproduciendo...");
                    }
                });
 
                clip.start();
                playing = true;
 
            } catch (UnsupportedAudioFileException e) {
                JOptionPane.showMessageDialog(null,
                    "Formato de audio no soportado: " + archivo.getName(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (LineUnavailableException | IOException e) {
                JOptionPane.showMessageDialog(null,
                    "Error al reproducir el archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    public boolean isPlaying() {
        return playing && clip != null && clip.isRunning();
    }

    public void stop() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.close();
            clip = null;
        }
        playing = false;
        if (panelUI != null) panelUI.setEstado("Detenido");
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            playing = false;
            if (panelUI != null) panelUI.setEstado("Pausado");
        }
    }
 
    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            playing = true;
            if (panelUI != null) panelUI.setEstado("Reproduciendo...");
        }
    }

    public File getArchivoActual() {
        return archivoActual;
    }
}
