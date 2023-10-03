package com.example.fatec.ninetech.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

    @Column(name = "wbe_id")
    private Long wbeId;

    @Transient
    private List<List<Integer>> porcentagens;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getMes1() {
        return mes1;
    }

    public void setMes1(Integer mes1) {
        this.mes1 = mes1;
    }

    public Integer getMes2() {
        return mes2;
    }

    public void setMes2(Integer mes2) {
        this.mes2 = mes2;
    }
    
    public Integer getMes3() {
        return mes3;
    }

    public void setMes3(Integer mes3) {
        this.mes3 = mes3;
    }

    public Integer getMes4() {
        return mes4;
    }

    public void setMes4(Integer mes4) {
        this.mes4 = mes4;
    }

    public Integer getMes5() {
        return mes5;
    }

    public void setMes5(Integer mes5) {
        this.mes5 = mes5;
    }

    public Integer getMes6() {
        return mes6;
    }

    public void setMes6(Integer mes6) {
        this.mes6 = mes6;
    }

    public Integer getMes7() {
        return mes7;
    }

    public void setMes7(Integer mes7) {
        this.mes7 = mes7;
    }

    public Integer getMes8() {
        return mes8;
    }

    public void setMes8(Integer mes8) {
        this.mes8 = mes8;
    }

    public Integer getMes9() {
        return mes9;
    }

    public void setMes9(Integer mes9) {
        this.mes9 = mes9;
    }

    public Integer getMes10() {
        return mes10;
    }

    public void setMes10(Integer mes10) {
        this.mes10 = mes10;
    }

    public Integer getMes11() {
        return mes11;
    }

    public void setMes11(Integer mes11) {
        this.mes11 = mes11;
    }

    public Integer getMes12() {
        return mes12;
    }

    public void setMes12(Integer mes12) {
        this.mes12 = mes12;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }
    
    public Long getWbeId() {
        return wbeId;
    }

    public void setWbeId(Long wbeId) {
        this.wbeId = wbeId;
    }
    
    public List<List<Integer>> getPorcentagens() {
        return porcentagens;
    }
}

