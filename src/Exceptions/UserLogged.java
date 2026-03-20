/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exceptions;

/**
 *
 * @author nasry
 */
public class UserLogged {

    private static UserLogged instance;
    private Usuario userLogged;

    private UserLogged() {
        this.userLogged = new Usuario("invitado", "", false);
    }

    public static UserLogged getInstance() {

        if (instance == null) {
            instance = new UserLogged();
        }
        return instance;
    }

    public Usuario getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario de sesión no puede ser null");
        }
        this.userLogged = usuario;
    }
 
    public boolean login(String nombre, String password) {
        Usuario[] usuariosRegistrados = {
            new Usuario("admin",  "admin123", true),
            new Usuario("david",  "1234",     false),
            new Usuario("enequi", "pass",     false),
            new Usuario("marcos", "pass",     false)
        };
 
        for (Usuario u : usuariosRegistrados) {
            if (u.getName().equalsIgnoreCase(nombre) && u.verificarPassword(password)) {
                this.userLogged = u;
                return true;
            }
        }
        return false;
    }
 
    public void logout() {
        this.userLogged = new Usuario("invitado", "", false);
    }
 
    public boolean haySesionActiva() {
        return userLogged != null && !"invitado".equals(userLogged.getName());
    }
 
    @Override
    public String toString() {
        return "UserLogged{usuario=" + (userLogged != null ? userLogged.toString() : "null") + "}";
    }
}
