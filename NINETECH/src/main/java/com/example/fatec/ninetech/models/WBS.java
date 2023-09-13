package com.example.fatec.ninetech.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class WBS {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
    private String wbs;
	@Column
    private Double valor;
	@Column
    private Double hh;
	
	@OneToOne
	@JoinColumn(name = "projeto_id")
	private Projeto projeto;
	
	public Long getId() {
		return id;
	}
	public String getWbs() {
		return wbs;
	}
	public void setWbs(String wbs) {
		this.wbs = wbs;
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
