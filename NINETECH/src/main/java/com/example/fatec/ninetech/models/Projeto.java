package com.example.fatec.ninetech.models;

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
	
	@ManyToOne
	@JoinColumn(name = "engenheiro_chefe_id")
	private EngenheiroChefe engenheiroChefe;
	
	@ManyToOne
	@JoinColumn(name = "lider_de_projeto_id")
	private LiderDeProjeto liderDeProjeto;
}
