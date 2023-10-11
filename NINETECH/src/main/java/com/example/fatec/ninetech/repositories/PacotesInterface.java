package com.example.fatec.ninetech.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.models.Projeto;

public interface PacotesInterface extends JpaRepository<Pacotes, Long>{

	List<Pacotes> findByProjetoId(Long projetoId);

	List<Pacotes> findByProjeto_Id(Long projetoId);
	
	Optional<Pacotes> findById(Long pacoteId);

	//procura pelos ids usando um Objeto
	List<Pacotes> findByProjetoId(Projeto projeto);

	List<Pacotes> findAllById(Long idProjeto);
	
}
