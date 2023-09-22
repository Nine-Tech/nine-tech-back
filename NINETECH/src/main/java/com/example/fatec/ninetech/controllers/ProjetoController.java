package com.example.fatec.ninetech.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.repositories.ProjetoInterface;

@RestController
@RequestMapping("/projeto")
public class ProjetoController {

    @Autowired
    private ProjetoInterface projetoInterface;

    @PostMapping("/criar")
    public ResponseEntity<Projeto> criarProjeto(@RequestBody Projeto projeto) {
        try {
            Projeto novoProjeto = projetoInterface.save(projeto);
            return new ResponseEntity<>(novoProjeto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Projeto>> listarProjetos() {
        List<Projeto> projetos = projetoInterface.findAll();
        return new ResponseEntity<>(projetos, HttpStatus.OK);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<Projeto> lerProjeto(@PathVariable Long id) {
        try {
            return projetoInterface.findById(id)
                .map(projeto -> new ResponseEntity<>(projeto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Projeto> atualizarProjeto(@PathVariable Long id, @RequestBody Projeto projetoAtualizado) {
        try {
            return projetoInterface.findById(id)
                .map(projeto -> {
                    projeto.setNome(projetoAtualizado.getNome());
                    projeto.setData_inicio(projetoAtualizado.getData_inicio());
                    projeto.setData_final(projetoAtualizado.getData_final());
                    // Atualize outros campos, se necessário
                    Projeto projetoAtualizadoSalvo = projetoInterface.save(projeto);
                    return new ResponseEntity<>(projetoAtualizadoSalvo, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        try {
            projetoInterface.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}