package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;

public interface CronogramaEstimadoInterface extends JpaRepository<CronogramaEstimado, Long>{

	List<CronogramaEstimado> findByProjeto(Projeto projetoExistente);

	void deleteByProjeto(Projeto projetoExistente);

    @Query("SELECT ce FROM CronogramaEstimado ce WHERE ce.projeto.id = :projetoId")
    List<CronogramaEstimado> findByProjetoId(@Param("projetoId") Long projetoId);

}
