package com.example.fatec.ninetech.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Subpacotes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "pacotes_id")
	private Pacotes pacotes;

	@ManyToOne
	@JoinColumn(name = "lider_de_projeto_id")
	private LiderDeProjeto liderDeProjeto;
	
	@Column
	private double porcentagem;
	
	@Column
	private double valor_total;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Pacotes getPacotes() {
		return pacotes;
	}

	public void setPacotes(Pacotes pacotes) {
		this.pacotes = pacotes;
	}

	public double getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(double porcentagem) {
		this.porcentagem = porcentagem;
	}

	public double getValor_total() {
		return valor_total;
	}
	
	public LiderDeProjeto getLiderDeProjeto() {
		return liderDeProjeto;
	}

	public void setLiderDeProjeto(LiderDeProjeto liderDeProjeto) {
		this.liderDeProjeto = liderDeProjeto;
	}

	public void setValor_total(double valor_total) {
		this.valor_total = valor_total;
	}
}