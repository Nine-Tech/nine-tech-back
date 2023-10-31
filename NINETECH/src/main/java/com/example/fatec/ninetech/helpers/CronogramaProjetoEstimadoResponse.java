package com.example.fatec.ninetech.helpers;

public class CronogramaProjetoEstimadoResponse {
    private Integer mes;  // Usando Integer ao invés de int
    private Double porcentagem;  // Usando Double ao invés de int

    public CronogramaProjetoEstimadoResponse(Integer mes, Double porcentagem) {
        this.mes = mes;
        this.porcentagem = porcentagem;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(Double porcentagem) {
        this.porcentagem = porcentagem;
    }
}
