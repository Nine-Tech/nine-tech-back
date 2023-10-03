package com.example.fatec.ninetech.config;

public enum UsuarioRole {
    ENGENHEIRO_CHEFE("ENGENHEIRO_CHEFE"),
    LIDER_DE_PROJETO_1("LIDER_DE_PROJETO_1"),
    LIDER_DE_PROJETO_2("LIDER_DE_PROJETO_2");
    
    private String role;
    
    UsuarioRole(String role){
        this.role = role;
    }
    
    public String getRole() {
        return role;
    }
}