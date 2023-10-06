package com.example.fatec.ninetech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.Tarefas;

public interface TarefasInterface extends JpaRepository<Tarefas, Long>{
	
}
