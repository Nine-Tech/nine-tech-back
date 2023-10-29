package com.example.fatec.ninetech.repositories;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CronogramaEstimadoInterface extends JpaRepository<CronogramaEstimado, Long>{

	List<CronogramaEstimado> findByProjeto(Projeto projetoExistente);
    List<CronogramaEstimado> findByProjetoId(Long projetoId);
	void deleteByProjeto(Projeto projetoExistente);
	
//    @Query("SELECT ce FROM CronogramaEstimado ce WHERE ce.projeto = ?1 AND ce.wbe.id = ?2")
//    List<CronogramaEstimado> findByProjetoAndWbeId(Projeto projeto, Long wbeId);

    boolean existsBySubpacoteId(Long id);

    Optional<CronogramaEstimado> findBySubpacoteId(Long id);

    boolean existsByIdAndSubpacoteId(Long id, Long idSubpacote);

    @Modifying
    @Transactional
    @Query("DELETE FROM CronogramaEstimado ce WHERE ce.subpacote.id = :subpacoteId")
    void deleteBySubpacoteId(@Param("subpacoteId") Long subpacoteId);
	List<CronogramaEstimado> findByProjetoIdAndMes(Long idProjeto, int i);
	CronogramaEstimado findByProjetoIdAndSubpacoteIdAndMes(Long id, Long id2, int mes);
	List<CronogramaEstimado> findBySubpacoteAndProjeto(Subpacotes subpacote, Projeto projeto);
	void deleteByMesAndSubpacoteAndProjeto(Integer mesExistente, Subpacotes subpacote, Projeto projeto);
	List<CronogramaEstimado> findByProjetoIdAndSubpacoteId(Long id, Long id2);


    // boolean existsByProjetoAndWbeId(Projeto projeto, Long wbeId);
}
