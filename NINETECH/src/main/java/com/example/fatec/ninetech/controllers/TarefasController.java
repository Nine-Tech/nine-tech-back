package com.example.fatec.ninetech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.Tarefas;
import com.example.fatec.ninetech.repositories.TarefasInterface;

@RestController
@RequestMapping("/tarefas")
public class TarefasController {

	@Autowired
	private TarefasInterface interfaceTarefas;
	
	
	@PostMapping
	public ResponseEntity<Object> cadastrar(@RequestBody Tarefas tarefas) {
		
		
		
		return ResponseEntity.ok(tarefas);
	}
	
}
