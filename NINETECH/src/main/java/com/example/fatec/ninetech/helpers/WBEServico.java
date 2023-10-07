package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.WBSInterface;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WBEServico {

	@Autowired
	private WBSInterface wbeInterface;

	@Autowired
	private ProjetoInterface projetoInterface;

	@Autowired
	private LiderDeProjetoInterface liderdeprojetoInterface;

	

	@Transactional
	public WBE excluirWBEPorId(Long wbeId) {
		Optional<WBE> wbeOptional = wbeInterface.findById(wbeId);

		return wbeOptional.map(wbe -> {
			wbeInterface.delete(wbe);
			return wbe;
		}).orElseThrow(() -> new EntityNotFoundException("WBE não encontrado com ID: " + wbeId));
	}

	public WBEServico(WBSInterface wbeInterface) {
		this.wbeInterface = wbeInterface;
	}

	public List<WBE> obterTodosOsWBE() {
		return wbeInterface.findAll();
	}
	
	 public WBE atualizarDadosWBE(WBE wbe) {
	        // Verifique se o WBE existe no banco de dados
	        Optional<WBE> optionalWBE = wbeInterface.findById(wbe.getId());
	        if (!optionalWBE.isPresent()) {
	            throw new EntityNotFoundException("WBE não encontrado com o ID fornecido.");
	        }

	        // Atualize os campos do WBE com os novos valores
	        WBE wbeExistente = optionalWBE.get();
	        wbeExistente.setWbe(wbe.getWbe());

	        // Atualize o líder de projeto se o ID for diferente
	        if (!wbeExistente.getLiderDeProjeto().getId().equals(wbe.getLiderDeProjeto().getId())) {
	            // Lógica para atualizar o líder de projeto se necessário
	            // ...
	        }

	        // Salve as atualizações no banco de dados
	        return wbeInterface.save(wbeExistente);
	    }
	

}