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
	
	public void atualizarLiderProjetoNome(Long wbeId, String novoNome) {
		WBE wbe = wbeInterface.findById(wbeId).orElse(null);
		if (wbe != null) {
			wbeInterface.save(wbe);
		}
	}
	
	
    
	
	public WBE adicionarWBE(String wbe, Double valor, Double hh, Long projetoId, Long liderDeProjetoId) {
	    Optional<Projeto> optionalProjeto = projetoInterface.findById(projetoId);

	    if (!optionalProjeto.isPresent()) {
	        throw new EntityNotFoundException("Projeto não encontrado com ID: " + projetoId);
	    }

	    Optional<LiderDeProjeto> optionalLiderDeProjeto = liderdeprojetoInterface.findById(liderDeProjetoId);

	    if (!optionalLiderDeProjeto.isPresent()) {
	        throw new EntityNotFoundException("Líder de Projeto não encontrado com ID: " + liderDeProjetoId);
	    }

	    Projeto projeto = optionalProjeto.get();
	    LiderDeProjeto liderDeProjeto = optionalLiderDeProjeto.get();

	    // Crie um novo WBE com os dados fornecidos
	    WBE novoWBE = new WBE();
	    novoWBE.setHh(hh);
	    novoWBE.setValor(valor);
	    novoWBE.setWbe(wbe);
	    novoWBE.setProjeto(projeto);
	    novoWBE.setLiderDeProjeto(liderDeProjeto);

	    return wbeInterface.save(novoWBE);
	}

	
	@Transactional
	public WBE excluirWBEPorId(Long wbeId) {
	    Optional<WBE> wbeOptional = wbeInterface.findById(wbeId);

	    return wbeOptional.map(wbe -> {
	        wbeInterface.delete(wbe);
	        return wbe;
	    }).orElseThrow(() -> new EntityNotFoundException("WBE não encontrado com ID: " + wbeId));
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




	public WBE atualizarDadosWBE(Long wbeId, Double novoHH, Double novoValor, String novoWbe, Long novoLiderDeProjetoId) {
        // Verifique se o WBE com o ID fornecido existe
        Optional<WBE> optionalWBE = wbeInterface.findById(wbeId);
        if (optionalWBE.isPresent()) {
            WBE wbe = optionalWBE.get();

            // Atualize os campos
            if (novoHH != null) {
                wbe.setHh(novoHH);
            }
            if (novoValor != null) {
                wbe.setValor(novoValor);
            }
            if (novoWbe != null) {
                wbe.setWbe(novoWbe);
            }

            // Verifique se o novo líder de projeto existe
            Optional<LiderDeProjeto> optionalNovoLiderDeProjeto = liderdeprojetoInterface.findById(novoLiderDeProjetoId);
            if (!optionalNovoLiderDeProjeto.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Líder de projeto não encontrado com o novo lider_de_projeto_id fornecido");
            }
            wbe.setLiderDeProjeto(optionalNovoLiderDeProjeto.get());

            // Salve a atualização no banco de dados
            return wbeInterface.save(wbe);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "WBE não encontrado com o ID fornecido");
        }
    }

   

	
}