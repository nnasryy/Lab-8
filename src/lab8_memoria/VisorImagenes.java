/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
/**
 *
 * @author nasry
 */

    public class VisorImagenes extends JPanel {
 
    private BufferedImage imagenActual;
    private JLabel lblNombreArchivo;
    private JLabel lblDimensiones;
    private JPanel panelInfo;
 
    private final Color COLOR_FONDO  = new Color(20, 20, 20);
    private final Color COLOR_PANEL  = new Color(45, 45, 48);
    private final Color COLOR_TEXTO  = new Color(240, 240, 240);
    private final Color COLOR_MUTED  = new Color(160, 160, 160);
 
    public VisorImagenes() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_FONDO);
        buildUI();
    }
 
    private void buildUI() {
        // ── Panel de información inferior ───────────────────────────────────
        panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelInfo.setBackground(COLOR_PANEL);
        panelInfo.setBorder(new EmptyBorder(4, 10, 4, 10));
 
        lblNombreArchivo = new JLabel("Sin imagen cargada");
        lblNombreArchivo.setForeground(COLOR_TEXTO);
        lblNombreArchivo.setFont(new Font("Segoe UI", Font.BOLD, 12));
 
        lblDimensiones = new JLabel("");
        lblDimensiones.setForeground(COLOR_MUTED);
        lblDimensiones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
 
        panelInfo.add(lblNombreArchivo);
        panelInfo.add(lblDimensiones);
 
        add(panelInfo, BorderLayout.SOUTH);
    }
 
    /**
     * Carga y muestra una imagen desde un archivo.
     * Actualiza la etiqueta de información con nombre y dimensiones.
     * @param archivo Archivo de imagen (.png o .jpg) a mostrar.
     */
    public void mostrarImagen(File archivo) {
        if (archivo == null || !archivo.exists()) return;
 
        try {
            imagenActual = ImageIO.read(archivo);
            if (imagenActual != null) {
                lblNombreArchivo.setText(archivo.getName());
                lblDimensiones.setText(
                    imagenActual.getWidth() + " × " + imagenActual.getHeight() + " px"
                );
                repaint();
            }
        } catch (Exception e) {
            lblNombreArchivo.setText("Error al cargar: " + archivo.getName());
            lblDimensiones.setText("");
        }
    }
 
    /**
     * Retorna la imagen actualmente cargada.
     * @return BufferedImage actual, o null si no hay ninguna.
     */
    public BufferedImage getImagenActual() {
        return imagenActual;
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
 
        if (imagenActual == null) {
            // Placeholder cuando no hay imagen
            g.setColor(COLOR_MUTED);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            String msg = "Sin imagen";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g.drawString(msg, x, y);
            return;
        }
 
        // Escalar imagen manteniendo proporción
        int panelW = getWidth();
        int panelH = getHeight() - (panelInfo != null ? panelInfo.getHeight() : 30);
 
        if (panelW <= 0 || panelH <= 0) return;
 
        double scaleX = (double) panelW  / imagenActual.getWidth();
        double scaleY = (double) panelH  / imagenActual.getHeight();
        double scale  = Math.min(scaleX, scaleY);
 
        int drawW = (int) (imagenActual.getWidth()  * scale);
        int drawH = (int) (imagenActual.getHeight() * scale);
        int drawX = (panelW - drawW) / 2;
        int drawY = (panelH - drawH) / 2;
 
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(imagenActual, drawX, drawY, drawW, drawH, this);
    }
}
