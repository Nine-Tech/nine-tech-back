package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.Subpacotes;

public interface SubpacotesInterface extends JpaRepository<Subpacotes, Long>{

	List<Subpacotes> findByPacotesId(Long projetoId);
	
	List<Subpacotes> findByLiderDeProjetoId(Long idLider);
	
}
