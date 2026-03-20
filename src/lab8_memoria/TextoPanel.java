/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
/**
 *
 * @author nasry
 */
public class TextoPanel extends JPanel {
 
    private JTextPane textPane;
    private JLabel lblNombreArchivo;
 
    private final Color COLOR_FONDO  = new Color(30, 30, 30);
    private final Color COLOR_PANEL  = new Color(45, 45, 48);
    private final Color COLOR_TEXTO  = new Color(240, 240, 240);
    private final Color COLOR_EDITOR = new Color(25, 25, 25);
    private final Color COLOR_ACCENT = new Color(233, 84, 32);
 
    public TextoPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_FONDO);
        buildUI();
    }
 
    private void buildUI() {
      
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_PANEL);
        topBar.setBorder(new EmptyBorder(6, 12, 6, 12));
 
        lblNombreArchivo = new JLabel("Nuevo documento");
        lblNombreArchivo.setForeground(COLOR_TEXTO);
        lblNombreArchivo.setFont(new Font("Segoe UI", Font.BOLD, 13));
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setBackground(COLOR_PANEL);
 
        JButton btnGuardar = crearBoton("Guardar");
        JButton btnLimpiar = crearBoton("Limpiar");
 
        btnLimpiar.addActionListener(e -> {
            textPane.setText("");
            lblNombreArchivo.setText("Nuevo documento");
        });
 
        btnPanel.add(btnGuardar);
        btnPanel.add(btnLimpiar);
 
        topBar.add(lblNombreArchivo, BorderLayout.WEST);
        topBar.add(btnPanel, BorderLayout.EAST);
 

        textPane = new JTextPane();
        textPane.setBackground(COLOR_EDITOR);
        textPane.setForeground(COLOR_TEXTO);
        textPane.setCaretColor(COLOR_ACCENT);
        textPane.setFont(new Font("Consolas", Font.PLAIN, 13));
        textPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        textPane.setSelectionColor(new Color(233, 84, 32, 120));
 
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_EDITOR);
        scrollPane.setBackground(COLOR_FONDO);
 

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        statusBar.setBackground(COLOR_PANEL);
        JLabel lblStatus = new JLabel("Listo");
        lblStatus.setForeground(new Color(160, 160, 160));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(lblStatus);
 
        add(topBar,    BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
 
    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(COLOR_TEXTO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setPreferredSize(new Dimension(80, 26));
        return btn;
    }
 
 
    public JTextPane getTextPane() {
        return textPane;
    }
 
 
    public void setNombreArchivo(String nombre) {
        if (lblNombreArchivo != null) {
            lblNombreArchivo.setText(nombre != null ? nombre : "Nuevo documento");
        }
    }

    public String getContenido() {
        return textPane != null ? textPane.getText() : "";
    }
}