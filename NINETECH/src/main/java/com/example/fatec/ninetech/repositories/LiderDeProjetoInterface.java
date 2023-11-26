package com.example.fatec.ninetech.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.LiderDeProjeto;

public interface LiderDeProjetoInterface extends JpaRepository<LiderDeProjeto, Long> {
    UserDetails findByLogin(String login);
    
    Optional<LiderDeProjeto> findById(Long liderDeProjetoId);

	UserDetails findByNome(String nome);
}

