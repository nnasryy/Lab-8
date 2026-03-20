package lab8_memoria;

import Exceptions.nullSelected;
import Exceptions.UserLogged;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


public class FileExplorer extends JPanel {

    private static String userName = UserLogged.getInstance().getUserLogged().getName();
    private String raizUsuario = "";
    private String setUserRoute = "src\\Z\\Usuarios\\" + userName + "\\";
    private String recycleBin = "";
    private genFondos panelFondo;

    private final Color COLOR_FONDO     = new Color(30, 30, 30);
    private final Color COLOR_PANEL     = new Color(45, 45, 48);
    private final Color COLOR_NARANJA   = new Color(233, 84, 32);
    private final Color COLOR_TEXTO     = new Color(240, 240, 240);
    private final Color COLOR_SELECCION = new Color(233, 84, 32, 100);

    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode raizNodo;

    private JTable fileTable;
    private FileTableModel tableModel;

    private JLabel pathLabel;

    // ── Ordenamiento ─────────────────────────────────────────────────────────
    private JComboBox<String> opcionesOrdenar;
    private String currentDirPath;

    /** Criterio activo; sincronizado con OrdenadorArchivos. */
    private SortCriteria sortCriteria = SortCriteria.NAME;

    /** Instancia del ordenador que ejecuta Bubble Sort / Merge Sort. */
    private final OrdenadorArchivos ordenador = new OrdenadorArchivos(sortCriteria);

    // ── Portapapeles ──────────────────────────────────────────────────────────
    private List<File> clipboardFiles  = new ArrayList<>();
    private List<File> organizerFiles  = new ArrayList<>();
    private List<File> papeleraFiles   = new ArrayList<>();
    private boolean isCutOperation     = false;

    JPanel northPanel = new JPanel(new BorderLayout());

    // ─────────────────────────────────────────────────────────────────────────

    public FileExplorer(genFondos panelFondo) {
        setLayout(new BorderLayout(5, 5));
        this.panelFondo = panelFondo;
        setBackground(COLOR_FONDO);

        raizUsuario = getRutaCondicionada();
        recycleBin  = setUserRoute + "Papelera";

        setupFileTree();
        setupContentTable();

        northPanel.setBackground(COLOR_PANEL);
        northPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JToolBar toolBar = setupToolBar();
        northPanel.add(toolBar, BorderLayout.NORTH);

        JPanel panelSort = new JPanel(new BorderLayout());
        panelSort.setBackground(COLOR_PANEL);
        pathLabel = new JLabel("Ruta Actual: " + formatDisplayPath(raizUsuario));
        pathLabel.setForeground(COLOR_TEXTO);
        pathLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        panelSort.add(pathLabel, BorderLayout.WEST);
        setupSortControls(panelSort);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(fileTree),
            new JScrollPane(fileTable)
        );
        splitPane.setDividerLocation(200);
        splitPane.setBackground(COLOR_FONDO);
        splitPane.setBorder(null);
        styleScrollPane((JScrollPane) splitPane.getLeftComponent());
        styleScrollPane((JScrollPane) splitPane.getRightComponent());

        northPanel.add(panelSort, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        File rootFile = new File(raizUsuario);
        currentDirPath = raizUsuario;
        displayContents(rootFile);
    }

    // ─── Helpers de estilo ────────────────────────────────────────────────────

    private void styleScrollPane(JScrollPane scroll) {
        scroll.setBorder(new LineBorder(COLOR_PANEL, 1));
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBackground(COLOR_FONDO);
    }

    private String getRutaCondicionada() {
        String rutaUsuarios = "src\\Z\\Usuarios";
        String nameActual = UserLogged.getInstance().getUserLogged().getName();
        if (UserLogged.getInstance().getUserLogged().isAdmin()) {
            return rutaUsuarios;
        }
        return rutaUsuarios + "\\" + nameActual + "\\";
    }

    private String formatDisplayPath(String userString) {
        int rootIndex = userString.indexOf(raizUsuario);
        if (rootIndex != -1) {
            return userString.substring(rootIndex);
        }
        return userString;
    }

    // ─── Actualización dinámica de barra de herramientas ─────────────────────

