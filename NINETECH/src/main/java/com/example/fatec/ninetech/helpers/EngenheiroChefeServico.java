package com.example.fatec.ninetech.helpers;

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
}
