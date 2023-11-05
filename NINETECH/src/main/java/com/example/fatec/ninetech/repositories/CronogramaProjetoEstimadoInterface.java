package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.CronogramaProjetoEstimado;
import org.springframework.stereotype.Repository;

@Repository
public interface CronogramaProjetoEstimadoInterface extends JpaRepository<CronogramaProjetoEstimado, Long> {

	List<CronogramaProjetoEstimado> findByProjetoId(Long id);

	CronogramaProjetoEstimado findByProjetoIdAndMes(Long idProjeto, int mes);

	CronogramaProjetoEstimado findByMesAndProjetoId(int mes, Long idProjeto);
    
}
