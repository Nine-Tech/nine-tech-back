package com.example.fatec.ninetech.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/subpacotes")
public class SubpacotesController {

    @Autowired
    private SubpacotesInterface interfaceSubpacotes;

    @Autowired
    private LiderDeProjetoInterface interfaceLiderDeProjeto;
    
    @Autowired
    private PacotesInterface interfacePacotes;


	@GetMapping("/listarUmSubpacote/{idDoSubpacote}")
	public ResponseEntity<Subpacotes> listarSubpacotes (@PathVariable Long idDoSubpacote) {
	    Optional<Subpacotes> subpacoteOptional = interfaceSubpacotes.findById(idDoSubpacote);

	    if (subpacoteOptional.isPresent()) {
	        Subpacotes subpacotes = subpacoteOptional.get();
	        return new ResponseEntity<>(subpacotes, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}

    @GetMapping("/{idDoLider}")
	public ResponseEntity<List<Subpacotes>> listarSubpacotesPorLiderId(@PathVariable Long idDoLider) {
	    try {
	        List<Subpacotes> subpacotes = interfaceSubpacotes.findByLiderDeProjetoId(idDoLider);
	        if (!subpacotes.isEmpty()) {
	            return new ResponseEntity<>(subpacotes, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
    
    @GetMapping("/listaIdSubpacote/{idSubpacote}")
	public ResponseEntity<Subpacotes> listarSubpacotesPorId(@PathVariable Long idSubpacote) {
	    try {
	        Optional<Subpacotes> subpacotes = interfaceSubpacotes.findById(idSubpacote);
	        if (subpacotes != null) {
	        	return new ResponseEntity<Subpacotes>(subpacotes.get(), HttpStatus.OK);

	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
    
    @GetMapping("/porIdProjeto/{id}")
	public ResponseEntity<List<Subpacotes>> listarPacotesPorIdProjeto(@PathVariable Long id){
		
		Optional<Pacotes> pacotesOptional = interfacePacotes.findById(id);
		
		if (pacotesOptional.isPresent()) {
			Long idPacote = pacotesOptional.get().getId();
			
			List<Subpacotes> listaPacotes = interfaceSubpacotes.findByPacotesId(idPacote);
			
			return new ResponseEntity<>(listaPacotes, HttpStatus.OK);
			
		} else {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

    // Isolar as variáveis e salvar apenas as que mudaram, se não ele seta para nulo
	@PutMapping("/{idLider}/{id}")
	public ResponseEntity<Subpacotes> atualizarWBS(@PathVariable Long id, @PathVariable Long idLider) {
		//Verificando se WBE, Projeto e LiderDeProjeto existem
		
		System.out.println(id);
		System.out.println(idLider);
		
	    Optional<Subpacotes> encontrarSubpacotesPorId = interfaceSubpacotes.findById(id);
	    Optional<LiderDeProjeto> liderDeProjetoOptional = interfaceLiderDeProjeto.findById(idLider);

//	    if (encontrarPorIdWBS.isEmpty()) {
//	        return ResponseEntity.notFound().build();
//	    }

	    Subpacotes atualizandoWBS = encontrarSubpacotesPorId.get();

//	    if (atualizadoWBS.getWbe() != null) {
//	        atualizandoWBS.setWbe(atualizadoWBS.getWbe());
//	    }
//
//	    if (projetoOptional.isPresent()) {
//	        atualizandoWBS.setProjeto(projetoOptional.get());
	    //}

	    if (liderDeProjetoOptional.isPresent()) {
	        atualizandoWBS.setLiderDeProjeto(liderDeProjetoOptional.get());
	    }

	    Subpacotes wbeAtualizado = interfaceSubpacotes.save(atualizandoWBS);

	    return ResponseEntity.ok(wbeAtualizado);
	}
    
}
