package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.LoggerSubpacotesPorcentagensReais;

public interface LoggerSubpacotesInterface extends JpaRepository<LoggerSubpacotesPorcentagensReais, Long> {
    List<LoggerSubpacotesPorcentagensReais> findBySubpacotesId(Long id);
}
