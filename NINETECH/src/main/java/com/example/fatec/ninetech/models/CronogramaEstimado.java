package com.example.fatec.ninetech.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cronograma_estimado")
public class CronogramaEstimado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer mes;

    @Column
    private Integer porcentagem;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @Column(name = "wbe_id")
    private Long wbeId;
    
    @ManyToMany
    @JoinTable(
        name = "cronograma_estimado_wbe",
        joinColumns = @JoinColumn(name = "cronograma_estimado_id"),
        inverseJoinColumns = @JoinColumn(name = "wbe_id")
    )
    private List<WBE> wbes = new ArrayList<>();
    
    @Transient
    private List<List<Integer>> porcentagens;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWbeId() {
        return wbeId;
    }

    public void setWbeId(Long wbeId) {
        this.wbeId = wbeId;
    }
    
    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(Integer porcentagem) {
        this.porcentagem = porcentagem;
    }
    
    public List<List<Integer>> getPorcentagens() {
        return porcentagens;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public List<WBE> getWBE() {
        return wbes;
    }

    public void setWBE(List<WBE> wbe) {
        this.wbes = wbe;
    }
    
}

