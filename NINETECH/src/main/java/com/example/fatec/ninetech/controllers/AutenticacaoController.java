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

import com.example.fatec.ninetech.helpers.AutenticacaoDTOServico;
import com.example.fatec.ninetech.helpers.LoginResponseDTOServico;
import com.example.fatec.ninetech.helpers.RegistroDTOServico;
import com.example.fatec.ninetech.helpers.TokenServico;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Usuario;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.UsuarioInterface;

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
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AutenticacaoDTOServico data) {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.nome(), data.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            
            var token = tokenServico.generateToken((LiderDeProjeto)auth.getPrincipal());
            
            return ResponseEntity.ok(new LoginResponseDTOServico(token));

    }
    
    @PostMapping("/registro")
    public ResponseEntity register(@RequestBody @Valid RegistroDTOServico data){
        if(this.repository.findByNome(data.nome()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        LiderDeProjeto newUser = new LiderDeProjeto(data.nome(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}