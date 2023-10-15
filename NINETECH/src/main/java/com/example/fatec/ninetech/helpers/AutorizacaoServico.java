package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
@Service
public class AutorizacaoServico implements UserDetailsService {
    @Autowired
    LiderDeProjetoInterface liderDeProjetoRepositorio;

    @Autowired
    EngenheiroChefeInterface engenheiroChefeRepositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;

        // Verifica se o usuário é um Líder de Projeto
        userDetails = liderDeProjetoRepositorio.findByLogin(username);
        if (userDetails != null) {
            return userDetails;
        }

        // Se não for um Líder de Projeto, verifica se é um Engenheiro Chefe
        userDetails = engenheiroChefeRepositorio.findByLogin(username);
        if (userDetails != null) {
            return userDetails;
        }

        throw new UsernameNotFoundException("Usuário não encontrado.");
    }
}