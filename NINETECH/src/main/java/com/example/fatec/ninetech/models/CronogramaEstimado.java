package com.example.fatec.ninetech.models;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cronograma_estimado")
public class CronogramaEstimado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Colunas separadas para cada mês
    @Column(name = "mes1")
    private Integer mes1;

    @Column(name = "mes2")
    private Integer mes2;

    @Column(name = "mes3")
    private Integer mes3;

    @Column(name = "mes4")
    private Integer mes4;

    @Column(name = "mes5")
    private Integer mes5;

    @Column(name = "mes6")
    private Integer mes6;

    @Column(name = "mes7")
    private Integer mes7;

    @Column(name = "mes8")
    private Integer mes8;

    @Column(name = "mes9")
    private Integer mes9;

    @Column(name = "mes10")
    private Integer mes10;

    @Column(name = "mes11")
    private Integer mes11;

    @Column(name = "mes12")
    private Integer mes12;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "id_subpacote", referencedColumnName = "id")
    private Subpacotes subpacote;

    @Transient
    private List<List<Integer>> porcentagens;

    public CronogramaEstimado() {

    }

    public void setId_subpacote(Long idSubpacote) {
        this.subpacote.setId(idSubpacote);
    }

    public CronogramaEstimado(
            Integer mes1,
            Integer mes2,
            Integer mes3,
            Integer mes4,
            Integer mes5,
            Integer mes6,
            Integer mes7,
            Integer mes8,
            Integer mes9,
            Integer mes10,
            Integer mes11,
            Integer mes12,
            Projeto projeto,
            Subpacotes subpacote,
            List<List<Integer>> porcentagens
    ) {
        this.mes1 = mes1;
        this.mes2 = mes2;
        this.mes3 = mes3;
        this.mes4 = mes4;
        this.mes5 = mes5;
        this.mes6 = mes6;
        this.mes7 = mes7;
        this.mes8 = mes8;
        this.mes9 = mes9;
        this.mes10 = mes10;
        this.mes11 = mes11;
        this.mes12 = mes12;
        this.projeto = projeto;
        this.subpacote = subpacote;
        this.porcentagens = porcentagens;
    }

    public CronogramaEstimado(
            Long id,
            Integer mes1,
            Integer mes2,
            Integer mes3,
            Integer mes4,
            Integer mes5,
            Integer mes6,
            Integer mes7,
            Integer mes8,
            Integer mes9,
            Integer mes10,
            Integer mes11,
            Integer mes12,
            Projeto projeto,
            Subpacotes subpacote,
            List<List<Integer>> porcentagens
    ) {
        this.id = id;
        this.mes1 = mes1;
        this.mes2 = mes2;
        this.mes3 = mes3;
        this.mes4 = mes4;
        this.mes5 = mes5;
        this.mes6 = mes6;
        this.mes7 = mes7;
        this.mes8 = mes8;
        this.mes9 = mes9;
        this.mes10 = mes10;
        this.mes11 = mes11;
        this.mes12 = mes12;
        this.projeto = projeto;
        this.subpacote = subpacote;
        this.porcentagens = porcentagens;
    }
}

