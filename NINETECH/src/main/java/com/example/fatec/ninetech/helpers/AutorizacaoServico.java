package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.UsuarioInterface;
@Service
public class AutorizacaoServico implements UserDetailsService {
    @Autowired
    LiderDeProjetoInterface repositorio;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = repositorio.findByNome(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }
        return userDetails;
    }
}