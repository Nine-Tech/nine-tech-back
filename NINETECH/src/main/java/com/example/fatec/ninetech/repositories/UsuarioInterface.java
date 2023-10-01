package com.example.fatec.ninetech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.fatec.ninetech.models.Usuario;

public interface UsuarioInterface extends JpaRepository<Usuario, String> {
	UserDetails findByLogin(String login);
}
