/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

/**
 *
 * @author jerem
 */
import java.io.File;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;


public class ArbolDirectorios {

    private DefaultMutableTreeNode raiz;
    private DefaultTreeModel modelo;
    private JTree arbol;

    private static final String NODO_FANTASMA = "cargando...";

    public ArbolDirectorios(File carpetaRaiz) {
        try {
            if (carpetaRaiz == null || !carpetaRaiz.exists()) {
                throw new IllegalArgumentException("La carpeta raíz no existe.");
            }

            raiz = new DefaultMutableTreeNode(new ArchivoNodo(carpetaRaiz));
            modelo = new DefaultTreeModel(raiz);
            arbol = new JTree(modelo);
            arbol.setRootVisible(true);
            arbol.setShowsRootHandles(true);

            agregarHijosDirectos(raiz, carpetaRaiz);

            configurarRenderer();
            configurarLazy();

        } catch (IllegalArgumentException e) {
            System.err.println("Error al iniciar árbol: " + e.getMessage());
            File home = new File(System.getProperty("user.home"));
            raiz = new DefaultMutableTreeNode(new ArchivoNodo(home));
            modelo = new DefaultTreeModel(raiz);
            arbol = new JTree(modelo);
        }
    }

    private void agregarHijosDirectos(DefaultMutableTreeNode nodo, File carpeta) {
        try {
            File[] hijos = carpeta.listFiles();
            if (hijos == null) return;

            for (File hijo : hijos) {
                if (hijo.isDirectory()) {
                    DefaultMutableTreeNode nodoHijo =
                            new DefaultMutableTreeNode(new ArchivoNodo(hijo));
                    nodo.add(nodoHijo);

                    if (tieneCarpetasHijas(hijo)) {
                        nodoHijo.add(new DefaultMutableTreeNode(NODO_FANTASMA));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar hijos de " + carpeta.getName() + ": " + e.getMessage());
        }
    }

    private boolean tieneCarpetasHijas(File carpeta) {
        try {
            File[] hijos = carpeta.listFiles();
            if (hijos == null) return false;
            for (File f : hijos) {
                if (f.isDirectory()) return true;
            }
        } catch (Exception e) {
            System.err.println("Error verificando hijos: " + e.getMessage());
        }
        return false;
    }

    private void configurarLazy() {
        arbol.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                try {
                    DefaultMutableTreeNode nodo =
                            (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

                    if (nodo.getChildCount() == 1) {
                        DefaultMutableTreeNode primerHijo =
                                (DefaultMutableTreeNode) nodo.getChildAt(0);
                        if (NODO_FANTASMA.equals(primerHijo.getUserObject())) {
                            nodo.removeAllChildren();
                            File carpeta = ((ArchivoNodo) nodo.getUserObject()).getArchivo();
                            agregarHijosDirectos(nodo, carpeta);
                            modelo.reload(nodo);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error en expansión lazy: " + e.getMessage());
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {
            }
        });
    }

    public void recargarNodo(DefaultMutableTreeNode nodo) {
        try {
            if (nodo == null) throw new IllegalArgumentException("El nodo no puede ser null.");
            File carpeta = ((ArchivoNodo) nodo.getUserObject()).getArchivo();
            nodo.removeAllChildren();
            agregarHijosDirectos(nodo, carpeta);
            modelo.reload(nodo);
        } catch (Exception e) {
            System.err.println("Error al recargar nodo: " + e.getMessage());
        }
    }

    public void agregarNodo(DefaultMutableTreeNode padre, File nuevaCarpeta) {
        try {
            if (padre == null) throw new IllegalArgumentException("El nodo padre no puede ser null.");
            if (nuevaCarpeta == null || !nuevaCarpeta.exists()) {
                throw new IllegalArgumentException("La carpeta no existe.");
            }
            DefaultMutableTreeNode nuevo =
                    new DefaultMutableTreeNode(new ArchivoNodo(nuevaCarpeta));
            if (tieneCarpetasHijas(nuevaCarpeta)) {
                nuevo.add(new DefaultMutableTreeNode(NODO_FANTASMA));
            }
            padre.add(nuevo);
            modelo.reload(padre);
        } catch (Exception e) {
            System.err.println("Error al agregar nodo: " + e.getMessage());
        }
    }

    private void configurarRenderer() {
        arbol.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public java.awt.Component getTreeCellRendererComponent(
                    JTree tree, Object value, boolean sel, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(
                        tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    Object obj = ((DefaultMutableTreeNode) value).getUserObject();
                    if (obj instanceof ArchivoNodo) {
                        setText(((ArchivoNodo) obj).toString());
                    } else if (NODO_FANTASMA.equals(obj)) {
                        setText(""); 
                    }
                }
                return this;
            }
        });
    }

    public JTree getArbol()            { return arbol; }
    public DefaultTreeModel getModelo(){ return modelo; }

    public static class ArchivoNodo {
        private File archivo;

        public ArchivoNodo(File archivo) {
            this.archivo = archivo;
        }

        public File getArchivo() { return archivo; }

        @Override
        public String toString() {
            if (archivo.getParentFile() == null) return archivo.getAbsolutePath();
            return archivo.getName();
        }
    }
}