package com.example.fatec.ninetech.helpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Usuario;

@Service
public class TokenServico {
	@Value("${api.security.token.secret}")
	private String secret;
	
	// Em TokenServico.java, atualize generateToken para aceitar UserDetails
	public String generateToken(UserDetails userDetails) {
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(secret);
	        String token = JWT.create()
	                .withIssuer("auth-api")
	                .withSubject(userDetails.getUsername())
	                .withExpiresAt(tempoExpiracao())
	                .sign(algorithm);
	        return token;
	    } catch (JWTCreationException exception) {
	        throw new RuntimeException("Erro ao gerar token", exception);
	    }
	}
	
	public String validarToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
					.withIssuer("auth-api")
					.build() //verificar
					.verify(token) //decodificar
					.getSubject();
		} catch (JWTVerificationException exception) {
			return "";
		}
	}
	
	private Instant tempoExpiracao() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // horario de brasilia -3 e esta 2 horas o tempo de expiração
	}
}
