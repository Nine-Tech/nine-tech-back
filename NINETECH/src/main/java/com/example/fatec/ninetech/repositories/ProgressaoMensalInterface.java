package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fatec.ninetech.models.ProgressaoMensal;

import jakarta.transaction.Transactional;

public interface ProgressaoMensalInterface extends JpaRepository<ProgressaoMensal, Long> {
	
	List<ProgressaoMensal> findByExecucao(boolean execucao);
	 
	@Query("SELECT pm, pm.wbe.id FROM ProgressaoMensal pm " +
		       "INNER JOIN pm.wbe wbe " +
		       "WHERE wbe.liderDeProjeto.id = :liderId " +
		       "AND wbe.projeto.id = :projetoId")
		List<Object[]> buscarPorLiderEProjeto(@Param("liderId") Long liderId, @Param("projetoId") Long projetoId);
		
		// CREATE
		@Modifying
	    @Transactional
	    @Query(value = "INSERT INTO progressao_mensal (data, execucao, id_wbe, peso) " +
	                   "VALUES (:data, :execucao, :id_wbe, :peso)",
	           nativeQuery = true)
	    void inserirProgressaoMensal(@Param("data") String data,
	                                 @Param("execucao") boolean execucao,
	                                 @Param("id_wbe") Long idWbe,
	                                 @Param("peso") String peso);
		// UPDATE
		@Modifying
	    @Transactional
	    @Query(value = "UPDATE progressao_mensal " +
	                   "SET data = :data, execucao = :execucao, peso = :peso " +
	                   "WHERE id = :id",
	           nativeQuery = true)
	    void atualizarProgressaoMensal(@Param("id") Long id,
	                                   @Param("data") String data,
	                                   @Param("execucao") boolean execucao,
	                                   @Param("peso") String peso);
	

}
