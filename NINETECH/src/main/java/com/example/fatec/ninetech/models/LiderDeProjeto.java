package com.example.fatec.ninetech.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lider_de_projeto")
public class LiderDeProjeto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lider_de_projeto_id;

	public Long getLider_de_projeto_id() {
		return lider_de_projeto_id;
	}
	public void setLider_de_projeto_id(Long lider_de_projeto_id) {
		this.lider_de_projeto_id = lider_de_projeto_id;
	}

	@Column
	private String nome;
	
	@Column
	private String senha;

	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public LiderDeProjeto findByNome(String novoNome) {
		// TODO Auto-generated method stub
		return null;
	}

}
