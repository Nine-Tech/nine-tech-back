package com.example.fatec.ninetech.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cronograma_projeto_estimado")
public class CronogramaProjetoEstimado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    private Integer mes;
    private Double porcentagem;

    public CronogramaProjetoEstimado() {
    }

    public CronogramaProjetoEstimado(Integer mes, Double porcentagem, Projeto projeto) {
        this.mes = mes;
        this.porcentagem = porcentagem;
        this.projeto = projeto;
    }

    // Getter e Setter para o campo "mes"
    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    // Getter e Setter para o campo "porcentagem"
    public Double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(Double porcentagem) {
        this.porcentagem = porcentagem;
    }

    // Getter e Setter para o campo "projeto"
    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

	public void setProjeto(Long idProjeto) {
		// TODO Auto-generated method stub
		
	}
}
