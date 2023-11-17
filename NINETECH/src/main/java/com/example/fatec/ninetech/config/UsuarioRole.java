package com.example.fatec.ninetech.config;

public enum UsuarioRole {
    ENGENHEIRO_CHEFE("ENGENHEIRO_CHEFE"),
    LIDER_DE_PROJETO("LIDER_DE_PROJETO");
    
    private String role;
    
    UsuarioRole(String role){
        this.role = role;
    }
    
    public String getRole() {
        return role;
    }
    
    public static UsuarioRole fromString(String role) {
        for (UsuarioRole usuarioRole : UsuarioRole.values()) {
            if (usuarioRole.role.equalsIgnoreCase(role)) {
                return usuarioRole;
            }
        }
        return null;
    }
        
}