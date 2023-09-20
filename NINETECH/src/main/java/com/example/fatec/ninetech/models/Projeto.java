package com.example.fatec.ninetech.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonGetter;

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
	private Long id;
	
	@Column
	private String nome;
	
	@Column
	private LocalDate data_inicio;
	
	@Column
	private LocalDate data_final;
	
	@ManyToOne
	@JoinColumn(name = "engenheiro_chefe_id")
	private EngenheiroChefe engenheiroChefe;
	
	@ManyToOne
	@JoinColumn(name = "lider_de_projeto_id")
	private LiderDeProjeto liderDeProjeto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public EngenheiroChefe getEngenheiroChefe() {
		return engenheiroChefe;
	}

	public void setEngenheiroChefe(EngenheiroChefe engenheiroChefe) {
		this.engenheiroChefe = engenheiroChefe;
	}

	public LiderDeProjeto getLiderDeProjeto() {
		return liderDeProjeto;
	}

	public void setLiderDeProjeto(LiderDeProjeto liderDeProjeto) {
		this.liderDeProjeto = liderDeProjeto;
	}

    public LocalDate getData_inicio() {
        return data_inicio;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (data_inicio != null) {
            stringBuilder.append("data_inicio: ").append(data_inicio.format(formatter)).append("\n");
        } else {
            stringBuilder.append("data_inicio: ").append("null").append("\n"); // Or handle the case where data_inicio is null
        }
        
        if (data_final != null) {
            stringBuilder.append("data_final: ").append(data_final.format(formatter)).append("\n");
        } else {
            stringBuilder.append("data_final: ").append("null").append("\n"); // Or handle the case where data_final is null
        }

        return stringBuilder.toString();
    }

    @JsonGetter("data_inicio")
    public String getDataInicioAsString() {
        return getData_inicio().toString();
    }

    @JsonGetter("data_final")
    public String getDataFinalAsString() {
        return getData_final().toString();
    }
    
	public void setData_inicio(LocalDate data_inicio) {
		this.data_inicio = data_inicio;
	}

	public LocalDate getData_final() {
		return data_final;
	}

	public void setData_final(LocalDate data_final) {
		this.data_final = data_final;
	}
	
}
