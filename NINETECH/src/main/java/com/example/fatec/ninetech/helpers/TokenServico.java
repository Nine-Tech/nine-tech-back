package com.example.fatec.ninetech.helpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.models.LiderDeProjeto;

@Service
public class TokenServico {
	@Value("${api.security.token.secret}")
	private String secret;
	
	// Em TokenServico.java, atualize generateToken para incluir o campo "id"
	public String generateToken(UserDetails userDetails) {
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(secret);
	        String id = extractUserId(userDetails); // Extrair o ID do usuário, se possível
            String nome = extractNome(userDetails); // Buscar o login do usuário no banco de dados
	        String token = JWT.create()
	                .withIssuer("auth-api")
	                .withSubject(userDetails.getUsername())
	                .withClaim("id", id) // Adicione o ID do usuário como uma reclamação personalizada
	                .withClaim("login", userDetails.getUsername())
	                .withClaim("nome", nome)
	                .withClaim("role", userDetails.getAuthorities().stream()
	                        .map(GrantedAuthority::getAuthority)
	                        .collect(Collectors.toList()))
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
	
	public Map<String, Object> getUserInfoFromToken(String token) {
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(secret);
	        DecodedJWT decodedJWT = JWT.require(algorithm)
	                .withIssuer("auth-api")
	                .build()
	                .verify(token);

	        Map<String, Object> userInfo = new HashMap<>();
	        userInfo.put("id", decodedJWT.getClaim("id").asString());
	        userInfo.put("nome", decodedJWT.getClaim("nome").asString());
	        userInfo.put("login", decodedJWT.getClaim("login").asString());
	        userInfo.put("roles", decodedJWT.getClaim("role").asList(String.class));

	        return userInfo;
	    } catch (JWTVerificationException exception) {
	        return Collections.emptyMap(); // Retorne um mapa vazio se o token não for válido
	    }
	}
	
	private String extractUserIdEngenheiroChefe(EngenheiroChefe engenheiroChefe) {
	    // Suponha que o EngenheiroChefe tenha um campo 'id' como um Long.
	    return String.valueOf(engenheiroChefe.getId());
	}
	
	private String extractUserIdLiderDeProjeto(LiderDeProjeto liderDeProjeto) {
	    // Suponha que o LiderDeProjeto tenha um campo 'id' como um Long.
	    return String.valueOf(liderDeProjeto.getId());
	}
	
	private String extractUserId(UserDetails userDetails) {
	    if (userDetails instanceof EngenheiroChefe) {
	        EngenheiroChefe engenheiroChefe = (EngenheiroChefe) userDetails;
	        return String.valueOf(engenheiroChefe.getId()); // Converter para String
	    } else if (userDetails instanceof LiderDeProjeto) {
	        LiderDeProjeto liderDeProjeto = (LiderDeProjeto) userDetails;
	        return String.valueOf(liderDeProjeto.getId()); // Converter para String
	    } else {
	        return null;
	    }
	}
    
    private String extractNome(UserDetails userDetails) {
	    if (userDetails instanceof EngenheiroChefe) {
	        EngenheiroChefe engenheiroChefe = (EngenheiroChefe) userDetails;
	        return engenheiroChefe.findNomeById(engenheiroChefe.getId());
	    } else if (userDetails instanceof LiderDeProjeto) {
	        LiderDeProjeto liderDeProjeto = (LiderDeProjeto) userDetails;
	        return liderDeProjeto.findNomeById(liderDeProjeto.getId());
	    } else {
	        return null;
	    }
    }
}