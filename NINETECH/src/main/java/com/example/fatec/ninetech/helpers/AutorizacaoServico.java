package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.repositories.UsuarioInterface;

@Service
public class AutorizacaoServico implements UserDetailsService {

	@Autowired
	UsuarioInterface repositorio;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return repositorio.findByLogin(username);
	}

}
