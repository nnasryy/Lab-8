/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author nasry
 */
public class audioPlayer extends JPanel {
 
    private audioLogic player;
 
    private JLabel lblTitulo;
    private JLabel lblEstado;
    private JButton btnPlay;
    private JButton btnStop;
    private JSlider sliderVolumen;
 
    private final Color COLOR_FONDO  = new Color(30, 30, 30);
    private final Color COLOR_PANEL  = new Color(45, 45, 48);
    private final Color COLOR_TEXTO  = new Color(240, 240, 240);
    private final Color COLOR_ACCENT = new Color(233, 84, 32);
 
    public audioPlayer() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }
 
    private void buildUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_PANEL);
        topPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
 
        lblTitulo = new JLabel("Sin archivo cargado");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
 
        lblEstado = new JLabel("Detenido");
        lblEstado.setForeground(COLOR_ACCENT);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
 
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        topPanel.add(lblEstado, BorderLayout.SOUTH);
 

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(COLOR_FONDO);
 
        btnPlay = new JButton("▶  Reproducir");
        btnPlay.setBackground(COLOR_ACCENT);
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFocusPainted(false);
        btnPlay.setBorderPainted(false);
        btnPlay.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPlay.setPreferredSize(new Dimension(130, 35));
        btnPlay.addActionListener(e -> {
            if (player != null) {
                lblEstado.setText("Reproduciendo...");
            }
        });
 
        btnStop = new JButton("■  Detener");
        btnStop.setBackground(new Color(60, 60, 60));
        btnStop.setForeground(Color.WHITE);
        btnStop.setFocusPainted(false);
        btnStop.setBorderPainted(false);
        btnStop.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnStop.setPreferredSize(new Dimension(110, 35));
        btnStop.addActionListener(e -> stopPlayback());
 
        controlPanel.add(btnPlay);
        controlPanel.add(btnStop);

        JPanel volPanel = new JPanel(new BorderLayout(10, 0));
        volPanel.setBackground(COLOR_FONDO);
        volPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
 
        JLabel lblVol = new JLabel("Volumen:");
        lblVol.setForeground(COLOR_TEXTO);
        lblVol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
 
        sliderVolumen = new JSlider(0, 100, 80);
        sliderVolumen.setBackground(COLOR_FONDO);
        sliderVolumen.setForeground(COLOR_ACCENT);
 
        volPanel.add(lblVol, BorderLayout.WEST);
        volPanel.add(sliderVolumen, BorderLayout.CENTER);
 
        add(topPanel,    BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(volPanel,    BorderLayout.SOUTH);
    }
 
    public void setPlayerExterno(audioLogic player) {
        this.player = player;
    }
 
    public audioLogic getPlayer() {
        return player;
    }
 
    public void playExterno(File archivo) {
        if (archivo != null) {
            lblTitulo.setText(archivo.getName());
            lblEstado.setText("Reproduciendo...");
        }
    }

    public void stopPlayback() {
        if (player != null) {
            player.stop();
        }
        lblEstado.setText("Detenido");
    }

    public void setEstado(String estado) {
        if (lblEstado != null) lblEstado.setText(estado);
    }
}