    private void actualizarVistaBotones(File carpeta) {
        northPanel.removeAll();
        if (carpeta.getName().equals("Papelera")) {
            northPanel.add(setupPapeleraBar(), BorderLayout.NORTH);
        } else {
            northPanel.add(setupToolBar(), BorderLayout.NORTH);
        }

        JPanel panelSort = new JPanel(new BorderLayout());
        panelSort.setBackground(COLOR_PANEL);
        try {
            pathLabel.setText("Ruta Actual: " + formatDisplayPath(carpeta.getAbsolutePath()));
        } catch (Exception e) { /* ignore */ }
        pathLabel.setForeground(COLOR_TEXTO);
        pathLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        panelSort.add(pathLabel, BorderLayout.WEST);
        setupSortControls(panelSort);
        northPanel.add(panelSort, BorderLayout.CENTER);
        northPanel.revalidate();
        northPanel.repaint();
    }

    // ─── Barra de Papelera ────────────────────────────────────────────────────

    private JToolBar setupPapeleraBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(COLOR_PANEL);
        toolBar.setBorder(new EmptyBorder(5, 5, 5, 5));

        BotonModerno restaurar = new BotonModerno("Restaurar Archivos", COLOR_NARANJA, Color.WHITE);
        restaurar.setToolTipText("Restaura los archivos seleccionados");
        restaurar.addActionListener(e -> restuararFiles());
        restaurar.setPreferredSize(new Dimension(140, 30));
        toolBar.add(restaurar);
        toolBar.addSeparator(new Dimension(10, 0));

        BotonModerno eliminar = new BotonModerno("Eliminar", new Color(200, 50, 50), Color.WHITE);
        eliminar.setToolTipText("Elimina los archivos permanentemente");
        eliminar.addActionListener(e -> borrarFiles());
        eliminar.setPreferredSize(new Dimension(100, 30));
        toolBar.add(eliminar);

