package com.example.fatec.ninetech.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;

@Service
public class LiderDeProjetoServico {
	
	@Autowired
	private LiderDeProjetoInterface interfaceLiderDeProjeto;
	
	public LiderDeProjeto criarLiderDeProjeto(LiderDeProjeto liderDeProjeto){
		return interfaceLiderDeProjeto.save(liderDeProjeto);
	}
	
}
