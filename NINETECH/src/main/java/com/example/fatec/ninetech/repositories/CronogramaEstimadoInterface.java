package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;

public interface CronogramaEstimadoInterface extends JpaRepository<CronogramaEstimado, Long>{

	List<CronogramaEstimado> findByProjeto(Projeto projetoExistente);
    List<CronogramaEstimado> findByProjetoId(Long projetoId);
	void deleteByProjeto(Projeto projetoExistente);
	
    @Query("SELECT ce FROM CronogramaEstimado ce WHERE ce.projeto = ?1 AND ce.wbe.id = ?2")
    List<CronogramaEstimado> findByProjetoAndWbeId(Projeto projeto, Long wbeId);
    
    boolean existsByProjetoAndWbeId(Projeto projeto, Long wbeId);
}
