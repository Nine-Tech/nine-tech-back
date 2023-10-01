package com.example.fatec.ninetech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.helpers.AutenticacaoDTOServico;
import com.example.fatec.ninetech.helpers.LoginResponseDTOServico;
import com.example.fatec.ninetech.helpers.RegistroDTOServico;
import com.example.fatec.ninetech.helpers.TokenServico;
import com.example.fatec.ninetech.models.Usuario;
import com.example.fatec.ninetech.repositories.UsuarioInterface;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UsuarioInterface repositorio;
	
	@Autowired
	private TokenServico tokenServico;

	@PostMapping("/login")
	public ResponseEntity login(@RequestBody @Valid AutenticacaoDTOServico data) {
		var usuarioSenha = new UsernamePasswordAuthenticationToken(data.login(), data.password());
		var auth = this.authenticationManager.authenticate(usuarioSenha);
		
		var token = tokenServico.generateToken((Usuario)auth.getPrincipal());
		
		return ResponseEntity.ok(new LoginResponseDTOServico(token));
	}
	
	//não vamos usar, mas é bom ter
	@PostMapping("/registro")
	public ResponseEntity registro(@RequestBody @Valid RegistroDTOServico data) {
		if(this.repositorio.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();
		
		String senhaEncriptada = new BCryptPasswordEncoder().encode(data.password());
		Usuario novoUsuario = new Usuario(data.login(), senhaEncriptada, data.role());
		
		this.repositorio.save(novoUsuario);
		
		return ResponseEntity.ok().build();
	}
}
