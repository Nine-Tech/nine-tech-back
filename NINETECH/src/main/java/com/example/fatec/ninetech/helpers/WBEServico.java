package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.WBE;
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
	
	public void atualizarLiderProjetoNome(Long wbeId, String novoNome) {
		WBE wbe = wbeInterface.findById(wbeId).orElse(null);
		if (wbe != null) {
			wbeInterface.save(wbe);
		}
	}
	
	
    
	
	public void adicionarWBE(String wbe, Double valor, Double hh, String lider_de_projeto_nome, Long projeto_id) {
	    Optional<Projeto> optionalProjeto = projetoInterface.findById(projeto_id);

	    if (!optionalProjeto.isPresent()) {
	        throw new EntityNotFoundException("Projeto não encontrado com ID: " + projeto_id);
	    }

	    Projeto projeto = optionalProjeto.get();

	    // Crie um novo WBE com os dados fornecidos
	    WBE novoWBE = new WBE();
	    novoWBE.setHh(hh);
	    novoWBE.setValor(valor);
	    novoWBE.setWbe(wbe);
	    
	    novoWBE.setProjeto(projeto);

	    wbeInterface.save(novoWBE);
	}
	
	@Transactional
	 public void excluirWBEPorId(Long wbeId) {
        Optional<WBE> wbeOptional = wbeInterface.findById(wbeId);

        wbeOptional.ifPresent(wbe -> {
            wbeInterface.delete(wbe);
        });
    }
	
	public WBE atualizarWBE(Long wbeId, Double novoHH, Double novoValor, String novoWbe, Long projetoId) {
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

	public Optional<WBE> findById(Long wbeId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(WBE wbe) {
		// TODO Auto-generated method stub
		
	}


	public WBE atualizarProjetoId(Long wbeId, Long novoProjetoId) {
	    Optional<WBE> optionalWBE = wbeInterface.findById(wbeId);

	    if (optionalWBE.isPresent()) {
	        WBE wbe = optionalWBE.get();

	        // Obtenha o projeto correspondente ao novoProjetoId
	        Optional<Projeto> optionalProjeto = projetoInterface.findById(novoProjetoId);
	        if (optionalProjeto.isPresent()) {
	            Projeto novoProjeto = optionalProjeto.get();

	            // Atualize o projeto_id no objeto WBE
	            wbe.setProjeto(novoProjeto);
	            return wbeInterface.save(wbe);
	        } else {
	            throw new EntityNotFoundException("Projeto não encontrado com ID: " + novoProjetoId);
	        }
	    } else {
	        throw new EntityNotFoundException("WBE não encontrado com ID: " + wbeId);
	    }
	}
	
	 public WBEServico(WBSInterface wbeInterface) {
	        this.wbeInterface = wbeInterface;
	    }

	public List<WBE> obterTodosOsWBE() {
        return wbeInterface.findAll();
    }



	public void adicionarWBE(String wbe, Double valor, Double hh, Long projetoId) {
		// TODO Auto-generated method stub
		
	}

	
}