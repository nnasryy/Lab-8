/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

/**
 *
 * @author jerem
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

public class VentanaPrincipal extends JFrame {

    private JTree arbolUI;
    private JTable tablaArchivos;
    private DefaultTableModel modeloTabla;
    private JLabel labelRuta;
    private JLabel labelEstado;

    private GestorArchivos gestor;
    private ListaEnlazadaArchivos listaArchivos;
    private ArbolDirectorios arbolDirectorios;

    private File carpetaActual;
    private DefaultMutableTreeNode nodoActual;

    private List<File> historial = new ArrayList<>();
    private int posHistorial = -1;

    private static final Color COLOR_TITULO_FONDO = new Color(55, 100, 175);
    private static final Color COLOR_NAV_FONDO    = new Color(195, 215, 245);
    private static final Color COLOR_NAV_BORDE    = new Color(150, 180, 225);
    private static final Color COLOR_PANEL_IZQ    = new Color(235, 241, 252);
    private static final Color COLOR_TABLA_HEADER = new Color(220, 232, 248);
    private static final Color COLOR_TABLA_GRID   = new Color(215, 224, 240);
    private static final Color COLOR_SELECCION    = new Color(180, 205, 240);
    private static final Color COLOR_FONDO_TABLA  = Color.WHITE;
    private static final Color COLOR_PIE          = new Color(220, 230, 245);

    public VentanaPrincipal() {
    try {
        gestor        = new GestorArchivos();
        listaArchivos = new ListaEnlazadaArchivos();

        File carpetaRaiz = crearEstructuraRaiz();
        carpetaActual    = carpetaRaiz;

        arbolDirectorios = new ArbolDirectorios(carpetaRaiz);
        arbolUI          = arbolDirectorios.getArbol();

        configurarVentana();
        construirGUI();
        configurarEventos();
        cargarContenido(carpetaRaiz);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null,
                "Error al iniciar la aplicación:\n" + e.getMessage(),
                "Error crítico", JOptionPane.ERROR_MESSAGE);
    }
}

    private void configurarVentana() {
        setTitle("Navegador y Organizador de Archivos");
        setSize(860, 590);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(680, 440));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Look and Feel no disponible: " + e.getMessage());
        }
    }

    private void construirGUI() {
        setLayout(new BorderLayout(0, 0));
        add(crearNorte(),      BorderLayout.NORTH);
        add(crearCentro(),     BorderLayout.CENTER);
        add(crearPie(),        BorderLayout.SOUTH);
    }

    private JPanel crearNorte() {
        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(COLOR_TITULO_FONDO);

        JPanel barraT = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        barraT.setBackground(COLOR_TITULO_FONDO);
        JLabel icono = new JLabel(" ");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel txtTitulo = new JLabel("Navegador y Organizador de Archivos");
        txtTitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
        txtTitulo.setForeground(Color.WHITE);
        barraT.add(icono);
        barraT.add(txtTitulo);
        norte.add(barraT, BorderLayout.NORTH);

        norte.add(crearBarraNavegacion(), BorderLayout.SOUTH);
        return norte;
    }

    private JPanel crearBarraNavegacion() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBackground(COLOR_NAV_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_NAV_BORDE),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));

        JPanel flechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        flechas.setBackground(COLOR_NAV_FONDO);
        JButton btnAtras    = crearBotonFlecha("<");
        JButton btnAdelante = crearBotonFlecha(">");
        btnAtras.addActionListener(e -> navegarAtras());
        btnAdelante.addActionListener(e -> navegarAdelante());
        flechas.add(btnAtras);
        flechas.add(btnAdelante);
        panel.add(flechas, BorderLayout.WEST);

        labelRuta = new JLabel();
        labelRuta.setFont(new Font("Tahoma", Font.PLAIN, 12));
        labelRuta.setForeground(new Color(30, 30, 30));
        labelRuta.setBackground(Color.WHITE);
        labelRuta.setOpaque(true);
        labelRuta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_NAV_BORDE),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        actualizarLabelRuta();
        panel.add(labelRuta, BorderLayout.CENTER);

        JPanel panelOrden = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        panelOrden.setBackground(COLOR_NAV_FONDO);

        JLabel lblOrd = new JLabel("Ordenar:");
        lblOrd.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblOrd.setForeground(new Color(30, 30, 30));

        String[] modos = {
            "Nombre (Bubble)", "Tamaño (Bubble)",
            "Fecha (Bubble)",  "Tipo (Bubble)",
            "Nombre (Merge)"
        };
        JComboBox<String> combo = new JComboBox<>(modos);
        combo.setFont(new Font("Tahoma", Font.PLAIN, 11));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(155, 24));
        combo.addActionListener(e -> accionOrdenarCombo(combo.getSelectedIndex()));

        panelOrden.add(lblOrd);
        panelOrden.add(combo);
        panel.add(panelOrden, BorderLayout.EAST);

        return panel;
    }

    private JSplitPane crearCentro() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelArbol(), crearPanelDerecho());
        split.setDividerLocation(220);
        split.setDividerSize(3);
        split.setBorder(null);
        return split;
    }

    private JPanel crearPanelArbol() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL_IZQ);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_NAV_BORDE));

        arbolUI.setBackground(COLOR_PANEL_IZQ);
        arbolUI.setFont(new Font("Tahoma", Font.PLAIN, 12));
        arbolUI.setRowHeight(22);
        arbolUI.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        arbolUI.setComponentPopupMenu(crearMenuContextualArbol());

        JScrollPane scroll = new JScrollPane(arbolUI);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_PANEL_IZQ);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO_TABLA);

        String[] cols = {"Nombre", "Fecha de modificación", "Tipo", "Tamaño"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaArchivos = new JTable(modeloTabla);
        tablaArchivos.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tablaArchivos.setRowHeight(22);
        tablaArchivos.setBackground(COLOR_FONDO_TABLA);
        tablaArchivos.setSelectionBackground(COLOR_SELECCION);
        tablaArchivos.setSelectionForeground(new Color(20, 20, 20));
        tablaArchivos.setGridColor(COLOR_TABLA_GRID);
        tablaArchivos.setShowHorizontalLines(true);
        tablaArchivos.setShowVerticalLines(false);
        tablaArchivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaArchivos.setFillsViewportHeight(true);

        JTableHeader header = tablaArchivos.getTableHeader();
        header.setBackground(COLOR_TABLA_HEADER);
        header.setForeground(new Color(40, 55, 100));
        header.setFont(new Font("Tahoma", Font.PLAIN, 12));
        header.setPreferredSize(new Dimension(0, 24));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_NAV_BORDE));

        tablaArchivos.getColumnModel().getColumn(0).setPreferredWidth(240);
        tablaArchivos.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaArchivos.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaArchivos.getColumnModel().getColumn(3).setPreferredWidth(70);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaArchivos.columnAtPoint(e.getPoint());
                accionOrdenar(col);
            }
        });

        tablaArchivos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if (e.getClickCount() == 2) {
                        int fila = tablaArchivos.getSelectedRow();
                        if (fila < 0) return;
                        String nombre = limpiarNombre((String) modeloTabla.getValueAt(fila, 0));
                        File f = new File(carpetaActual, nombre);

                        if (f.isDirectory()) {
                            cargarContenido(f);
                        } else if (f.isFile()) {
                            if (java.awt.Desktop.isDesktopSupported()) {
                                try {
                                    java.awt.Desktop.getDesktop().open(f);
                                } catch (Exception ex) {
                                    mostrarError("No se pudo abrir el archivo",
                                            "No hay programa asociado para abrir: " + f.getName());
                                }
                            } else {
                                mostrarAdvertencia("Tu sistema no soporta abrir archivos automáticamente.");
                            }
                        }
                    }
                } catch (Exception ex) {
                    mostrarError("Error al abrir", ex.getMessage());
                }
            }
        });

        tablaArchivos.setComponentPopupMenu(crearMenuContextualTabla());

        JScrollPane scroll = new JScrollPane(tablaArchivos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO_TABLA);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(crearBarraHerramientas(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearBarraHerramientas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setBackground(COLOR_NAV_FONDO);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_NAV_BORDE));

        JButton btnOrganizar    = crearBotonAccion("📂 Organizar",      new Color(60, 110, 190));
        JButton btnNuevaCarpeta = crearBotonAccion("📁 Nueva Carpeta",  new Color(70, 130, 70));
        JButton btnRenombrar    = crearBotonAccion("✏ Renombrar",       new Color(140, 95, 40));
        JButton btnCopiar       = crearBotonAccion("📋 Copiar",         new Color(70, 70, 140));
        JButton btnPegar        = crearBotonAccion("📌 Pegar",          new Color(140, 70, 70));
        JButton btnImportar = crearBotonAccion("📥 Importar", new Color(80, 150, 130));
        JButton btnEliminar = crearBotonAccion("🗑 Eliminar", new Color(180, 50, 50));

        btnOrganizar.addActionListener(e    -> accionOrganizar());
        btnNuevaCarpeta.addActionListener(e -> accionNuevaCarpeta());
        btnRenombrar.addActionListener(e    -> accionRenombrar());
        btnCopiar.addActionListener(e       -> accionCopiar());
        btnPegar.addActionListener(e        -> accionPegar());
        btnImportar.addActionListener(e -> accionImportar());  
        btnEliminar.addActionListener(e -> accionEliminar());
        
        panel.add(btnEliminar);    
        panel.add(btnImportar);

        panel.add(btnOrganizar);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnNuevaCarpeta);
        panel.add(btnRenombrar);
        panel.add(btnCopiar);
        panel.add(btnPegar);

        return panel;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(COLOR_PIE);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_NAV_BORDE));
        pie.setPreferredSize(new Dimension(0, 24));

        labelEstado = new JLabel("  Listo");
        labelEstado.setFont(new Font("Tahoma", Font.PLAIN, 11));
        labelEstado.setForeground(new Color(80, 90, 120));
        pie.add(labelEstado, BorderLayout.WEST);

        JLabel lblDerechos = new JLabel("Angie - Nasry - Adan - Jeremy", SwingConstants.RIGHT);
        lblDerechos.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblDerechos.setForeground(new Color(80, 90, 120));
        pie.add(lblDerechos, BorderLayout.EAST);

        return pie;
    }

    private JPopupMenu crearMenuContextualArbol() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miNueva     = new JMenuItem("📁 Nueva Carpeta");
        JMenuItem miActualizar = new JMenuItem("🔄 Actualizar");

        miNueva.addActionListener(e -> accionNuevaCarpeta());
        miActualizar.addActionListener(e -> {
            try {
                if (nodoActual != null) {
                    arbolDirectorios.recargarNodo(nodoActual);
                    cargarContenido(carpetaActual);
                }
            } catch (Exception ex) {
                mostrarError("Error al actualizar", ex.getMessage());
            }
        });

        menu.add(miNueva);
        menu.add(miActualizar);
        return menu;
    }

    private JPopupMenu crearMenuContextualTabla() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miAbrir     = new JMenuItem("📂 Abrir");
        JMenuItem miNueva     = new JMenuItem("📁 Nueva Carpeta");
        JMenuItem miOrganizar = new JMenuItem("🗂 Organizar");
        JMenuItem miCopiar    = new JMenuItem("📋 Copiar");
        JMenuItem miPegar     = new JMenuItem("📌 Pegar");
        JMenuItem miRenombrar = new JMenuItem("✏ Renombrar");

        miAbrir.addActionListener(e -> {
            try {
                int fila = tablaArchivos.getSelectedRow();
                if (fila < 0) throw new Exception("No hay elemento seleccionado.");
                String nombre = limpiarNombre((String) modeloTabla.getValueAt(fila, 0));
                File f = new File(carpetaActual, nombre);
                if (f.isDirectory()) cargarContenido(f);
            } catch (Exception ex) {
                mostrarError("Error al abrir", ex.getMessage());
            }
        });
        miNueva.addActionListener(e     -> accionNuevaCarpeta());
        miOrganizar.addActionListener(e -> accionOrganizar());
        miCopiar.addActionListener(e    -> accionCopiar());
        miPegar.addActionListener(e     -> accionPegar());
        miRenombrar.addActionListener(e -> accionRenombrar());

        menu.add(miAbrir);
        menu.addSeparator();
        menu.add(miNueva);
        menu.add(miOrganizar);
        menu.addSeparator();
        menu.add(miCopiar);
        menu.add(miPegar);
        menu.addSeparator();
        menu.add(miRenombrar);
        return menu;
    }

    private void configurarEventos() {
        arbolUI.addTreeSelectionListener(e -> {
            try {
                DefaultMutableTreeNode nodo =
                        (DefaultMutableTreeNode) arbolUI.getLastSelectedPathComponent();
                if (nodo == null) return;
                nodoActual = nodo;
                Object obj = nodo.getUserObject();
                if (obj instanceof ArbolDirectorios.ArchivoNodo) {
                    File carpeta = ((ArbolDirectorios.ArchivoNodo) obj).getArchivo();
                    cargarContenido(carpeta);
                }
            } catch (Exception ex) {
                System.err.println("Error en selección de árbol: " + ex.getMessage());
            }
        });
    }

    private void cargarContenido(File carpeta) {
        try {
            if (carpeta == null || !carpeta.exists()) {
                if (carpeta != null && carpeta.getParentFile() != null
                        && carpeta.getParentFile().exists()) {
                    carpeta = carpeta.getParentFile();
                } else {
                    return;
                }
            }

            if (posHistorial < historial.size() - 1) {
                historial = new ArrayList<>(historial.subList(0, posHistorial + 1));
            }
            historial.add(carpeta);
            posHistorial = historial.size() - 1;

            carpetaActual = carpeta;
            actualizarLabelRuta();

            listaArchivos.limpiar();
            for (File f : gestor.listarContenido(carpeta)) {
                listaArchivos.agregar(f);
            }
            actualizarTabla();
            setEstado("  " + listaArchivos.getTamanio() + " elementos");

        } catch (IllegalArgumentException ex) {
            mostrarError("Carpeta inválida", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error al cargar carpeta", ex.getMessage());
        }
    }

    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (File f : listaArchivos.aLista()) {
                String ico    = f.isDirectory() ? "📁 " : obtenerIcono(f.getName());
                String nombre = ico + f.getName();
                String fecha  = sdf.format(new Date(f.lastModified()));
                String tipo   = f.isDirectory() ? "Carpeta de archivos" : obtenerTipo(f.getName());
                String tam    = f.isDirectory() ? "" : formatearTamanio(f.length());
                modeloTabla.addRow(new Object[]{nombre, fecha, tipo, tam});
            }
        } catch (Exception e) {
            mostrarError("Error al actualizar tabla", e.getMessage());
        }
    }

    private void actualizarLabelRuta() {
        try {
            if (carpetaActual == null) return;
            String home = System.getProperty("user.home");
            String ruta = carpetaActual.getAbsolutePath();
            if (ruta.startsWith(home)) {
                ruta = ruta.substring(home.length());
                if (ruta.startsWith(File.separator)) ruta = ruta.substring(1);
                ruta = ruta.replace(File.separator, "/");
            }
            if (ruta.isEmpty()) ruta = carpetaActual.getName();
            labelRuta.setText("  " + ruta + "  ▶");
        } catch (Exception e) {
            System.err.println("Error al actualizar ruta: " + e.getMessage());
        }
    }
    
    private void accionOrganizar() {
        try {
            if (carpetaActual == null) throw new IllegalStateException("No hay carpeta seleccionada.");
            int op = JOptionPane.showConfirmDialog(this,
                    "¿Organizar archivos de \"" + carpetaActual.getName() + "\" por tipo?\n" +
                    "Se crearán subcarpetas: Imagenes, Documentos, Musica",
                    "Organizar archivos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op == JOptionPane.YES_OPTION) {
                String res = gestor.organizar(carpetaActual);
                JOptionPane.showMessageDialog(this, res, "Resultado", JOptionPane.INFORMATION_MESSAGE);
                cargarContenido(carpetaActual);
                if (nodoActual != null) arbolDirectorios.recargarNodo(nodoActual);
            }
        } catch (IllegalStateException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al organizar", e.getMessage());
        }
    }

    private void accionNuevaCarpeta() {
        try {
            if (carpetaActual == null) throw new IllegalStateException("No hay carpeta seleccionada.");

            if (nodoActual == null) {
                mostrarAdvertencia("Haz clic primero en una carpeta del árbol de la izquierda.");
                return;
            }

            String nombre = JOptionPane.showInputDialog(this,
                    "Nombre de la nueva carpeta:", "Nueva Carpeta", JOptionPane.PLAIN_MESSAGE);
            if (nombre == null) return;
            if (nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");

            boolean ok = gestor.crearCarpeta(carpetaActual, nombre);
            if (ok) {
                File nueva = new File(carpetaActual, nombre.trim());
                arbolDirectorios.agregarNodo(nodoActual, nueva);
                arbolUI.expandPath(new TreePath(
                        ((DefaultMutableTreeNode) nodoActual).getPath()));
                cargarContenido(carpetaActual);
                setEstado("  Carpeta \"" + nombre.trim() + "\" creada.");
            } else {
                throw new Exception("No se pudo crear. ¿Ya existe una carpeta con ese nombre?");
            }

        } catch (IllegalArgumentException e) {
            mostrarError("Nombre inválido", e.getMessage());
        } catch (IllegalStateException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al crear carpeta", e.getMessage());
        }
    }

    private void accionRenombrar() {
        try {
            File archivo = null;
            String nombreViejo = null;

            int fila = tablaArchivos.getSelectedRow();
            if (fila >= 0) {
                nombreViejo = (String) modeloTabla.getValueAt(fila, 0);
                nombreViejo = nombreViejo.trim().replaceAll("^\\S+\\s+", "");
                archivo = new File(carpetaActual, nombreViejo);
            }
            else if (nodoActual != null) {
                Object obj = nodoActual.getUserObject();
                if (obj instanceof ArbolDirectorios.ArchivoNodo) {
                    archivo = ((ArbolDirectorios.ArchivoNodo) obj).getArchivo();
                    nombreViejo = archivo.getName();
                }
            }

            if (archivo == null || nombreViejo == null) {
                throw new IllegalStateException("Selecciona un archivo o carpeta primero.");
            }
            if (!archivo.exists()) {
                throw new IllegalStateException("El elemento ya no existe en disco.");
            }

            String nuevo = (String) JOptionPane.showInputDialog(
                    this,
                    "Nuevo nombre:",
                    "Renombrar",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    nombreViejo);

            if (nuevo == null) return;
            nuevo = nuevo.trim();
            if (nuevo.isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
            if (nuevo.equals(nombreViejo)) return;

            boolean ok = gestor.renombrar(archivo, nuevo);
            if (ok) {
                if (carpetaActual.equals(archivo)) {
                    carpetaActual = new File(archivo.getParentFile(), nuevo);
                }

                if (nodoActual != null) {
                    DefaultMutableTreeNode padre =
                            (DefaultMutableTreeNode) nodoActual.getParent();
                    if (padre != null) {
                        arbolDirectorios.recargarNodo(padre);
                    } else {
                        arbolDirectorios.recargarNodo(nodoActual);
                    }
                }

                cargarContenido(carpetaActual);
                setEstado("  Renombrado: \"" + nombreViejo + "\" → \"" + nuevo + "\"");
            } else {
                throw new Exception("No se pudo renombrar. ¿Ya existe ese nombre?");
            }

        } catch (IllegalArgumentException e) {
            mostrarError("Nombre inválido", e.getMessage());
        } catch (IllegalStateException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al renombrar", e.getMessage());
        }
    }

    private void accionCopiar() {
        try {
            int[] filas = tablaArchivos.getSelectedRows();
            if (filas.length == 0) throw new IllegalStateException("Selecciona archivos para copiar.");
            List<File> seleccionados = new ArrayList<>();
            for (int fila : filas) {
                String nombre = limpiarNombre((String) modeloTabla.getValueAt(fila, 0));
                File f = new File(carpetaActual, nombre);
                if (!f.exists()) throw new IllegalStateException("El archivo \"" + nombre + "\" no existe.");
                seleccionados.add(f);
            }
            gestor.copiarAlPortapapeles(seleccionados);
            setEstado("  " + seleccionados.size() + " elemento(s) en el portapapeles.");
        } catch (IllegalStateException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al copiar", e.getMessage());
        }
    }

    private void accionPegar() {
        try {
            if (gestor.portapapelesVacio()) throw new IllegalStateException("El portapapeles está vacío. Primero copia archivos.");
            List<String> errores = gestor.pegar(carpetaActual);
            cargarContenido(carpetaActual);
            if (errores.isEmpty()) {
                setEstado("  Pegado exitoso en: " + carpetaActual.getName());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Algunos archivos no se pudieron pegar:\n" + String.join("\n", errores),
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalStateException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al pegar", e.getMessage());
        }
    }

    private void accionOrdenar(int columna) {
        try {
            switch (columna) {
                case 0: listaArchivos.ordenarPorNombreBubble();   break;
                case 1: listaArchivos.ordenarPorFechaBubble();    break;
                case 2: listaArchivos.ordenarPorTipoBubble();     break;
                case 3: listaArchivos.ordenarPorTamanioBubble();  break;
                default: return;
            }
            actualizarTabla();
        } catch (Exception e) {
            mostrarError("Error al ordenar", e.getMessage());
        }
    }
    
    

    private void accionOrdenarCombo(int idx) {
        try {
            switch (idx) {
                case 0: listaArchivos.ordenarPorNombreBubble();   break;
                case 1: listaArchivos.ordenarPorTamanioBubble();  break;
                case 2: listaArchivos.ordenarPorFechaBubble();    break;
                case 3: listaArchivos.ordenarPorTipoBubble();     break;
                case 4:
                    listaArchivos.ordenarMergeSortPorNombre();
                    listaArchivos.recalcularTamanio();
                    break;
            }
            actualizarTabla();
        } catch (Exception e) {
            mostrarError("Error al ordenar", e.getMessage());
        }
    }
    
    private void accionImportar() {
        try {
            if (carpetaActual == null) throw new IllegalStateException("Selecciona una carpeta destino primero.");

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Seleccionar archivos para importar");
            chooser.setMultiSelectionEnabled(true);

            int resultado = chooser.showOpenDialog(this);
            if (resultado != JFileChooser.APPROVE_OPTION) return;

            File[] archivos = chooser.getSelectedFiles();
            if (archivos.length == 0) return;

            JProgressBar barra = new JProgressBar(0, archivos.length);
            barra.setStringPainted(true);
            barra.setString("Importando...");

            JDialog dialogo = new JDialog(this, "Importando archivos", false);
            dialogo.setLayout(new BorderLayout(10, 10));
            dialogo.add(new JLabel("  Importando " + archivos.length + " archivo(s)..."), BorderLayout.NORTH);
            dialogo.add(barra, BorderLayout.CENTER);
            dialogo.setSize(350, 100);
            dialogo.setLocationRelativeTo(this);
            dialogo.setVisible(true);

            SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    int importados = 0;
                    for (int i = 0; i < archivos.length; i++) {
                        File origen = archivos[i];
                        try {
                            File destino = new File(carpetaActual, origen.getName());
                            if (destino.exists()) {
                                String nombre = origen.getName();
                                int punto = nombre.lastIndexOf('.');
                                String base = punto >= 0 ? nombre.substring(0, punto) : nombre;
                                String ext  = punto >= 0 ? nombre.substring(punto) : "";
                                destino = new File(carpetaActual, base + "_copia" + ext);
                            }
                            java.nio.file.Files.copy(
                                origen.toPath(),
                                destino.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                            );
                            importados++;
                        } catch (Exception ex) {
                            System.err.println("Error importando " + origen.getName() + ": " + ex.getMessage());
                        }
                        publish(i + 1);
                    }
                    return importados;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    int ultimo = chunks.get(chunks.size() - 1);
                    barra.setValue(ultimo);
                    barra.setString("Importando " + ultimo + " de " + archivos.length + "...");
                }

                @Override
                protected void done() {
                    try {
                        int importados = get();
                        dialogo.dispose();
                        cargarContenido(carpetaActual);
                        setEstado("  " + importados + " archivo(s) importados.");
                        JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                importados + " archivo(s) importados correctamente.",
                                "Importación completa", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        dialogo.dispose();
                        mostrarError("Error al finalizar importación", ex.getMessage());
                    }
                }
            };

        worker.execute();

    } catch (IllegalStateException e) {
        mostrarAdvertencia(e.getMessage());
    } catch (Exception e) {
        mostrarError("Error al importar", e.getMessage());
    }
}
    
    private void accionEliminar() {
    try {
        int[] filas = tablaArchivos.getSelectedRows();
        if (filas.length == 0) throw new IllegalStateException("Selecciona un archivo o carpeta para eliminar.");

        String mensaje = filas.length == 1
                ? "¿Eliminar \"" + limpiarNombre((String) modeloTabla.getValueAt(filas[0], 0)) + "\"?"
                : "¿Eliminar " + filas.length + " elementos seleccionados?";

        int confirm = JOptionPane.showConfirmDialog(this,
                mensaje + "\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        int eliminados = 0;
        List<String> errores = new ArrayList<>();

        for (int fila : filas) {
            try {
                String nombre = limpiarNombre((String) modeloTabla.getValueAt(fila, 0));
                File f = new File(carpetaActual, nombre);

                if (!f.exists()) throw new Exception("No existe: " + nombre);

                if (f.isDirectory()) {
                    eliminarCarpetaRecursivo(f);
                } else {
                    if (!f.delete()) throw new Exception("No se pudo eliminar: " + nombre);
                }
                eliminados++;

            } catch (Exception ex) {
                errores.add(ex.getMessage());
            }
        }

        cargarContenido(carpetaActual);
        if (nodoActual != null) arbolDirectorios.recargarNodo(nodoActual);

        if (errores.isEmpty()) {
            setEstado("  " + eliminados + " elemento(s) eliminados.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Algunos elementos no se pudieron eliminar:\n" + String.join("\n", errores),
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

    } catch (IllegalStateException e) {
        mostrarAdvertencia(e.getMessage());
    } catch (Exception e) {
        mostrarError("Error al eliminar", e.getMessage());
    }
}

    private void eliminarCarpetaRecursivo(File carpeta) throws Exception {
        File[] contenido = carpeta.listFiles();
        if (contenido != null) {
            for (File f : contenido) {
                if (f.isDirectory()) {
                    eliminarCarpetaRecursivo(f);
                } else {
                    if (!f.delete()) throw new Exception("No se pudo eliminar: " + f.getName());
                }
            }
        }
        if (!carpeta.delete()) throw new Exception("No se pudo eliminar carpeta: " + carpeta.getName());
    }

    private void navegarAtras() {
        try {
            if (posHistorial <= 0) return;
            posHistorial--;
            carpetaActual = historial.get(posHistorial);
            actualizarLabelRuta();
            listaArchivos.limpiar();
            for (File f : gestor.listarContenido(carpetaActual)) listaArchivos.agregar(f);
            actualizarTabla();
            setEstado("  " + listaArchivos.getTamanio() + " elementos");
        } catch (Exception e) {
            mostrarError("Error al navegar atrás", e.getMessage());
        }
    }

    private void navegarAdelante() {
        try {
            if (posHistorial >= historial.size() - 1) return;
            posHistorial++;
            carpetaActual = historial.get(posHistorial);
            actualizarLabelRuta();
            listaArchivos.limpiar();
            for (File f : gestor.listarContenido(carpetaActual)) listaArchivos.agregar(f);
            actualizarTabla();
            setEstado("  " + listaArchivos.getTamanio() + " elementos");
        } catch (Exception e) {
            mostrarError("Error al navegar adelante", e.getMessage());
        }
    }

    private void setEstado(String texto) {
        labelEstado.setText(texto);
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
        System.err.println("[ERROR] " + titulo + ": " + mensaje);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private String limpiarNombre(String nombre) {
        if (nombre == null) return "";
        return nombre.replaceAll("^[^\\w\\-.()áéíóúÁÉÍÓÚñÑ]+", "").trim();
    }

    private String obtenerIcono(String nombre) {
        String n = nombre.toLowerCase();
        if (n.matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) return "🖼 ";
        if (n.matches(".*\\.(mp3|wav|ogg|flac)$"))     return "🎵 ";
        if (n.matches(".*\\.(pdf|docx|doc|odt|txt)$")) return "📄 ";
        return "📄 ";
    }

    private String obtenerTipo(String nombre) {
        int p = nombre.lastIndexOf('.');
        if (p >= 0) return "Documento " + nombre.substring(p).toUpperCase();
        return "Archivo";
    }

    private String formatearTamanio(long bytes) {
        if (bytes < 1024)             return bytes + " B";
        if (bytes < 1024 * 1024)      return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }

    private JButton crearBotonAccion(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        btn.setBackground(fondo);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fondo.darker()),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(fondo.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(fondo); }
        });
        return btn;
    }

    private JButton crearBotonFlecha(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Tahoma", Font.BOLD, 11));
        btn.setBackground(new Color(170, 195, 230));
        btn.setForeground(new Color(30, 30, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(130, 160, 210)),
                BorderFactory.createEmptyBorder(2, 9, 2, 9)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
    

private File crearEstructuraRaiz() {
    File raiz = new File(System.getProperty("user.home"), "PollitoRaiz");

    if (raiz.exists()) return raiz; 

    try {
        raiz.mkdir();

        File documentos = new File(raiz, "Documentos");
        documentos.mkdir();
        crearArchivoTexto(new File(documentos, "tesnica_utadoc.txt"), "Documento tecnico UNITEC");
        crearArchivoTexto(new File(documentos, "marcos.txt"),         "Notas de marcos");
        crearArchivoTexto(new File(documentos, "script.txt"),         "Script del proyecto");
        crearArchivoTexto(new File(documentos, "tema.txt"),           "Descripcion del tema");
        crearArchivoTexto(new File(documentos, "reporte.pdf"),        "Reporte final");
        crearArchivoTexto(new File(documentos, "tarea.docx"),         "Tarea entregable");

        File imagenes = new File(raiz, "Imagenes");
        imagenes.mkdir();

        File musica = new File(raiz, "Musica");
        musica.mkdir();

        File descargas = new File(raiz, "Descargas");
        descargas.mkdir();
        crearArchivoTexto(new File(descargas, "apuntes.txt"),    "texto");
        crearArchivoTexto(new File(descargas, "foto_viaje.jpg"), "imagen");
        crearArchivoTexto(new File(descargas, "musica.mp3"),     "audio");
        crearArchivoTexto(new File(descargas, "manual.pdf"),     "documento");
        crearArchivoTexto(new File(descargas, "portada.png"),    "imagen");

        File trabajos = new File(raiz, "Trabajos");
        trabajos.mkdir();
        crearArchivoTexto(new File(trabajos, "trabajo1.txt"),  "Trabajo numero 1");
        crearArchivoTexto(new File(trabajos, "trabajo2.docx"), "Trabajo numero 2");
        crearArchivoTexto(new File(trabajos, "trabajo3.pdf"),  "Trabajo numero 3");

    } catch (Exception e) {
        System.err.println("Error al crear estructura raíz: " + e.getMessage());
    }

    return raiz;
}

    private void crearArchivoTexto(File archivo, String contenido) {
        try (java.io.FileWriter fw = new java.io.FileWriter(archivo)) {
            fw.write(contenido);
        } catch (Exception e) {
            System.err.println("Error al crear archivo " + archivo.getName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new VentanaPrincipal().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error fatal al iniciar:\n" + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}