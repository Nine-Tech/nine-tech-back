package com.example.fatec.ninetech.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.*;

@Entity
@Table(name = "projeto")
public class Projeto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projeto_id;

	@Column
	private String nome;

	@Column
	private LocalDate data_inicio;

	@Column
	private LocalDate data_final;

	@Column
	private double porcentagem;

	@Column
	private double valor_total;

	@Column
	private double valor_homem_hora = 6; // Salário Mínimo Brasileiro

	@ManyToOne
	@JoinColumn(name = "engenheiro_chefe_id")
	private EngenheiroChefe engenheiroChefe;

	public double getValor_homem_hora(){return this.valor_homem_hora;}

	public void setValor_homem_hora(double valor_homem_hora){this.valor_homem_hora = valor_homem_hora;}

	public Long getId() {
		return projeto_id;
	}

	public void setId(Long id) {
		this.projeto_id = id;
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

	public double getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(double porcentagem) {
		this.porcentagem = porcentagem;
	}

	public double getValor_total() {
		return valor_total;
	}

	public void setValor_total(double valor_total) {
		this.valor_total = valor_total;
	}

	public LocalDate getData_inicio() {
		return data_inicio;
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

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		stringBuilder.append("data_inicio: ").append(data_inicio != null ? data_inicio.format(formatter) : "null").append("\n");
		stringBuilder.append("data_final: ").append(data_final != null ? data_final.format(formatter) : "null").append("\n");

		return stringBuilder.toString();
	}

	@JsonGetter("data_inicio")
	public String getDataInicioAsString() {
		return getData_inicio() != null ? getData_inicio().toString() : null;
	}

	@JsonGetter("data_final")
	public String getDataFinalAsString() {
		return getData_final() != null ? getData_final().toString() : null;
	}

	public void setPacotes(List<Pacotes> pacotes) {
		// Implementation for setting pacotes
	}
}
