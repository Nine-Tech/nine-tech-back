package com.example.fatec.ninetech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatec.ninetech.models.Projeto;

import java.util.Optional;

public interface ProjetoInterface extends JpaRepository<Projeto, Long> {

    Optional<Projeto> findById(Long id);
}