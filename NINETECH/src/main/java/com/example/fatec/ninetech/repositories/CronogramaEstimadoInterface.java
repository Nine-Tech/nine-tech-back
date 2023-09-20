package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;

public interface CronogramaEstimadoInterface extends JpaRepository<CronogramaEstimado, Long>{

	List<CronogramaEstimado> findByProjeto(Projeto projetoExistente);

	void deleteByProjeto(Projeto projetoExistente);

}
