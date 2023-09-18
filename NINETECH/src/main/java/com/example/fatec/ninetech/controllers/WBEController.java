package com.example.fatec.ninetech.controllers;

import java.util.Map;

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

import com.example.fatec.ninetech.helpers.WBEServico;
import com.example.fatec.ninetech.models.WBE;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/wbe")
public class WBEController {

	@Autowired
	private WBEServico wbeServico;

	@PutMapping("/atualizar-lider/{wbeId}")
	public ResponseEntity<String> atualizarLiderProjetoNome(@PathVariable Long wbeId,
			@RequestBody Map<String, String> requestBody) {
		String novoNome = requestBody.get("novoNome");

		if (novoNome == null || novoNome.isEmpty()) {
			return ResponseEntity.badRequest().body("O novo nome do líder não pode ser vazio."); // Retorna uma mensagem
																									// de erro se o novo
																									// nome estiver
																									// ausente ou vazio
		}

		wbeServico.atualizarLiderProjetoNome(wbeId, novoNome);

		String mensagem = "Líder de Projeto Atualizado para: " + novoNome;

		return ResponseEntity.ok().body(mensagem);
	}

	@PostMapping("/adicionarLinha")
	public ResponseEntity<String> adicionarWBE(@RequestBody Map<String, Object> requestBody) {
		String wbe = (String) requestBody.get("wbe");
		Double valor = (Double) requestBody.get("valor");
		Double hh = (Double) requestBody.get("hh");
		String liderDeProjetoNome = (String) requestBody.get("lider_de_projeto_nome");
		Long projetoId = ((Number) requestBody.get("projeto_id")).longValue();

		if (wbe == null || valor == null || hh == null || liderDeProjetoNome == null || projetoId == null) {
			return ResponseEntity.badRequest().body("Todos os campos são obrigatórios.");
		}

		wbeServico.adicionarWBE(wbe, valor, hh, liderDeProjetoNome, projetoId);

		return ResponseEntity.ok().body("WBE adicionado com sucesso.");
	}

	@DeleteMapping("/delete/{wbeId}")
	public ResponseEntity<String> excluirWBE(@PathVariable Long wbeId) {
		try {
			wbeServico.excluirWBEPorId(wbeId);
			return ResponseEntity.ok("Linha excluída com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao excluir a linha.");
		}
	}

	@PutMapping("/atualizar/{wbeId}")
	public ResponseEntity<String> atualizarWBE(@PathVariable Long wbeId, @RequestBody Map<String, Object> requestBody) {
		Double novoHH = (Double) requestBody.get("novoHH");
		Double novoValor = (Double) requestBody.get("novoValor");
		String novoWbe = (String) requestBody.get("novoWbe");
		Long projetoId = ((Number) requestBody.get("projetoId")).longValue();

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

}