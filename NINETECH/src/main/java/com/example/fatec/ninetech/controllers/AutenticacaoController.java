package com.example.fatec.ninetech.controllers;

import java.util.List;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.config.UsuarioRole;
import com.example.fatec.ninetech.helpers.AutenticacaoDTOServico;
import com.example.fatec.ninetech.helpers.LoginResponseDTOServico;
import com.example.fatec.ninetech.helpers.RegistroDTOServico;
import com.example.fatec.ninetech.helpers.TokenServico;
import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import com.mysql.cj.util.StringUtils;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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
    
    @Autowired
    private SubpacotesInterface subpacotesInterface;

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
    
    @PostMapping("/registro")
    public ResponseEntity registro(@RequestBody @Valid RegistroDTOServico data) {
        // Obtém a lista de todos os líderes de projeto
        List<LiderDeProjeto> lideresDeProjeto = this.repository.findAll();

        // Pega o último elemento da lista
        LiderDeProjeto ultimoLiderDeProjeto = lideresDeProjeto.get(lideresDeProjeto.size() - 1);

        // Obtém o ID desse elemento
        Long idUltimoLiderDeProjeto = ultimoLiderDeProjeto.getId();

        // Cria o nome do usuário com o número após
        String login = "lider" + (idUltimoLiderDeProjeto + 1);
        String nome = "Líder de Projeto" + (idUltimoLiderDeProjeto + 1);

        // Verifica se o nome do usuário já existe
        if (this.repository.findByLogin(login) != null) {
            return ResponseEntity.badRequest().build();
        }

        // Encripta a senha
        String senhaEncriptada = new BCryptPasswordEncoder().encode(data.senha());

        // Cria o novo usuário
        LiderDeProjeto novoUsuario = new LiderDeProjeto(login, nome, senhaEncriptada, data.role());

        // Salva o novo usuário no banco de dados
        this.repository.save(novoUsuario);

        // Retorna o status 200
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/lideres")
    public ResponseEntity<List<LiderDeProjeto>> visualizarLideresProjeto() {
    	List<LiderDeProjeto> lideres = repository.findAll();
        return new ResponseEntity<>(lideres, HttpStatus.OK);
    }
    
    @GetMapping("/lideres/{id}")
    public ResponseEntity<LiderDeProjeto> visualizarlider(@PathVariable Long id) {
        try {
            return repository.findById(id)
                .map(lider -> new ResponseEntity<>(lider, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<? extends Object> atualizarLiderProjeto(@PathVariable Long id, @RequestBody LiderDeProjeto lideratualizado) {
        try {
            return repository.findById(id)
                    .map(lider -> {
                        if (!StringUtils.isEmptyOrWhitespaceOnly(lideratualizado.getNome())) {
                            lider.setNome(lideratualizado.getNome());
                        }

                        if (!StringUtils.isEmptyOrWhitespaceOnly(lideratualizado.getLogin())) {
                            lider.setLogin(lideratualizado.getLogin());
                        }

                        // Verifica se a senha atual está presente
                        if (!StringUtils.isEmptyOrWhitespaceOnly(lideratualizado.getSenhaAtual())) {
                            String senhaAtualDescriptografada = lideratualizado.getSenhaAtual();

                            // Verifica se a senha atual está correta
                            if (!BCrypt.checkpw(senhaAtualDescriptografada, lider.getSenha())) {
                                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                            }
                        } else {
                            // Senha atual não fornecida, então a senha não é atualizada
                        }

                        // Verifica se a nova senha está presente
                        if (!StringUtils.isEmptyOrWhitespaceOnly(lideratualizado.getSenha())) {
                            // Criptografa a nova senha
                            String novaSenhaCriptografada = BCrypt.hashpw(lideratualizado.getSenha(), BCrypt.gensalt(10));

                            lider.setSenha(novaSenhaCriptografada);
                        }

                        LiderDeProjeto liderAtualizadoSalvo = repository.save(lider);

                        // Retorna o líder atualizado salvo
                        if (liderAtualizadoSalvo != null) {
                            return new ResponseEntity<>(liderAtualizadoSalvo, HttpStatus.OK);
                        } else {
                            // Retorna um código de erro se o líder não for atualizado
                            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    })
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<LiderDeProjeto> excluirlider(@PathVariable Long id) {
    	LiderDeProjeto liderDeProjeto = repository.findById(id).orElse(null);
    	if (liderDeProjeto == null) {
    	    // O líder de projeto não existe
    	}
        // Verifica se o líder de projeto está em um gerenciamento de subpacote
        List<Subpacotes> subpacotes = subpacotesInterface.findByLiderDeProjetoId(id);
        if (!subpacotes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
