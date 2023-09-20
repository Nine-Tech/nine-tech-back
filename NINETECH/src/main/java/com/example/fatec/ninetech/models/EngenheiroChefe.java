package com.example.fatec.ninetech.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "engenheiro_chefe")
public class EngenheiroChefe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long engenheiro_chefe_id;
	
	@Column
	private String nome;

	public Long getEngenheiro_chefe_id() {
		return engenheiro_chefe_id;
	}
	public void setEngenheiro_chefe_id(Long engenheiro_chefe_id) {
		this.engenheiro_chefe_id = engenheiro_chefe_id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

}
