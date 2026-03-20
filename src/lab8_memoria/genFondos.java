/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author nasry
 */
public class genFondos extends JDesktopPane {
 
    private static final Color COLOR_FONDO = new Color(30, 30, 30);
 
    public genFondos() {
        setBackground(COLOR_FONDO);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }
 
    @Override
    public Component add(Component frame) {
        Component c = super.add(frame);
        if (frame instanceof JInternalFrame) {
            ((JInternalFrame) frame).toFront();
        }
        return c;
    }
}