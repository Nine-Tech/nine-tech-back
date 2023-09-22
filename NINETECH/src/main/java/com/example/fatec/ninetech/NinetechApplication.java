package com.example.fatec.ninetech;

import java.time.LocalDate;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.fatec.ninetech.helpers.EngenheiroChefeServico;
import com.example.fatec.ninetech.helpers.LiderDeProjetoServico;
import com.example.fatec.ninetech.helpers.ProjetoServico;
import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;

import java.util.Map;

@SpringBootApplication
public class NinetechApplication {

	public static void main(String[] args) {
		ApplicationContext context =SpringApplication.run(NinetechApplication.class, args);
		
		Map<String, Object> configuracao = new HashMap<>();

		configuracao.put("server.port", "5000"); // porta do backend
		configuracao.put("spring.datasource.url", "jdbc:mysql://localhost:3306/ninetech"); // caminho da conexão
		configuracao.put("spring.datasource.username", "root"); // usuario
		configuracao.put("spring.datasource.password", "123456"); // senha
        configuracao.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver"); // driver mysql
        configuracao.put("spring.jpa.hibernate.ddl-auto", "update"); // criar - atualizar
        configuracao.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); // dialeto mysql
		
		SpringApplication app = new SpringApplication(NinetechApplication.class);
		app.setDefaultProperties(configuracao);
		app.run(args);
		
		
		//!!! Setando alguns usuarios
        // Obtendo o serviço EngenheiroChefeServico do contexto Spring
		EngenheiroChefeServico servicoEngenheiroChefe = context.getBean(EngenheiroChefeServico.class);
		LiderDeProjetoServico servicoLiderDeProjeto = context.getBean(LiderDeProjetoServico.class);
		ProjetoServico servicoProjeto = context.getBean(ProjetoServico.class);

		// Criando apenas se não houver um projeto no BD
		Projeto projetoExistente = servicoProjeto.obterProjetoPorId(1L);
		
		if (projetoExistente == null) {
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
	        
	        Projeto projeto = new Projeto();
	        projeto.setNome("Carro");
	        projeto.setData_inicio(LocalDate.of(2023, 9, 14));
	        projeto.setData_final(LocalDate.of(2024, 9, 14)); 
	        projeto.setEngenheiroChefe(engenheiroChefe);
	        servicoProjeto.criarProjeto(projeto);

	        Projeto projeto2 = new Projeto();
	        projeto2.setNome("Barco");
	        projeto2.setData_inicio(LocalDate.of(2023, 9, 14));
	        projeto2.setData_final(LocalDate.of(2024, 6, 25)); 
	        projeto2.setEngenheiroChefe(engenheiroChefe);
	        servicoProjeto.criarProjeto(projeto2);

	        Projeto projeto3 = new Projeto();
	        projeto3.setNome("Avião");
	        projeto3.setData_inicio(LocalDate.of(2023, 9, 14));
	        projeto3.setData_final(LocalDate.of(2025, 6, 25)); 
	        projeto3.setEngenheiroChefe(engenheiroChefe);
	        servicoProjeto.criarProjeto(projeto3);

		} 
	}
}