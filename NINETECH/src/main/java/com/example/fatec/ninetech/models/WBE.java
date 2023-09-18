package com.example.fatec.ninetech.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;


@Entity
public class WBE {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long wbe_id;

	@Column
	private String wbe;
	@Column
	private Double valor;
	@Column
	private Double hh;
	@Column
	private String lider_de_projeto_nome;

	@ManyToOne
	@JoinColumn(name = "projeto_id")
	private Projeto projeto;
	
	public void autualizarLiderProjetoNome(String novoNome) {
		this.lider_de_projeto_nome = novoNome;
	}
	
	public Long getId() {
		return wbe_id;
	}

	public String getWbe() {
		return wbe;
	}

	public void setWbe(String wbs) {
		this.wbe = wbs;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getHh() {
		return hh;
	}

	public void setHh(Double hh) {
		this.hh = hh;
	}

	public String getLider_de_projeto_nome() {
		return lider_de_projeto_nome;
	}

	public void setLider_de_projeto_nome(String lider_de_projeto_nome) {
		this.lider_de_projeto_nome = lider_de_projeto_nome;
	}
	public WBE(String wbe, Double valor, Double hh, String lider_de_projeto_nome) {
        this.wbe = wbe;
        this.valor = valor;
        this.hh = hh;
        this.lider_de_projeto_nome = lider_de_projeto_nome;
	}
	
	public WBE() {} // Para funcionar o a função delete
	

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

}
