package com.example.fatec.ninetech.config;

public enum UsuarioRole {
	ENGENHEIROCHEFE("engenheirochefe"),
	LIDERDEPROJETO("liderdeprojeto");
	
	private String role;
	
	UsuarioRole(String role){
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
	
	
}
