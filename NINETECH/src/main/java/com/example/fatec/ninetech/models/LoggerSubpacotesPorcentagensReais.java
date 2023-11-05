package com.example.fatec.ninetech.models;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "logger_subpacotes_porcentagens_reais")
public class LoggerSubpacotesPorcentagensReais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "subpacote_id")
    private Subpacotes subpacotes;

    private LocalDate data;
    private Double porcentagem;

    public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

    public Subpacotes getSubpacotes() {
		return subpacotes;
	}

	public void setSubpacotes(Subpacotes subpacotes) {
		this.subpacotes = subpacotes;
	}

    public LocalDate getData(){
        return data;
    }

    public void setData(LocalDate localDate){
        this.data = localDate;
    }

    public Double getPorcentagem(){
        return porcentagem;
    }

    public void setPorcentagem(Double porcentagem){
        this.porcentagem = porcentagem;
    }

}
