/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab8_memoria;

import javax.swing.*;

/**
 *
 * @author nasry
 */
public class Lab8_Memoria {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JAVA CENTER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            genFondos escritorio = new genFondos();

            FileExplorer explorador = new FileExplorer(escritorio);

            JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                explorador,
                escritorio
            );
            splitPane.setDividerLocation(750);
            splitPane.setResizeWeight(0.75);

            frame.add(splitPane);
            frame.setVisible(true);
        });
    }
}
