package com.example.fatec.ninetech.controllers;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.config.UsuarioRole;
import com.example.fatec.ninetech.helpers.AutenticacaoDTOServico;
import com.example.fatec.ninetech.helpers.LoginResponseDTOServico;
import com.example.fatec.ninetech.helpers.TokenServico;
import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;

import jakarta.validation.Valid;
@RestController
@RequestMapping("auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenServico tokenServico;
    
    @Autowired
    private LiderDeProjetoInterface repository;
    
    @Autowired
    private EngenheiroChefeInterface engenheiroChefeInterface;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AutenticacaoDTOServico data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.nome(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        UserDetails userDetails = userDetailsService.loadUserByUsername(data.nome());

        if (userDetails instanceof LiderDeProjeto) {
            var token = tokenServico.generateToken((LiderDeProjeto) userDetails);
            return ResponseEntity.ok(new LoginResponseDTOServico(token));
        } else if (userDetails instanceof EngenheiroChefe) {
            var token = tokenServico.generateToken((EngenheiroChefe) userDetails);
            return ResponseEntity.ok(new LoginResponseDTOServico(token));
        } else {
            // Handle other user types or return an error response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
}