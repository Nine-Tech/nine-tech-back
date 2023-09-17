package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.WBSInterface;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class WBEServico {
	
	@Autowired
	private WBSInterface wbeInterface;
	
	public void atualizarLiderProjetoNome(Long wbeId, String novoNome) {
		WBE wbe = wbeInterface.findById(wbeId).orElse(null);
		if (wbe != null) {
			wbe.setLider_de_projeto_nome(novoNome);
			wbeInterface.save(wbe);
		}
	}
	
	public void adicionarWBE(String wbe, Double valor, Double hh, String lider_de_projeto_nome) {
        
        WBE novoWBE = new WBE(wbe, valor, hh, lider_de_projeto_nome);
        
        wbeInterface.save(novoWBE);
    }
	
	@Transactional
	 public void excluirWBEPorId(Long wbeId) {
        Optional<WBE> wbeOptional = wbeInterface.findById(wbeId);

        wbeOptional.ifPresent(wbe -> {
            wbeInterface.delete(wbe);
        });
    }
	
	public WBE atualizarWBE(Long wbeId, Double novoHH, Double novoValor, String novoWbe) {
        Optional<WBE> optionalWBE = wbeInterface.findById(wbeId);

        if (optionalWBE.isPresent()) {
            WBE wbe = optionalWBE.get();

            // Atualiza os campos com os novos valores
            wbe.setHh(novoHH);
            wbe.setValor(novoValor);
            wbe.setWbe(novoWbe);

            // Salva a entidade atualizada
            return wbeInterface.save(wbe);
        } else {
            throw new EntityNotFoundException("WBE não encontrado com ID: " + wbeId);
        }
    }
	
}