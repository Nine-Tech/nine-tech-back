package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.PacotesInterface;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WBEServico {

	@Autowired
	private PacotesInterface wbeInterface;

	@Autowired
	private ProjetoInterface projetoInterface;

	@Autowired
	private LiderDeProjetoInterface liderdeprojetoInterface;

	

	@Transactional
	public Pacotes excluirWBEPorId(Long wbeId) {
		Optional<Pacotes> wbeOptional = wbeInterface.findById(wbeId);

		return wbeOptional.map(wbe -> {
			wbeInterface.delete(wbe);
			return wbe;
		}).orElseThrow(() -> new EntityNotFoundException("WBE não encontrado com ID: " + wbeId));
	}

	public WBEServico(PacotesInterface wbeInterface) {
		this.wbeInterface = wbeInterface;
	}

	public List<Pacotes> obterTodosOsWBE() {
		return wbeInterface.findAll();
	}
	
	 public Pacotes atualizarDadosWBE(Pacotes pacotes) {
	        // Verifique se o WBE existe no banco de dados
	        Optional<Pacotes> optionalWBE = wbeInterface.findById(pacotes.getId());
	        if (!optionalWBE.isPresent()) {
	            throw new EntityNotFoundException("WBE não encontrado com o ID fornecido.");
	        }

	        // Atualize os campos do WBE com os novos valores
	        Pacotes wbeExistente = optionalWBE.get();
	        wbeExistente.setNome(pacotes.getNome());

	        // Salve as atualizações no banco de dados
	        return wbeInterface.save(wbeExistente);
	    }
	

}