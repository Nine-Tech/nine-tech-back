package com.example.fatec.ninetech.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;

@RestController
@RequestMapping("/lider")
public class LiderDeProjetoController {

	@Autowired
    private LiderDeProjetoInterface liderDeProjetoInterface;
	
	@GetMapping("/listar")
    public ResponseEntity<List<LiderDeProjeto>> listarLideres() {
        List<LiderDeProjeto> lideres = liderDeProjetoInterface.findAll();
        return new ResponseEntity<>(lideres, HttpStatus.OK);
    }
	
}
