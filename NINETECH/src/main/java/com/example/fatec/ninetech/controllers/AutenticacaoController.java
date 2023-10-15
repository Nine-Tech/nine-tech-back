package com.example.fatec.ninetech.controllers;

import java.util.Map;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(data.login());

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
    
    @GetMapping("/informacaoUsuario")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        // Verifique se o cabeçalho de autorização está presente e no formato correto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String token = authorizationHeader.substring(7); // Remova o prefixo "Bearer " do token

        Map<String, Object> userInfo = tokenServico.getUserInfoFromToken(token);

        // Agora você pode usar as informações do usuário conforme necessário
        return ResponseEntity.ok(userInfo);
    }
    
}
