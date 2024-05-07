package com.ucaldas.mssecurity.Models;

public class Role {
    private String _id;
    private String name;
    private String description;
    private String IdCliente;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String get_id() {
        return _id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getIdCliente(){
        return IdCliente;
    }
    public void setIdCliente(){
        this.IdCliente=IdCliente;
    }
    
}
