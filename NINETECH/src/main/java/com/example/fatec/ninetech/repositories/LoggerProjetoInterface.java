package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.LoggerProjetoPorcentagensReais;

public interface LoggerProjetoInterface extends JpaRepository<LoggerProjetoPorcentagensReais, Long>{
    List<LoggerProjetoPorcentagensReais> findByProjetoId(Long id);
}
