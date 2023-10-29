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

    private Integer mes;

    private Integer porcentagem;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "id_subpacote")
    private Subpacotes subpacote;

    public CronogramaEstimado() {

    }

    public void setId_subpacote(Long idSubpacote) {
        this.subpacote.setId(idSubpacote);
    }

    public CronogramaEstimado(
            Integer mes,
            Integer porcentagem,
            Projeto projeto,
            Subpacotes subpacote
    ) {
        this.mes = mes;
        this.porcentagem = porcentagem;
        this.projeto = projeto;
        this.subpacote = subpacote;
    }

    public CronogramaEstimado(
            Long id,
            Integer mes,
            Integer porcentagem,
            Projeto projeto,
            Subpacotes subpacote
    ) {
        this.id = id;
        this.mes = mes;
        this.porcentagem = porcentagem;
        this.projeto = projeto;
        this.subpacote = subpacote;
    }
}
