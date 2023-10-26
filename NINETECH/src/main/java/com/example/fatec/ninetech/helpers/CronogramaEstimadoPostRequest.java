package com.example.fatec.ninetech.helpers;

import lombok.Getter;

import java.util.List;

@Getter
public class CronogramaEstimadoPostRequest {
        private Long id;

        private Integer mes1;

        private Long id_projeto;

        private List<List<Integer>> porcentagens;
}
