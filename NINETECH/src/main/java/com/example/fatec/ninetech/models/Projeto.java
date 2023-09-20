package com.example.fatec.ninetech.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Projeto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projeto_id;
	
	@Column
	private String nome;
	
	@Column
	private LocalDate data_inicio;
	
	@Column
	private LocalDate data_final;
	
	@ManyToOne
	@JoinColumn(name = "engenheiro_chefe_id")
	private EngenheiroChefe engenheiroChefe;
	
	@ManyToOne
	@JoinColumn(name = "lider_de_projeto_id")
	private LiderDeProjeto liderDeProjeto;

	public Long getId() {
		return projeto_id;
	}

	public void setId(Long id) {
		this.projeto_id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public EngenheiroChefe getEngenheiroChefe() {
		return engenheiroChefe;
	}

	public void setEngenheiroChefe(EngenheiroChefe engenheiroChefe) {
		this.engenheiroChefe = engenheiroChefe;
	}

	public LiderDeProjeto getLiderDeProjeto() {
		return liderDeProjeto;
	}

	public void setLiderDeProjeto(LiderDeProjeto liderDeProjeto) {
		this.liderDeProjeto = liderDeProjeto;
	}

	public LocalDate getData_inicio() {
		return data_inicio;
	}

	public void setData_inicio(LocalDate data_inicio) {
		this.data_inicio = data_inicio;
	}

	public LocalDate getData_final() {
		return data_final;
	}

	public void setData_final(LocalDate data_final) {
		this.data_final = data_final;
	}
	
}
