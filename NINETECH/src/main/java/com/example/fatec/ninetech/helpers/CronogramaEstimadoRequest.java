package com.example.fatec.ninetech.helpers;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
public class CronogramaEstimadoRequest {
    private Integer mes1;
    private Long id_projeto;
    private List<Integer> porcentagens; // Altere o tipo de dado para uma lista de Integer

    // Adicione um setter para porcentagens se você não o tiver

    public void setPorcentagens(List<Integer> porcentagens) {
        this.porcentagens = porcentagens;
    }
}
