package com.example.fatec.ninetech.helpers;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
public class CronogramaEstimadoRequest {
        private Integer mes1;

        private Integer mes2;

        private Integer mes3;

        private Integer mes4;

        private Integer mes5;

        private Integer mes6;

        private Integer mes7;

        private Integer mes8;

        private Integer mes9;

        private Integer mes10;

        private Integer mes11;

        private Integer mes12;

        private Long id_projeto;

        private List<List<Integer>> porcentagens;
}
