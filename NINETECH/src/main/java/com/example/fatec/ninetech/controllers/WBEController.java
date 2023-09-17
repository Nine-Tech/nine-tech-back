package com.example.fatec.ninetech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.helpers.WBEServico;
import com.example.fatec.ninetech.models.WBE;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/wbe")
public class WBEController {

	@Autowired
	private WBEServico wbeServico;

	@PutMapping("/{wbeId}/atualizar-lider")
	public void atualizarLiderProjetoNome(@PathVariable Long wbeId, @RequestParam String novoNome) {
		wbeServico.atualizarLiderProjetoNome(wbeId, novoNome);
	}

	@PostMapping("/adicionar")
	public void adicionarWBE(@RequestParam String wbe, @RequestParam Double valor, @RequestParam Double hh,
			@RequestParam String lider_de_projeto_nome) {
		wbeServico.adicionarWBE(wbe, valor, hh, lider_de_projeto_nome);
	}

	@DeleteMapping("/delete/{wbeId}")
	public void excluirWBE(@PathVariable Long wbeId) {
		wbeServico.excluirWBEPorId(wbeId);
	}

	@PutMapping("/atualizar/{wbeId}")
	public ResponseEntity<WBE> atualizarWBE(@PathVariable Long wbeId, @RequestParam Double novoHH,
			@RequestParam Double novoValor, @RequestParam String novoWbe) {
		try {
			WBE wbeAtualizado = wbeServico.atualizarWBE(wbeId, novoHH, novoValor, novoWbe);
			return ResponseEntity.ok(wbeAtualizado);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}