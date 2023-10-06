package com.example.fatec.ninetech.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.helpers.WBEServico;
import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/wbe")
public class WBEController {

	@Autowired
	private PacotesInterface wbeInterface;

	@Autowired
	private ProjetoInterface interfaceProjeto;

	@Autowired
	private LiderDeProjetoInterface liderdeprojetoInterface;

	@Autowired
	private WBEServico wbeServico;

	// ADICIONAR LINHAS NA TABELA WBE NO PROJETO INDICADO

	@PostMapping("/{projeto_id}")
	public ResponseEntity<Object> adicionarWBE(@PathVariable("projeto_id") Long projetoId, @RequestBody Pacotes pacotes) {

		try {
			// Verificar se os campos obrigatórios estão presentes
			if (pacotes.getNome() == null) {
				Map<String, String> response = new HashMap<>();
				response.put("error",
						"Todos os campos obrigatórios devem ser preenchidos (WBE, Valor, Hh, Material, LiderDeProjeto).");
				return ResponseEntity.badRequest().body(response);
			}

			// Verificar se o projeto com o ID fornecido existe
			Optional<Projeto> optionalProjeto = interfaceProjeto.findById(projetoId);
			if (!optionalProjeto.isPresent()) {
				Map<String, String> response = new HashMap<>();
				response.put("error", "Projeto não encontrado com o ID fornecido.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Verificar se o projeto com o ID de Lider de Projeto Existe
//			Optional<LiderDeProjeto> optionalLider = liderdeprojetoInterface.findById(pacotes.getLiderDeProjeto().getId());
//			if (!optionalLider.isPresent()) {
//				Map<String, String> response = new HashMap<>();
//				response.put("error", "Lider de Projeto não encontrado com o ID fornecido.");
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//			}

			// Definir o projeto para o WBE
			pacotes.setProjeto(optionalProjeto.get());

			// Salvar o WBE no banco de dados

			Pacotes wbeAdicionado = wbeInterface.save(pacotes);

			// Retornar o WBE criado em JSON
			return ResponseEntity.status(HttpStatus.CREATED).body(wbeAdicionado);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Ocorreu um erro ao adicionar a linha ao Projeto: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// DELETAR LINHA NO PROJETO INDICADO

	@DeleteMapping("/{wbeId}")
	public ResponseEntity<Object> excluirWBE(@PathVariable Long wbeId) {
		try {
			Pacotes wbeExcluido = wbeServico.excluirWBEPorId(wbeId);

			// Obter as outras linhas relacionadas ao projeto_id da linha excluída
			List<Pacotes> outrasLinhas = wbeInterface.findByProjetoId(wbeExcluido.getProjeto().getId());

			// Construir a resposta JSON com as outras linhas e o objeto excluído
			Map<String, Object> response = new HashMap<>();

			response.put("tabelaNova", outrasLinhas);

			return ResponseEntity.ok(response);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao excluir a linha.");
		}
	}

	// ATUALIZAR A LINHA WBE_ID E QUE O PROJETO_ID EXISTA NA PLANILHA PROJETO.

//	@PutMapping("/{wbeId}")
//	public ResponseEntity<Object> atualizarDadosWBE(@PathVariable Long wbeId, @RequestBody Pacotes requestBody) {
//	    // Extrair os novos valores dos campos
//	    String novoWbe = requestBody.getNome();
//
//	    try {
//	        // Verificar se o WBE com o ID fornecido existe
//	        Optional<Pacotes> optionalWBE = wbeInterface.findById(wbeId);
//	        if (!optionalWBE.isPresent()) {
//	            Map<String, String> response = new HashMap<>();
//	            response.put("error", "WBE não encontrado com o ID fornecido.");
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//	        }
//
//	        Pacotes pacotes = optionalWBE.get();
//	        // Atualizar os campos do WBE com os novos valores
//	        pacotes.setNome(novoWbe);
//
//	        // Atualizar o líder de projeto se o ID for diferente
//	        LiderDeProjeto novoLiderDeProjeto = requestBody.getLiderDeProjeto();
//	        if (novoLiderDeProjeto != null) {
//	            Long novoLiderDeProjetoId = novoLiderDeProjeto.getId();
//
//	            // Verificar se o novo ID do Líder de Projeto é válido
//	            Optional<LiderDeProjeto> optionalLider = liderdeprojetoInterface.findById(novoLiderDeProjetoId);
//	            if (!optionalLider.isPresent()) {
//	                Map<String, String> response = new HashMap<>();
//	                response.put("error", "Líder de Projeto não encontrado com o ID fornecido.");
//	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//	            }
//
//	            pacotes.setLiderDeProjeto(optionalLider.get());
//	        }
//
//	        // Atualizar o WBE no banco de dados usando o serviço
//	        Pacotes wbeAtualizado = wbeServico.atualizarDadosWBE(pacotes);
//
//	        // Retornar o WBE atualizado em JSON
//	        return ResponseEntity.ok(wbeAtualizado);
//	    } catch (Exception e) {
//	        Map<String, String> response = new HashMap<>();
//	        response.put("error", "Ocorreu um erro ao atualizar os dados do WBE: " + e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	    }
//	}





	// LER TODA A TABELA WBE DE ACORDO COM O PROJETO_ID INFORMADO

	@GetMapping("/{projeto_id}")
	public ResponseEntity<Object> listarPorProjetoId(@RequestBody Map<String, Long> requestBody) {
		Long projetoId = requestBody.get("projetoId");

		// Verificar se o projeto com o ID fornecido existe
		Optional<Projeto> optionalProjeto = interfaceProjeto.findById(projetoId);
		if (!optionalProjeto.isPresent()) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Projeto não encontrado com o ID fornecido");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		// Buscar os elementos da tabela WBE pelo projeto_id
		List<Pacotes> wbeList = wbeInterface.findByProjeto_Id(projetoId); // Assume que você tem um método correto de busca
																		// por projetoId

		return ResponseEntity.ok(wbeList);
	}

}