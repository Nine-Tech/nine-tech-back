package com.example.fatec.ninetech;

import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.fatec.ninetech.config.UsuarioRole;
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
		ApplicationContext context = SpringApplication.run(NinetechApplication.class, args);
		
		Map<String, Object> configuracao = new HashMap<>();

		configuracao.put("server.port", "5000"); // porta do backend
		configuracao.put("spring.main.lazy-initialization","false");
		configuracao.put("spring.datasource.initialization-mode", "always");
		configuracao.put("spring.jpa.hibernate.ddl-auto", "update");
		configuracao.put("spring.datasource.url", "jdbc:mysql://localhost:3306/ninetech?createDatabaseIfNotExist=true"); // caminho da conexão
		configuracao.put("spring.datasource.username", "root"); // usuario
		configuracao.put("spring.datasource.password", "mcs2809"); // senha
        configuracao.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver"); // driver mysql
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
		EngenheiroChefe engenheiroExistente = servicoEngenheiroChefe.obterEngenheiroPorId(1L);
		Projeto projetoExistente = servicoProjeto.obterProjetoPorId(1L);
				
		
		
		if (engenheiroExistente == null) {
	        // Criar e salvar um EngenheiroChefe
	        EngenheiroChefe engenheiroChefe = new EngenheiroChefe();
	        engenheiroChefe.setNome("Engenheiro Chefe 1");
			String senhaEncriptada = new BCryptPasswordEncoder().encode("123");
			engenheiroChefe.setRole(UsuarioRole.ENGENHEIRO_CHEFE);
	        engenheiroChefe.setSenha(senhaEncriptada);
	        servicoEngenheiroChefe.criarEngenheiroChefe(engenheiroChefe);
	        
	        // Criar e salvar dois LiderDeProjeto
	        LiderDeProjeto liderDeProjeto = new LiderDeProjeto();
	        liderDeProjeto.setNome("Lider de Projeto 1");
			liderDeProjeto.setRole(UsuarioRole.LIDER_DE_PROJETO_1);
	        liderDeProjeto.setSenha(senhaEncriptada);
	        servicoLiderDeProjeto.criarLiderDeProjeto(liderDeProjeto);
	        
	        LiderDeProjeto liderDeProjeto2 = new LiderDeProjeto();
	        liderDeProjeto2.setNome("Líder de Projeto 2");
	        liderDeProjeto2.setSenha(senhaEncriptada);
	        liderDeProjeto2.setRole(UsuarioRole.LIDER_DE_PROJETO_2);
	        servicoLiderDeProjeto.criarLiderDeProjeto(liderDeProjeto2);

		} 
	}
}