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

import com.example.fatec.ninetech.repositories.WBSInterface;
import com.example.fatec.ninetech.helpers.ProjetoServico;
import com.example.fatec.ninetech.helpers.WBEServico;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private WBEServico wbeServico;

	@Autowired
	private ProjetoServico projetoServico;

	// FUNÇÃO DE ATUALIZAR O PROJETO_ID - DO WBE_ID

	@PutMapping("/atualizar-projeto-id/{wbeId}")
	public ResponseEntity<?> atualizarLiderProjetoId(@PathVariable Long wbeId,
			@RequestBody Map<String, Long> requestBody) {
		Long novoProjetoId = requestBody.get("novoProjetoId");
		System.out.println(novoProjetoId);
		if (novoProjetoId == null) {
			return ResponseEntity.badRequest().body("O novo projeto_id não pode ser nulo.");
		}

		WBE wbe = wbeServico.atualizarProjetoId(wbeId, novoProjetoId);
		System.out.println(wbe);

		if (wbe != null) {
			// Obter informações do projeto atualizado
			Long projetoId = wbe.getProjeto().getId();
			Long liderDeProjetoId = wbe.getProjeto().getLiderDeProjeto().getLider_de_projeto_id();
			String nomeLiderProjeto = wbe.getProjeto().getLiderDeProjeto().getNome();
			System.out.println("Chegando aqui");

			// Montar a resposta JSON
			Map<String, Object> response = new HashMap<>();
			response.put("projetoId", projetoId);
			response.put("liderDeProjetoId", liderDeProjetoId);
			response.put("nomeLiderProjeto", nomeLiderProjeto);

			return ResponseEntity.ok().body(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// ADICIONAR LINHAS NA TABELA WBE NO PROJETO INDICADO

	@PostMapping("/adicionarLinha")
	public ResponseEntity<Map<String, String>> adicionarWBE(@RequestBody Map<String, Object> requestBody) {
		// Obter os dados do requestBody
		String wbe = (String) requestBody.get("wbe");
		Double valor = (Double) requestBody.get("valor");
		Double hh = (Double) requestBody.get("hh");
		Long projetoId = ((Number) requestBody.get("projeto_id")).longValue();

		// Verificar se todos os campos são fornecidos
		if (wbe == null || valor == null || hh == null || projetoId == null) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Todos os campos são obrigatórios.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Verificar se o projeto com o ID fornecido existe
		Optional<Projeto> optionalProjeto = Optional.ofNullable(projetoServico.obterProjetoPorId(projetoId));
		if (!optionalProjeto.isPresent()) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Projeto não encontrado com o ID fornecido.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Todos os campos são fornecidos e o projeto_id existe, podemos adicionar o WBE
		wbeServico.adicionarWBE(wbe, valor, hh, projetoId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Linhas adicionada ao Projeto com Sucesso!.");
		return ResponseEntity.ok().body(response);
	}

	// DELETAR LINHA NO PROJETO INDICADO

	@DeleteMapping("/delete/{wbeId}")
	public ResponseEntity<String> excluirWBE(@PathVariable Long wbeId) {
		try {
			wbeServico.excluirWBEPorId(wbeId);
			return ResponseEntity.ok("Linha excluída com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao excluir a linha.");
		}
	}

	// ATUALIZAR A LINHA WBE_ID E QUE O PROJETO_ID EXISTA NA PLANILHA PROJETO.

	@PutMapping("/atualizar/{wbeId}")
	public ResponseEntity<?> atualizarWBE(@PathVariable Long wbeId, @RequestBody Map<String, Object> requestBody) {
		Double novoHH = (Double) requestBody.get("novoHH");
		Double novoValor = (Double) requestBody.get("novoValor");
		String novoWbe = (String) requestBody.get("novoWbe");
		Long projetoId = ((Number) requestBody.get("projetoId")).longValue();

		// Verificar se o projeto com o ID fornecido existe
		Optional<Projeto> optionalProjeto = Optional.ofNullable(projetoServico.obterProjetoPorId(projetoId));
		if (!optionalProjeto.isPresent()) {
			Map<String, String> response = new HashMap<>();
			response.put("error", "Projeto não encontrado com o ID fornecido");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		try {
			WBE wbeAtualizado = wbeServico.atualizarWBE(wbeId, novoHH, novoValor, novoWbe, projetoId);
			if (wbeAtualizado != null) {
				return ResponseEntity.ok("WBE atualizado com sucesso");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao atualizar o WBE.");
			}
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build(); // Usando build() para retornar uma resposta vazia com status 404
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao atualizar o WBE.");
		}
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
		List<WBE> wbeList = wbeInterface.findByProjetoId(projetoId);

		return ResponseEntity.ok(wbeList);
	}

}