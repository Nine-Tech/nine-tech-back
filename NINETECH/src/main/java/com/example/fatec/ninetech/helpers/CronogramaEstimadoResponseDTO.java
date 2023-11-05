package com.example.fatec.ninetech.helpers;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.models.CronogramaEstimado;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CronogramaEstimadoResponseDTO {
    private Long id;
    private Map<String, Integer> meses;
    private Projeto projeto;
    private Subpacotes subpacote;

    public CronogramaEstimadoResponseDTO(
            Long id,
            Projeto projeto,
            Subpacotes subpacote,
            List<CronogramaEstimado> cronogramas) {
        this.id = id;
        this.projeto = projeto;
        this.subpacote = subpacote;
        this.meses = new HashMap<>();

        for (CronogramaEstimado cronograma : cronogramas) {
            meses.put("mes" + cronograma.getMes(), cronograma.getPorcentagem());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Integer> getMeses() {
        return meses;
    }

    public void setMeses(Map<String, Integer> meses) {
        this.meses = meses;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Subpacotes getSubpacote() {
        return subpacote;
    }

    public void setSubpacote(Subpacotes subpacote) {
        this.subpacote = subpacote;
    }
}
