/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exceptions;

/**
 *
 * @author nasry
 */
public class Usuario {

    private String name;
    private String password;
    private boolean admin;

    public Usuario(String name, String password, boolean admin) {
        this.name = name;
        this.password = password;
        this.admin = admin;
    }

    public Usuario(String name, String password) {
        this(name, password, false);
    }

    public String getName(){
        return name;
    }
    
    public void setName(String name){
    this.name = name;
    }
    
    public String getPassword(){
    return password;
    }
    
    public void setPassword(){
    this.password = password;
    }
    
    public boolean isAdmin(){
    return admin;
    }
    
   public boolean verificarPassword(String passwordIngresada) {
        if (this.password == null || passwordIngresada == null) return false;
        return this.password.equals(passwordIngresada);
    }
 
    @Override
    public String toString() {
        return "Usuario{name='" + name + "', admin=" + admin + "}";
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario)) return false;
        Usuario otro = (Usuario) obj;
        return name != null && name.equalsIgnoreCase(otro.name);
    }
 
    @Override
    public int hashCode() {
        return name != null ? name.toLowerCase().hashCode() : 0;
    }
}
