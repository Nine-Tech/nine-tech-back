package com.example.fatec.ninetech.helpers;

public class CronogramaEstimadoResponse {
	private int mes;
    private int porcentagem;
    

    public CronogramaEstimadoResponse(int mes, int porcentagem) {
        this.mes = mes;
        this.porcentagem = porcentagem;
        
    }

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(int porcentagem) {
		this.porcentagem = porcentagem;
	}

	
    
    
}
