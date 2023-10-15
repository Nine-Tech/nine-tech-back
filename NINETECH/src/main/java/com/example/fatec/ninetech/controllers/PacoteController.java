package com.example.fatec.ninetech.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;

@RestController
@RequestMapping("/pacotes")
public class PacoteController {
	
	@Autowired
    private PacotesInterface interfacePacotes;
	
	@Autowired
	private ProjetoInterface interfaceProjeto;
	
	@GetMapping("/{id}")
	public ResponseEntity<Pacotes> listarPacotesPorId(@PathVariable Long id) {
	    Optional<Pacotes> pacotesOptional = interfacePacotes.findById(id);

	    if (pacotesOptional.isPresent()) {
	        Pacotes pacote = pacotesOptional.get();
	        return new ResponseEntity<>(pacote, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@GetMapping("/porIdProjeto/{id}")
	public ResponseEntity<List<Pacotes>> listarPacotesPorIdProjeto(@PathVariable Long id){
		
		Optional<Projeto> projetoOptional = interfaceProjeto.findById(id);
		
		if (projetoOptional.isPresent()) {
			Long idProjeto = projetoOptional.get().getId();
			
			List<Pacotes> listaPacotes = interfacePacotes.findByProjetoId(idProjeto);
			
			return new ResponseEntity<>(listaPacotes, HttpStatus.OK);
			
		} else {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
