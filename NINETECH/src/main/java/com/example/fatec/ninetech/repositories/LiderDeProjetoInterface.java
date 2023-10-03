package com.example.fatec.ninetech.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.fatec.ninetech.models.LiderDeProjeto;

public interface LiderDeProjetoInterface extends JpaRepository<LiderDeProjeto, Long> {
    UserDetails findByNome(String nome);
}

