package com.example.fatec.ninetech.helpers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;

@Service
public class EngenheiroChefeServico {
	
	@Autowired
	private EngenheiroChefeInterface interfaceEngenheiroChefe;
	
	public EngenheiroChefe criarEngenheiroChefe(EngenheiroChefe engenheiroChefe) {
        return interfaceEngenheiroChefe.save(engenheiroChefe);
    }
	
    public EngenheiroChefe obterEngenheiroPorId(Long id) {
        Optional<EngenheiroChefe> engenheiroOptional = interfaceEngenheiroChefe.findById(id);
        if (engenheiroOptional.isPresent()) {
            return engenheiroOptional.get();
        } else {
            // Se o projeto não for encontrado, retorne null ou uma mensagem de erro
            return null; // Ou retorne uma mensagem de erro como uma String
        }
    }
}
