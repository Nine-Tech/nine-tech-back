package com.example.fatec.ninetech.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.ProgressaoMensal;
import com.example.fatec.ninetech.repositories.ProgressaoMensalRepositorio;

@RestController
@RequestMapping("/progressaomensal")
public class ProgressaoMensalController {
	
	@Autowired
	private ProgressaoMensalRepositorio repoProgressaoMensal;
	
	@GetMapping
	public List<ProgressaoMensal> listar(){
		return repoProgressaoMensal.findAll();
	}
	
	@GetMapping("/{id}")
	public ProgressaoMensal buscar(@PathVariable Long id) {
		return repoProgressaoMensal.findById(id).get();	
	}
	
	@PostMapping("/cadastrar")
	public ProgressaoMensal cadastrar(@RequestBody ProgressaoMensal progressaomensal) {
		return repoProgressaoMensal.save(progressaomensal);
	}
	
	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		repoProgressaoMensal.deleteById(id);
	}
	
	@PutMapping("/{id}")
	public ProgressaoMensal alterar(@RequestBody ProgressaoMensal progressaomensal) {
		if(progressaomensal.getId() > 0){
			return repoProgressaoMensal.save(progressaomensal);
		}
		return null;
	}
}
