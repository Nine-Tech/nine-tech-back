package com.example.fatec.ninetech.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class WBE {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Boolean filho;
	
	public Boolean getFilho() {
		return filho;
	}

	public void setFilho(Boolean filho) {
		this.filho = filho;
	}
	
	@ManyToOne
	@JsonIgnore //ignorando valores do objeto pai inteiro
	@JoinColumn(name = "wbe_pai_id")
	private WBE wbePai;

	public WBE getWbePai() {
		return wbePai;
	}

	public void setWbePai(WBE wbePai) {
		this.wbePai = wbePai;
	}

	@Column
	private String wbe;
	@Column
	private Double valor;
	@Column
	private Double hh;
	@Column
	private Double material;

	@ManyToOne
	@JoinColumn(name = "projeto_id")
	private Projeto projeto;

	@ManyToOne
	@JoinColumn(name = "lider_de_projeto_id")
	private LiderDeProjeto liderDeProjeto;

	
	@ManyToOne
	@JoinColumn(name = "id_wbe")
	private ProgressaoMensal progressaoMensal;
	
	public Double getMaterial() {
		return material;
	}

	public void setMaterial(Double material) {
		this.material = material;
	}

	public LiderDeProjeto getLiderDeProjeto() {
		return liderDeProjeto;
	}

	public void setLiderDeProjeto(LiderDeProjeto liderDeProjeto) {
		this.liderDeProjeto = liderDeProjeto;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Long getId() {
		return id;
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

}
