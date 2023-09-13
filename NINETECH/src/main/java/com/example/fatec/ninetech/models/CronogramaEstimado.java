package com.example.fatec.ninetech.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ForeignKey;

@Entity
public class CronogramaEstimado {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
    private Integer mes;
	
	@Column
    private List<Integer> porcentagem;
	
	@OneToOne
	@JoinColumn(name = "projeto_id")
	private Projeto projeto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public List<Integer> getPorcentagens() {
		return porcentagem;
	}

	public void setPorcentagens(List<Integer> porcentagem) {
		this.porcentagem = porcentagem;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	
}
