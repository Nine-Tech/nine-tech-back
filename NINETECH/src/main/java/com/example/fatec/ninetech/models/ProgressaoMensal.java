package com.example.fatec.ninetech.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ProgressaoMensal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String peso;
	
	@Column
    private boolean execucao;
	
	@Column
	private Timestamp data;
	
	@ManyToOne
	@JoinColumn(name = "wbe_id")
	private WBE wbe;
	
	public WBE getWbe() {
		return wbe;
	}
	public void setWbe(WBE wbe) {
		this.wbe = wbe;
	}

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