        return toolBar;
    }

    // ─── Barra de herramientas principal ─────────────────────────────────────

    private JToolBar setupToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(COLOR_PANEL);
        toolBar.setBorder(new EmptyBorder(5, 5, 5, 5));

        addButtonToToolbar(toolBar, "Nueva Carpeta",
            UIManager.getIcon("FileChooser.newFolderIcon"),
            "Crear una nueva carpeta en la ubicación actual",
            e -> createNewFolder());

        addButtonToToolbar(toolBar, "Nuevo Archivo",
            UIManager.getIcon("FileChooser.getFolderIcon"),
            "Crear un nuevo archivo de texto (.txt) en la ubicación actual",
            e -> createNewFile());

        addButtonToToolbar(toolBar, "Importar", null, "Importar Archivos", e -> ImportFiles());

        toolBar.addSeparator();

        // ── Botón Organizar (ahora usa OrdenadorArchivos.organizar) ───────────
        addButtonToToolbar(toolBar, "Organizar", null,
            "Mueve automáticamente los archivos a subcarpetas según su tipo (Imágenes, Documentos, Música, Otros)",
            e -> organizeFiles());

        addButtonToToolbar(toolBar, "Copiar",
            UIManager.getIcon("Table.copyIcon"),
            "Copia el archivo o carpeta seleccionada",
            e -> copySelectedFiles(false));

        addButtonToToolbar(toolBar, "Pegar",
            UIManager.getIcon("Table.pasteIcon"),
            "Pega los archivos/carpetas copiados/cortados",
            e -> pasteFiles());

        toolBar.addSeparator();

        addButtonToToolbar(toolBar, "Eliminar",
            UIManager.getIcon("InternalFrame.closeIcon"),
            "Mueve el archivo o carpeta a la Papelera",
            e -> deleteSelectedFiles());

        BotonModerno renameBt = new BotonModerno("Renombrar", new Color(60, 60, 60), COLOR_TEXTO);
        renameBt.setPreferredSize(new Dimension(100, 30));
        renameBt.setToolTipText("Cambiar el nombre de archivo o carpeta");
        renameBt.addActionListener(e -> {
            try {
                realizarRenombre();
            } catch (nullSelected ex) {
                JOptionPane.showMessageDialog(panelFondo, ex.getMessage(), "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toolBar.add(renameBt);
        toolBar.add(Box.createHorizontalStrut(5));

        BotonModerno abrirBt = new BotonModerno("Abrir", new Color(60, 60, 60), COLOR_TEXTO);
        abrirBt.setPreferredSize(new Dimension(80, 30));
        abrirBt.setIcon(UIManager.getIcon("InternalFrame.openIcon"));
        abrirBt.setToolTipText("Abrir el archivo seleccionado");
        abrirBt.addActionListener(e -> {
            try {
                openSelectedFile();
            } catch (nullSelected ex) {
                JOptionPane.showMessageDialog(panelFondo, ex.getMessage(), "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toolBar.add(abrirBt);

        return toolBar;
    }

    private void addButtonToToolbar(JToolBar bar, String text, Icon icon, String tooltip,
                                    java.awt.event.ActionListener action) {
        BotonModerno btn = new BotonModerno(text, new Color(60, 60, 60), COLOR_TEXTO);
        if (icon != null) btn.setIcon(icon);
        btn.addActionListener(action);
        if (tooltip != null) btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(110, 30));
        bar.add(btn);
        bar.add(Box.createHorizontalStrut(5));
    }

    // ─── ORGANIZAR (usa OrdenadorArchivos) ───────────────────────────────────

    /**
     * Organiza los archivos del directorio actual clasificándolos en
     * subcarpetas según su tipo usando OrdenadorArchivos.organizar().
     *
     * Ya NO requiere que el usuario seleccione archivos ni carpeta destino;
     * trabaja directamente sobre currentDirPath.
     */
    private void organizeFiles() {
        File dirActual = new File(currentDirPath);

        if (!dirActual.isDirectory()) {
            JOptionPane.showMessageDialog(panelFondo,
                "Selecciona una carpeta para organizar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmación
        int confirm = JOptionPane.showConfirmDialog(panelFondo,
            "<html>Se organizarán los archivos de:<br><b>" + dirActual.getName() + "</b><br><br>" +
            "• Imágenes (.jpg, .png, .gif) → /Imagenes<br>" +
            "• Documentos (.pdf, .docx, .txt) → /Documentos<br>" +
            "• Música (.mp3, .wav) → /Musica<br>" +
            "• Otros → /Otros<br><br>¿Desea continuar?</html>",
            "Organizar archivos",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Delegar al OrdenadorArchivos
        int movidos = ordenador.organizar(dirActual);

        displayContents(dirActual);
        updateTree(dirActual);

        JOptionPane.showMessageDialog(panelFondo,
            movidos + " archivo(s) organizados exitosamente.",
            "Organización completa", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─── Papelera: borrar y restaurar ────────────────────────────────────────

    private void borrarFiles() {
        int[] selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(panelFondo,
                "Selecciona los archivos o carpetas a borrar", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(panelFondo,
            "¿Desea borrar permanentemente los archivos seleccionados?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        papeleraFiles.clear();
        for (int row : selectedRows) {
            papeleraFiles.add((File) tableModel.getValueAt(row, 0));
        }
        boolean success = true;
        for (File f : papeleraFiles) {
            try { borrar(f); } catch (Exception e) { success = false; }
        }
        if (success) {
            displayContents(new File(currentDirPath));
            updateTree(new File(currentDirPath));
            JOptionPane.showMessageDialog(panelFondo, "Elementos eliminados con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void restaurarArchivo(File archivoEnPapelera) {
        File dirDestino = new File(setUserRoute + "Mis Documentos");
        if (!dirDestino.exists() || !dirDestino.isDirectory()) return;
        File destino = new File(dirDestino, archivoEnPapelera.getName());
        archivoEnPapelera.renameTo(destino);
    }

    private void restuararFiles() {
        int[] selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(panelFondo,
                "Selecciona los archivos a restaurar", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(panelFondo,
            "¿Desea restaurar los archivos seleccionados?",
            "Confirmar Restauración", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        papeleraFiles.clear();
        for (int row : selectedRows) {
            papeleraFiles.add((File) tableModel.getValueAt(row, 0));
        }
        boolean success = true;
        for (File f : papeleraFiles) {
            try { restaurarArchivo(f); } catch (Exception e) { success = false; }
        }
        if (success) {
            displayContents(new File(currentDirPath));
            updateTree(new File(currentDirPath));
            JOptionPane.showMessageDialog(panelFondo, "Elementos restaurados con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── ÁRBOL ────────────────────────────────────────────────────────────────

    private void setupFileTree() {
        raizNodo = new DefaultMutableTreeNode(new File(raizUsuario));
        treeModel = new DefaultTreeModel(raizNodo);
        fileTree = new JTree(treeModel);

        fileTree.setCellRenderer(new FileTreeRenderer());
        fileTree.setRootVisible(true);
        fileTree.setBackground(COLOR_FONDO);
        fileTree.setForeground(COLOR_TEXTO);

        populateNode(raizNodo);

        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (node == null) return;
            Object obj = node.getUserObject();
            if (!(obj instanceof File)) return;
            File file = (File) obj;
            if (file.isDirectory()) {
                actualizarVistaBotones(file);
                displayContents(file);
                if (node.getChildCount() == 0 || node.getFirstChild().toString().equals("Cargando...")) {
                    populateNode(node);
                }
            }
        });
    }

    private void populateNode(DefaultMutableTreeNode parentNode) {
        parentNode.removeAllChildren();
        File parentFile = (File) parentNode.getUserObject();
        File[] children = parentFile.listFiles(File::isDirectory);
        String currentUsername = UserLogged.getInstance().getUserLogged().getName();

        if (children != null) {
            Arrays.sort(children, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            for (File child : children) {
                if (child.getAbsolutePath().equals(recycleBin)) continue;
                if (!UserLogged.getInstance().getUserLogged().isAdmin()
                    && parentFile.getName().equalsIgnoreCase("Usuarios")
                    && !child.getName().equalsIgnoreCase(currentUsername)) continue;

                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                File[] grandChildren = child.listFiles(File::isDirectory);
                if (grandChildren != null && grandChildren.length > 0) {
                    childNode.add(new DefaultMutableTreeNode("Cargando..."));
                }
                treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            }
        }
    }

    private void updateTree(File parentDir) {
        DefaultMutableTreeNode node = findNode(raizNodo, parentDir);
        if (node != null) {
            populateNode(node);
            treeModel.nodeStructureChanged(node);
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode startNode, File targetFile) {
        if (startNode.getUserObject().equals(targetFile)) return startNode;
        for (int i = 0; i < startNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) startNode.getChildAt(i);
            if (child.getUserObject() instanceof File) {
                DefaultMutableTreeNode found = findNode(child, targetFile);
                if (found != null) return found;
            }
        }
        return null;
    }

    // ─── TABLA ────────────────────────────────────────────────────────────────

    private void setupContentTable() {
        tableModel = new FileTableModel();
        fileTable = new JTable(tableModel);

        fileTable.setBackground(COLOR_FONDO);
        fileTable.setForeground(COLOR_TEXTO);
        fileTable.setSelectionBackground(COLOR_SELECCION);
        fileTable.setSelectionForeground(Color.WHITE);
        fileTable.setGridColor(new Color(50, 50, 50));
        fileTable.setShowGrid(false);
        fileTable.setShowHorizontalLines(true);
        fileTable.setRowHeight(30);

        DarkHeaderRenderer headerRenderer = new DarkHeaderRenderer();
        for (int i = 0; i < fileTable.getColumnModel().getColumnCount(); i++) {
            fileTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        fileTable.getColumnModel().getColumn(0).setCellRenderer(new FileNameRender());
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        fileTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        fileTable.getTableHeader().setReorderingAllowed(false);

        // Click en cabecera → cambiar criterio de sort
        fileTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = fileTable.columnAtPoint(e.getPoint());
                SortCriteria nuevo;
                switch (col) {
                    case 1:  nuevo = SortCriteria.DATE; break;
                    case 2:  nuevo = SortCriteria.TYPE; break;
                    case 3:  nuevo = SortCriteria.SIZE; break;
                    default: nuevo = SortCriteria.NAME; break;
                }
                sortCriteria = nuevo;
                ordenador.setCriterio(nuevo);
                if (col >= 0 && col < 4) opcionesOrdenar.setSelectedIndex(col);
                actualizarVistaBotones(new File(currentDirPath));
                displayContents(new File(currentDirPath));
            }
        });

        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Doble clic → navegar
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = fileTable.getSelectedRow();
                    if (row != -1) {
                        File file = (File) tableModel.getValueAt(
                            fileTable.convertRowIndexToModel(row), 0);
                        if (file.isDirectory()) {
                            displayContents(file);
                            updateTree(file);
                            actualizarVistaBotones(file);
                        }
                    }
                }
            }
        });
    }

    // ─── displayContents usando ListaEnlazadaArchivos ─────────────────────────

    /**
     * Muestra el contenido del directorio en la tabla.
     * Usa ListaEnlazadaArchivos para acumular los archivos y
     * OrdenadorArchivos (Merge Sort) para ordenarlos.
     */
    private void displayContents(File directory) {
        currentDirPath = directory.getAbsolutePath();
        pathLabel.setText("Ruta Actual: " + directory.getAbsolutePath());

        File[] contents = directory.listFiles();
        if (contents == null) {
            tableModel.setFiles(new ArrayList<>());
            return;
        }

        // Separar directorios y archivos en listas enlazadas independientes
        ListaEnlazadaArchivos listaDirectorios = new ListaEnlazadaArchivos();
        ListaEnlazadaArchivos listaArchivos    = new ListaEnlazadaArchivos();

        for (File f : contents) {
            // Ocultar papelera si no estamos en la raíz del usuario
            if (f.getAbsolutePath().equals(recycleBin)
                && !directory.getAbsolutePath().equals(raizUsuario)) continue;

            if (f.isDirectory()) {
                listaDirectorios.agregar(f);
            } else {
                listaArchivos.agregar(f);
            }
        }

        // Ordenar cada lista con Merge Sort (O(n log n))
        ordenador.setCriterio(sortCriteria);
        List<File> dirOrdenados    = ordenador.ordenar(listaDirectorios);
        List<File> filesOrdenados  = ordenador.ordenar(listaArchivos);

        // Combinar: directorios primero, luego archivos
        List<File> resultado = new ArrayList<>();
        resultado.addAll(dirOrdenados);
        resultado.addAll(filesOrdenados);

        tableModel.setFiles(resultado);
    }

    // ─── Controles de ordenamiento ────────────────────────────────────────────

    private void setupSortControls(JPanel panelCambios) {
        JPanel sortPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        sortPanel.setBackground(COLOR_PANEL);
        JLabel lblSort = new JLabel("Ordenar por:");
        lblSort.setForeground(COLOR_TEXTO);

        opcionesOrdenar = new JComboBox<>(new String[]{"Nombre", "Fecha", "Tipo", "Tamaño"});
        opcionesOrdenar.setSelectedItem("Nombre");

        sortPanel.add(lblSort);
        sortPanel.add(opcionesOrdenar);

        opcionesOrdenar.addActionListener(e -> {
            String selected = (String) opcionesOrdenar.getSelectedItem();
            if ("Nombre".equals(selected))  sortCriteria = SortCriteria.NAME;
            else if ("Fecha".equals(selected))   sortCriteria = SortCriteria.DATE;
            else if ("Tamaño".equals(selected))  sortCriteria = SortCriteria.SIZE;
            else if ("Tipo".equals(selected))    sortCriteria = SortCriteria.TYPE;

            ordenador.setCriterio(sortCriteria);
            displayContents(new File(currentDirPath));
        });

        panelCambios.add(sortPanel, BorderLayout.EAST);
    }

    // ─── Operaciones de archivos ──────────────────────────────────────────────

    private String obtenerExtension(File archivo) {
        if (archivo.isDirectory()) return "";
        String nombre = archivo.getName();
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombre.length() - 1) {
            return nombre.substring(ultimoPunto);
        }
        return "";
    }

    private boolean renombrar(File archivoOriginal, String nuevoNombre) {
        File dirPadre = archivoOriginal.getParentFile();
        File nuevoArchivo = new File(dirPadre, nuevoNombre);
        if (nuevoArchivo.exists()) {
            JOptionPane.showMessageDialog(panelFondo, "Ya existe un archivo/directorio con este nombre");
            return false;
        }
        boolean exito = archivoOriginal.renameTo(nuevoArchivo);
        JOptionPane.showMessageDialog(panelFondo,
            exito ? "Archivo renombrado con éxito" : "Error al intentar renombrar el archivo");
        return exito;
    }

    private void realizarRenombre() throws nullSelected {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1) throw new nullSelected("No se ha seleccionado ningún archivo");

        File file = (File) fileTable.getValueAt(selectedRow, 0);
        String extension = obtenerExtension(file);
        String newName = JOptionPane.showInputDialog(panelFondo, "Ingrese el nuevo nombre: ");

        if (newName != null && !newName.trim().isEmpty()) {
            String newNameFinal = newName.trim() + extension;
            if (renombrar(file, newNameFinal)) {
                displayContents(new File(currentDirPath));
            }
        } else if (newName != null) {
            JOptionPane.showMessageDialog(panelFondo, "El nuevo nombre no puede estar vacío");
        }
    }

    private void ImportFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("IMPORTAR ARCHIVOS");
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(panelFondo) == JFileChooser.APPROVE_OPTION) {
            for (File f : chooser.getSelectedFiles()) {
                try {
                    Files.copy(f.toPath(),
                        new File(currentDirPath, f.getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) { e.printStackTrace(); }
            }
            displayContents(new File(currentDirPath));
        }
    }

    private void createNewFolder() {
        String folderName = JOptionPane.showInputDialog(panelFondo,
            "Ingresa el nombre de la nueva carpeta:", "Crear Carpeta", JOptionPane.PLAIN_MESSAGE);
        if (folderName != null && !folderName.trim().isEmpty()) {
            File newFolder = new File(currentDirPath, folderName.trim());
            if (newFolder.exists()) {
                JOptionPane.showMessageDialog(panelFondo, "La Carpeta ya existe", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newFolder.mkdir()) {
                displayContents(new File(currentDirPath));
                updateTree(new File(currentDirPath));
            } else {
                JOptionPane.showMessageDialog(panelFondo, "Error al crear la carpeta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createNewFile() {
        String fileName = JOptionPane.showInputDialog(panelFondo,
            "Ingresa el nombre del nuevo archivo (ej: documento.txt):", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            String finalFileName = fileName.trim();
            if (!finalFileName.contains(".")) finalFileName += ".txt";
            File newFile = new File(currentDirPath, finalFileName);
            if (newFile.exists()) {
                JOptionPane.showMessageDialog(panelFondo, "El archivo ya existe", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if (newFile.createNewFile()) {
                        displayContents(new File(currentDirPath));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(panelFondo,
                        "Error de E/S: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void openSelectedFile() throws nullSelected {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1) throw new nullSelected("No se ha seleccionado ningún archivo");

        File file = (File) fileTable.getValueAt(selectedRow, 0);
        if (file.isDirectory()) {
            JOptionPane.showMessageDialog(panelFondo, "Solamente abrir ARCHIVOS, no directorios",
                "Atención", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nameFile = file.getName().toLowerCase();

        if (nameFile.endsWith(".wav") || nameFile.endsWith(".mp3")) {
            audioPlayer musicPanel = new audioPlayer();
            JInternalFrame frame = createMusicWindow(musicPanel);
            audioLogic logicPlayer = new audioLogic(musicPanel);
            musicPanel.setPlayerExterno(logicPlayer);
            panelFondo.add(frame);
            try {
                frame.setSelected(true);
                logicPlayer.load(file);
                musicPanel.playExterno(file);
            } catch (java.beans.PropertyVetoException ex) { /* ignore */ }

        } else if (nameFile.endsWith(".png") || nameFile.endsWith(".jpg")) {
            VisorImagenes galeriapanel = new VisorImagenes();
            JInternalFrame frame = createGalleryWindow(galeriapanel);
            panelFondo.add(frame);
            try {
                new ImagenesLogic().ImportarImagenesExterno(galeriapanel, file);
                frame.setSelected(true);
            } catch (java.beans.PropertyVetoException ex) { /* ignore */ }

        } else if (nameFile.endsWith(".txt")) {
            TextLogic logictxt = new TextLogic();
            TextoPanel textEditPanel = new TextoPanel();
            JInternalFrame frame = createTextWindow(textEditPanel);
            panelFondo.add(frame);
            try {
                frame.setSelected(true);
                logictxt.abrirExterno(textEditPanel, textEditPanel.getTextPane(), file);
            } catch (java.beans.PropertyVetoException ex) { /* ignore */ }
        } else {
            JOptionPane.showMessageDialog(panelFondo,
                "Extensión de archivo no soportada por el sistema", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void copySelectedFiles(boolean isCut) {
        int[] selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(panelFondo, "Selecciona los archivos o carpetas a copiar",
                "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        clipboardFiles.clear();
        isCutOperation = isCut;
        for (int row : selectedRows) {
            clipboardFiles.add((File) tableModel.getValueAt(row, 0));
        }
        JOptionPane.showMessageDialog(panelFondo,
            selectedRows.length + " elemento(s) preparado(s) para " + (isCut ? "Cortar" : "Copiar") + ".",
            "Portapapeles", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pasteFiles() {
        if (clipboardFiles.isEmpty()) {
            JOptionPane.showMessageDialog(panelFondo, "El portapapeles está vacío", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        File targetDir = new File(currentDirPath);
        if (!targetDir.isDirectory()) {
            JOptionPane.showMessageDialog(panelFondo, "La ubicación de destino no es una carpeta válida",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = true;
        for (File sourceFile : new ArrayList<>(clipboardFiles)) {
            try {
                File destFile = new File(targetDir, sourceFile.getName());
                if (isCutOperation) {
                    Files.move(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else if (sourceFile.isDirectory()) {
                    recursiveCopy(sourceFile.toPath(), destFile.toPath());
                } else {
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                success = false;
                JOptionPane.showMessageDialog(panelFondo,
                    "Error al " + (isCutOperation ? "mover" : "copiar") + " " + sourceFile.getName(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (success) {
            if (isCutOperation) { clipboardFiles.clear(); isCutOperation = false; }
            displayContents(targetDir);
            updateTree(targetDir);
            JOptionPane.showMessageDialog(panelFondo, "Elementos pegados con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void recursiveCopy(Path source, Path dest) throws IOException {
        if (!Files.exists(dest)) Files.createDirectories(dest);
        try (var stream = Files.walk(source)) {
            stream.forEach(sp -> {
                try {
                    Path rel = source.relativize(sp);
                    Path dp = dest.resolve(rel);
                    if (Files.isDirectory(sp)) {
                        if (!Files.exists(dp)) Files.createDirectory(dp);
                    } else {
                        Files.copy(sp, dp, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                } catch (IOException e) {
                    System.err.println("Error copiando " + sp + ": " + e.getMessage());
                }
            });
        }
    }

    private void deleteSelectedFiles() {
        int[] selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(panelFondo, "Selecciona los archivos o carpetas a eliminar.",
                "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<File> toDelete = new ArrayList<>();
        for (int row : selectedRows) toDelete.add((File) tableModel.getValueAt(row, 0));

        int confirm = JOptionPane.showConfirmDialog(panelFondo,
            "¿Mover " + selectedRows.length + " elemento(s) a la Papelera?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        File papelera = new File(recycleBin);
        if (!papelera.exists()) papelera.mkdir();

        boolean success = true;
        for (File file : toDelete) {
            if (file.getAbsolutePath().equals(recycleBin)) {
                JOptionPane.showMessageDialog(panelFondo,
                    "No se puede eliminar la Papelera de Reciclaje", "Error", JOptionPane.ERROR_MESSAGE);
                success = false;
                continue;
            }
            try {
                Files.move(file.toPath(), new File(recycleBin, file.getName()).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                success = false;
            }
        }
        if (success) {
            displayContents(new File(currentDirPath));
            updateTree(new File(currentDirPath));
            JOptionPane.showMessageDialog(panelFondo, "Elementos movidos a la Papelera.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean borrarAux(File mf) {
        if (mf.isDirectory()) for (File arc : mf.listFiles()) borrarAux(arc);
        return mf.delete();
    }

    public boolean borrar(File mifile) {
        if (mifile.isDirectory()) for (File arc : mifile.listFiles()) borrarAux(arc);
        return mifile.delete();
    }

    // ─── Renderers ────────────────────────────────────────────────────────────

    private class FileNameRender extends DefaultTableCellRenderer {
        private final FileSystemView fsv = FileSystemView.getFileSystemView();
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            File file = (File) value;
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setIcon(fsv.getSystemIcon(file));
            setText(fsv.getSystemDisplayName(file));
            if (!isSelected) { setBackground(COLOR_FONDO); setForeground(COLOR_TEXTO); }
            else             { setBackground(COLOR_SELECCION); setForeground(Color.WHITE); }
            return this;
        }
    }

    private class DarkHeaderRenderer extends DefaultTableCellRenderer {
        public DarkHeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(JLabel.LEFT);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setBackground(COLOR_PANEL);
            setForeground(COLOR_NARANJA);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(60, 60, 60)));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText(value.toString());
            return this;
        }
    }

    private class FileTreeRenderer extends DefaultTreeCellRenderer {
        private final FileSystemView fsv = FileSystemView.getFileSystemView();
        public FileTreeRenderer() {
            setBackgroundNonSelectionColor(COLOR_FONDO);
            setTextNonSelectionColor(COLOR_TEXTO);
            setBackgroundSelectionColor(COLOR_NARANJA);
            setTextSelectionColor(Color.WHITE);
        }
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();
            if (obj instanceof File) {
                setText(fsv.getSystemDisplayName((File) obj));
                setIcon(fsv.getSystemIcon((File) obj));
            } else { setIcon(null); }
            return this;
        }
    }

    // ─── Modelo de tabla ──────────────────────────────────────────────────────

    private class FileTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nombre", "Ultima Modificacion", "Tipo", "Tamaño"};
        private List<File> fileList = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        private final FileSystemView fsv = FileSystemView.getFileSystemView();

        @Override public int getRowCount()    { return fileList.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int col) { return columnNames[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            File file = fileList.get(rowIndex);
            switch (columnIndex) {
                case 0: return file;
                case 1: return dateFormat.format(file.lastModified());
                case 2: return file.isDirectory() ? "Carpeta de Archivos" : fsv.getSystemTypeDescription(file);
                case 3: return file.isDirectory() ? "" : formatSize(file.length());
                default: return null;
            }
        }

        public void setFiles(List<File> files) { this.fileList = files; fireTableDataChanged(); }

        private String formatSize(long size) {
            if (size <= 0) return "0 bytes";
            final String[] units = {"bytes", "KB", "MB", "GB", "TB"};
            int dg = (int) (Math.log10(size) / Math.log10(1024));
            return String.format("%.1f %s", size / Math.pow(1024, dg), units[dg]);
        }
    }

    // ─── Ventanas internas ────────────────────────────────────────────────────

    private JInternalFrame createMusicWindow(audioPlayer player) {
        JInternalFrame frame = new JInternalFrame("MUSIC INSANO", true, true, true, true);
        frame.add(player, BorderLayout.CENTER);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                audioLogic p = player.getPlayer();
                if (p.isPlaying()) player.stopPlayback();
                frame.dispose();
            }
        });
        frame.setSize(650, 450);
        frame.setLocation(100, 100);
        frame.setVisible(true);
        return frame;
    }

    private JInternalFrame createGalleryWindow(VisorImagenes galeriapanel) {
        JInternalFrame frame = new JInternalFrame("GALERIA INSANA", true, true, true, true);
        frame.add(galeriapanel, BorderLayout.CENTER);
        frame.setSize(900, 600);
        frame.setLocation(100, 100);
        frame.setVisible(true);
        return frame;
    }

    private JInternalFrame createTextWindow(TextoPanel panel) {
        JInternalFrame frame = new JInternalFrame("EDITOR DE TEXTO INSANO", true, true, true, true);
        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(900, 600);
        frame.setLocation(50, 50);
        frame.setVisible(true);
        return frame;
    }

    // ─── Botón con estilo ─────────────────────────────────────────────────────

    private class BotonModerno extends JButton {
        private final Color colorBase;
        private final Color colorHover;

        public BotonModerno(String text, Color bg, Color fg) {
            super(text);
            this.colorBase  = bg;
            this.colorHover = bg.brighter();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { setBackground(colorHover); }
                @Override public void mouseExited(MouseEvent e)  { setBackground(colorBase);  }
            });
            setBackground(colorBase);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}