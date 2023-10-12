package com.example.fatec.ninetech.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fatec.ninetech.models.Subpacotes;

public interface SubpacotesInterface extends JpaRepository<Subpacotes, Long>{

	List<Subpacotes> findByPacotesId(Long projetoId);
	
	List<Subpacotes> findByLiderDeProjetoId(Long idLider);
	
	@Query("SELECT SP FROM Subpacotes SP WHERE SP.id = :id")
	Optional<Subpacotes> findById(@Param("id")Long id);
	
}
