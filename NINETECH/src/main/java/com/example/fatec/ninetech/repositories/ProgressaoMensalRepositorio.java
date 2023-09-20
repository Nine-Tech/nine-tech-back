package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.ProgressaoMensal;

public interface ProgressaoMensalRepositorio extends JpaRepository<ProgressaoMensal, Long> {
	List<ProgressaoMensal> findByExecucao(boolean execucao);
}
