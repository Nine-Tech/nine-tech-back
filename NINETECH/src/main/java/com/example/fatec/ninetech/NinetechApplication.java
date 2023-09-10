package com.example.fatec.ninetech;

import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Map;

@SpringBootApplication
public class NinetechApplication {

	public static void main(String[] args) {
		SpringApplication.run(NinetechApplication.class, args);
		
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
	}

}
