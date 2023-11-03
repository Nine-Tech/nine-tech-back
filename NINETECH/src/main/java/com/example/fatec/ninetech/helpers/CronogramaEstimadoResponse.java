package com.example.fatec.ninetech.helpers;

public class CronogramaEstimadoResponse {
	private int mes;
    private double porcentagem;
    

    public CronogramaEstimadoResponse(int mes, double porcentagem) {
        this.mes = mes;
        this.porcentagem = porcentagem;
        
    }

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public double getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(double porcentagem) {
		this.porcentagem = porcentagem;
	}

	
    
    
}
