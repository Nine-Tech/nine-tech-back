package com.example.fatec.ninetech.helpers;

import java.util.List;

public class CronogramaEstimadoResponseDTO {
    private Long idProjeto;
    private List<Integer> porcentagens;

    public CronogramaEstimadoResponseDTO(Long idProjeto, List<Integer> porcentagens) {
        this.idProjeto = idProjeto;
        this.porcentagens = porcentagens;
    }

    public Long getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(Long idProjeto) {
        this.idProjeto = idProjeto;
    }

    public List<Integer> getPorcentagens() {
        return porcentagens;
    }

    public void setPorcentagens(List<Integer> porcentagens) {
        this.porcentagens = porcentagens;
    }
}