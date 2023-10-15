package com.example.fatec.ninetech.helpers;

public class CalcularPorcentagemDTO {
	public class TarefasDTO {

	    private Long id;
	    private String nome;
	    private String descricao;
	    private double hh;
	    private double material;
	    private double valor;
	    private Integer peso;
	    private boolean execucao;
	    private double porcentagem;

	    // Outros campos e métodos necessários

	    public void calcularPorcentagem() {
	        if (peso != null && peso != 0) {
	            this.porcentagem = ((execucao ? 1 : 0) * peso) / peso * 100;
	        } else {
	            this.porcentagem = 0.0;
	        }
	    }

	    // Getters e Setters para os campos necessários

	    // Exemplo de getters e setters para a porcentagem
	    public double getPorcentagem() {
	        return porcentagem;
	    }

	    public void setPorcentagem(double porcentagem) {
	        this.porcentagem = porcentagem;
	    }
	}
}
