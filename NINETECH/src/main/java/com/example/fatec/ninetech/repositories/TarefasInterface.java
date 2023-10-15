package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fatec.ninetech.models.Tarefas;

public interface TarefasInterface extends JpaRepository<Tarefas, Long>{

	List<Tarefas> findBySubpacotes_Id(Long id);
	
	List<Tarefas> findBySubpacotes_Pacotes_Id(Long pacoteId);
	
	List<Tarefas> findBySubpacotes_Pacotes_Projeto_Id(Long projetoId);
	
}


