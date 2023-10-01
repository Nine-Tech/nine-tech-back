package com.example.fatec.ninetech.models;

import java.sql.Timestamp;

import org.hibernate.mapping.ForeignKey;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ProgressaoMensal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String peso;
	
	@Column
    private boolean execucao;
	//@Column
	//private String execucao;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
	@Column
	private Timestamp data;
	
	/**
	@ManyToOne
	@JoinColumn(name = "wbe_id")
	*/
	private ForeignKey id_wbe;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPeso() {
		return peso;
	}
	public void setPeso(String peso) {
		this.peso = peso;
	}
	public boolean getExecucao() {
		return execucao;
	}
	public void setExecucao(boolean execucao) {
		this.execucao = execucao;
	}
	public Timestamp getData() {
		return data;
	}
	public void setData(Timestamp data) {
		this.data = data;
	}
	
	
}