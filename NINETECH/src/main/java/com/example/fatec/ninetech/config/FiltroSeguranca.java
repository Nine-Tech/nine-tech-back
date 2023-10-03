package com.example.fatec.ninetech.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.fatec.ninetech.helpers.TokenServico;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.UsuarioInterface;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FiltroSeguranca extends OncePerRequestFilter {
	
	@Autowired
	TokenServico tokenServico;
	
	@Autowired
	LiderDeProjetoInterface liderDeProjetoInterface;
	
	@Autowired
	EngenheiroChefeInterface engenheiroChefeInterface;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		var token = this.recoverToken(request);
		if (token != null) {
			var subject = tokenServico.validarToken(token);
			UserDetails liderdeprojeto = liderDeProjetoInterface.findByNome(subject);
			
			var authentication = new UsernamePasswordAuthenticationToken(liderdeprojeto, null, liderdeprojeto.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	private String recoverToken(HttpServletRequest request) {
	    var authHeader = request.getHeader("Authorization");
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return null;
	    }
	    return authHeader.substring(7); // Remove o prefixo "Bearer " para obter o token JWT
	}
}