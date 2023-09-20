package com.example.fatec.ninetech.helpers;

import com.example.fatec.ninetech.models.Projeto;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.repositories.ProjetoInterface;

@Service
public class ProjetoServico {
    
    @Autowired
    private ProjetoInterface interfaceProjeto;
    
    public Projeto criarProjeto(Projeto projeto) {
        return interfaceProjeto.save(projeto);
    }

    public Projeto obterProjetoPorId(Long id) {
        Optional<Projeto> projetoOptional = interfaceProjeto.findById(id);
        if (projetoOptional.isPresent()) {
            return projetoOptional.get();
        } else {
            // Se o projeto não for encontrado, retorne null ou uma mensagem de erro
            return null; // Ou retorne uma mensagem de erro como uma String
        }
    }


}
