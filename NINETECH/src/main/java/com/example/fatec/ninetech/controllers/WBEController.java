package com.example.fatec.ninetech.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.repositories.WBSInterface;
import com.example.fatec.ninetech.helpers.WBEServico;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/wbe")
public class WBEController {

	@Autowired
	private WBSInterface wbeInterface;

	@Autowired
	private ProjetoInterface interfaceProjeto;

	@Autowired
	private LiderDeProjetoInterface liderdeprojetoInterface;

	@Autowired
	private WBEServico wbeServico;

	// ADICIONAR LINHAS NA TABELA WBE NO PROJETO INDICADO

	@PostMapping("/adicionarLinha")
	public ResponseEntity<?> adicionarWBE(@RequestBody Map<String, Object> requestBody) {
		// Obter os dados do requestBody
		String wbe = (String) requestBody.get("wbe");
		Double valor = (Double) requestBody.get("valor");
		Double material = (Double) requestBody.get("material");
		Long projetoId = ((Number) requestBody.get("projeto_id")).longValue();
		Long liderId = ((Number) requestBody.get("lider_id")).longValue();
	
		// Verificar se todos os campos são fornecidos
		if (wbe == null || valor == null || material == null || projetoId == null || liderId == null) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Todos os campos são obrigatórios.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	
		try {
			// Verificar se o projeto com o ID fornecido existe
			Optional<Projeto> optionalProjeto = interfaceProjeto.findById(projetoId);
			if (!optionalProjeto.isPresent()) {
				Map<String, String> response = new HashMap<>();
				response.put("error", "Projeto não encontrado com o ID fornecido.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
	
			// Verificar se o líder de projeto com o ID fornecido existe
			Optional<LiderDeProjeto> optionalLiderDeProjeto = liderdeprojetoInterface.findById(liderId);
			if (!optionalLiderDeProjeto.isPresent()) {
				Map<String, String> response = new HashMap<>();
				response.put("error", "Líder de projeto não encontrado com o ID fornecido.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
	
			// Todos os campos são fornecidos e os IDs existem, podemos adicionar o WBE
			WBE wbeAdicionado = wbeServico.adicionarWBE(wbe, valor, material, projetoId, liderId);
	
			// Retornar o WBE criado em JSON
			return ResponseEntity.ok(wbeAdicionado);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Ocorreu um erro ao adicionar a linha ao Projeto.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// DELETAR LINHA NO PROJETO INDICADO

	@DeleteMapping("/delete/{wbeId}")
	public ResponseEntity<Object> excluirWBE(@PathVariable Long wbeId) {
		try {
			WBE wbeExcluido = wbeServico.excluirWBEPorId(wbeId);

			// Obter as outras linhas relacionadas ao projeto_id da linha excluída
			List<WBE> outrasLinhas = wbeInterface.findByProjetoId(wbeExcluido.getProjeto().getId());

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

	@PutMapping("/atualizar/{wbeId}")
	public ResponseEntity<WBE> atualizarDadosWBE(@PathVariable Long wbeId, @RequestBody Map<String, Object> requestBody) {
	    // Extrair os novos valores dos campos
	    Double novoValor = convertToDouble(requestBody.get("novoValor"));
	    Double novoMaterial = convertToDouble(requestBody.get("novoMaterial"));
	    String novoWbe = (String) requestBody.get("novoWbe");
	    Long novoLiderDeProjetoId = convertToLong(requestBody.get("novoLiderDeProjetoId"));

	    try {
	        // Atualizar os dados do WBE
	        WBE wbeAtualizado = wbeServico.atualizarDadosWBE(wbeId, novoValor, novoMaterial, novoWbe, novoLiderDeProjetoId);
	        return ResponseEntity.ok(wbeAtualizado);
	    } catch (EntityNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	private Double convertToDouble(Object value) {
	    if (value instanceof Number) {
	        return ((Number) value).doubleValue();
	    } else if (value instanceof String) {
	        try {
	            return Double.parseDouble((String) value);
	        } catch (NumberFormatException e) {
	            return null; // Valor não é um número válido
	        }
	    }
	    return null; // Valor não é um número válido
	}

	private Long convertToLong(Object value) {
	    if (value instanceof Number) {
	        return ((Number) value).longValue();
	    } else if (value instanceof String) {
	        try {
	            return Long.parseLong((String) value);
	        } catch (NumberFormatException e) {
	            return null; // Valor não é um número válido
	        }
	    }
	    return null; // Valor não é um número válido
	}


	// LER TODA A TABELA WBE DE ACORDO COM O PROJETO_ID INFORMADO

	@PostMapping("/listarPorProjetoId")
	public ResponseEntity<?> listarPorProjetoId(@RequestBody Map<String, Long> requestBody) {
		Long projetoId = requestBody.get("projetoId");

		// Verificar se o projeto com o ID fornecido existe
		Optional<Projeto> optionalProjeto = interfaceProjeto.findById(projetoId);
		if (!optionalProjeto.isPresent()) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Projeto não encontrado com o ID fornecido");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		// Buscar os elementos da tabela WBE pelo projeto_id
		List<WBE> wbeList = wbeInterface.findByProjeto_Id(projetoId); // Assume que você tem um método correto de busca
																		// por projetoId

		return ResponseEntity.ok(wbeList);
	}

}