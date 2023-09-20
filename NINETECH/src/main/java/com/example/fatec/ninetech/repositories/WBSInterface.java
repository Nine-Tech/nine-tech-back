package com.example.fatec.ninetech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.WBE;

public interface WBSInterface extends JpaRepository<WBE, Long>{

	
	List<WBE> findByProjetoId(Long projetoId);

}
