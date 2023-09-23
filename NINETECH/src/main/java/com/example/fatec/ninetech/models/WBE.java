package com.example.fatec.ninetech.models;

import java.util.List;

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
	private Long wbe_id;

	@Column
	private String wbe;
	@Column
	private Double valor;
	@Column
	private Double hh;

	@ManyToOne
	@JoinColumn(name = "projeto_id")
	private Projeto projeto;
	
	@ManyToOne
	@JoinColumn(name = "lider_de_projeto_id")
	private LiderDeProjeto liderDeProjeto;
	
	@ManyToMany(mappedBy = "wbes")
	    private List<CronogramaEstimado> cronogramasEstimados;
	
	public LiderDeProjeto getLiderDeProjeto() {
		return liderDeProjeto;
	}

	public void setLiderDeProjeto(LiderDeProjeto liderDeProjeto) {
		this.liderDeProjeto = liderDeProjeto;
	}
	
    public List<CronogramaEstimado> getCronogramasEstimados() {
        return cronogramasEstimados;
    }

    public void setCronogramasEstimados(List<CronogramaEstimado> cronogramasEstimados) {
        this.cronogramasEstimados = cronogramasEstimados;
    }

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
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
	
	public WBE() {} // Para funcionar o a função delete

	@Override
	public String toString() {
	    return "WBE [id=" + wbe_id + ", wbe=" + wbe + ", valor=" + valor + ", hh=" + hh + "]";
	}
	
}
