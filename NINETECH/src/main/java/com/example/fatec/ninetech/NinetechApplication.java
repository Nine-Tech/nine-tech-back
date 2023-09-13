package com.example.fatec.ninetech;

import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.fatec.ninetech.helpers.EngenheiroChefeServico;
import com.example.fatec.ninetech.helpers.LiderDeProjetoServico;
import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.models.LiderDeProjeto;

import java.util.Map;

@SpringBootApplication
public class NinetechApplication {

	public static void main(String[] args) {
		ApplicationContext context =SpringApplication.run(NinetechApplication.class, args);
		
		Map<String, Object> configuracao = new HashMap<>();

		configuracao.put("server.port", "3000");

		configuracao.put("spring.datasource.url", "jdbc:mysql://localhost:3306/ninetech"); // caminho da conexão
		
		configuracao.put("spring.datasource.username", "root"); // usuario
		configuracao.put("spring.datasource.password", "123456"); // senha
		
		configuracao.put("spring.jpa.show-sql", "true"); // mostrar comandos
		configuracao.put("spring.jpa.hibernate.ddl-auto", "update"); // criar editar
		
		SpringApplication app = new SpringApplication(NinetechApplication.class);
		app.setDefaultProperties(configuracao);
		app.run(args);
		
		//!!! Setando alguns usuarios
        
        // Obtendo o serviço EngenheiroChefeServico do contexto Spring
		EngenheiroChefeServico servicoEngenheiroChefe = context.getBean(EngenheiroChefeServico.class);
		LiderDeProjetoServico servicoLiderDeProjeto = context.getBean(LiderDeProjetoServico.class);

        // Criar e salvar um EngenheiroChefe
        EngenheiroChefe engenheiroChefe = new EngenheiroChefe();
        engenheiroChefe.setNome("Engenheiro Chefe 1");
        servicoEngenheiroChefe.criarEngenheiroChefe(engenheiroChefe);
        
     // Criar e salvar dois LiderDeProjeto
        LiderDeProjeto liderDeProjeto = new LiderDeProjeto();
        liderDeProjeto.setNome("Líder de Projeto 1");
        servicoLiderDeProjeto.criarLiderDeProjeto(liderDeProjeto);
        
        LiderDeProjeto liderDeProjeto2 = new LiderDeProjeto();
        liderDeProjeto2.setNome("Líder de Projeto 2");
        servicoLiderDeProjeto.criarLiderDeProjeto(liderDeProjeto2);
	}

}