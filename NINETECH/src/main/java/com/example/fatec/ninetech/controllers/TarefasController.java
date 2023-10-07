package com.example.fatec.ninetech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.models.Tarefas;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import com.example.fatec.ninetech.repositories.TarefasInterface;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tarefas")
public class TarefasController {

	@Autowired
	private TarefasInterface interfaceTarefas;

	@Autowired
	private SubpacotesInterface interfaceSubpacotes;

	@PostMapping
	public ResponseEntity<Object> cadastrar(@RequestBody Tarefas tarefas) {
		try {
			// Calcular o valor com base na fórmula
			double valorCalculado = (tarefas.getHh() * 100) + tarefas.getMaterial();
			tarefas.setValor(valorCalculado);

			// Calcular a porcentagem com base na fórmula
			double execucaoNumerica = tarefas.getExecucao() ? 1 : 0; // 1 se for true, 0 se for false
			double porcentagemCalculada = ((execucaoNumerica * tarefas.getPeso()) / tarefas.getPeso()) * 100;
			tarefas.setPorcentagem(porcentagemCalculada);

			// Obter o ID do Subpacote da Tarefa
			Long subpacoteId = tarefas.getSubpacotes().getId();
			// salvando nova tarefa
			Tarefas novaTarefa = interfaceTarefas.save(tarefas);

			// Obter as tarefas relacionadas ao Subpacote indicado
			List<Tarefas> tarefasRelacionadas = interfaceTarefas.findBySubpacotes_Id(subpacoteId);
			
			// Calcular a soma dos valores e pesos das tarefas relacionadas ao Subpacote
			double somaValores = 0.0;
			double somaPesos = 0.0;
			double somaPesosTotal = 0.0;

			for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
				double execucaoNumericaTarefa = tarefaRelacionada.getExecucao() ? 1.0 : 0.0;
				somaValores += execucaoNumericaTarefa * tarefaRelacionada.getValor();
				somaPesos += execucaoNumericaTarefa * tarefaRelacionada.getPeso();
			}
			System.out.println();
			for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
				somaPesosTotal += tarefaRelacionada.getPeso();
			}

			// Calcular valor_total e porcentagem no Subpacote
			double valorTotalCalculado = somaValores;
			double porcentagemSubpacote =  (somaPesos / somaPesosTotal)*100.0;

			// Atualizar o Subpacote com os novos valores
			Optional<Subpacotes> subpacoteOptional = interfaceSubpacotes.findById(subpacoteId);
			if (subpacoteOptional.isPresent()) {
				Subpacotes subpacote = subpacoteOptional.get();
				subpacote.setValor_total(valorTotalCalculado);
				subpacote.setPorcentagem(porcentagemSubpacote);
				interfaceSubpacotes.save(subpacote);
			}

			
			return new ResponseEntity<>(novaTarefa, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefas tarefaAtualizada) {
		try {
			Optional<Tarefas> tarefaExistente = interfaceTarefas.findById(id);

			if (tarefaExistente.isPresent()) {
				Tarefas tarefa = tarefaExistente.get();

				// Atualizar os campos da tarefa
				tarefa.setDescricao(tarefaAtualizada.getDescricao());
				tarefa.setData(tarefaAtualizada.getData());
				tarefa.setDescricao(tarefaAtualizada.getDescricao());
				tarefa.setExecucao(tarefaAtualizada.getExecucao());
				tarefa.setHh(tarefaAtualizada.getHh());
				tarefa.setMaterial(tarefaAtualizada.getMaterial());
				tarefa.setNome(tarefaAtualizada.getNome());
				tarefa.setPeso(tarefaAtualizada.getPeso());

				// Calcular o valor com base na fórmula
				double valorCalculado = (tarefaAtualizada.getHh() * 100) + tarefaAtualizada.getMaterial();
				tarefa.setValor(valorCalculado);

				// Calcular a porcentagem com base na fórmula
				double execucaoNumerica = tarefaAtualizada.getExecucao() ? 1 : 0; // 1 se for true, 0 se for false
				double porcentagemCalculada = (execucaoNumerica * tarefaAtualizada.getPeso())
						/ tarefaAtualizada.getPeso();
				tarefa.setPorcentagem(porcentagemCalculada);

				Tarefas tarefaAtualizadaNoBanco = interfaceTarefas.save(tarefa);

				return new ResponseEntity<>(tarefaAtualizadaNoBanco, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Método para excluir uma tarefa pelo ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> excluirTarefa(@PathVariable Long id) {
		try {
			Optional<Tarefas> tarefaOptional = interfaceTarefas.findById(id);

			if (tarefaOptional.isPresent()) {
				Tarefas tarefa = tarefaOptional.get();
				Long subpacoteId = tarefa.getSubpacotes().getId();

				interfaceTarefas.deleteById(id);

				// Buscar todas as tarefas relacionadas ao subpacote
				List<Tarefas> tarefasRelacionadas = interfaceTarefas.findBySubpacotes_Id(subpacoteId);

				return new ResponseEntity<>(tarefasRelacionadas, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("A tarefa com o ID " + id + " não foi encontrada.", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Erro interno do servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//    // Método para obter todas as tarefas
//    @GetMapping
//    public ResponseEntity<List<Tarefas>> listarTodasTarefas() {
//        try {
//            List<Tarefas> tarefas = interfaceTarefas.findAll();
//            return new ResponseEntity<>(tarefas, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    // Método para obter uma tarefa pelo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> obterTarefaPorId(@PathVariable Long id) {
//        try {
//            Optional<Tarefas> tarefa = interfaceTarefas.findById(id);
//
//            if (tarefa.isPresent()) {
//                return new ResponseEntity<>(tarefa.get(), HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

	// PEGAR TODAS AS TAREFAS RELACIONADAS AO SUBPACOTE ID

	@GetMapping("/subpacote/{id}")
	public ResponseEntity<Object> obterTarefasPorSubpacoteId(@PathVariable Long id) {
		try {
			Optional<Subpacotes> subpacoteOptional = interfaceSubpacotes.findById(id);

			if (!subpacoteOptional.isPresent()) {
				return new ResponseEntity<>("O subpacote com o ID " + id + " não existe.", HttpStatus.NOT_FOUND);
			}

			List<Tarefas> tarefas = interfaceTarefas.findBySubpacotes_Id(id);

			if (tarefas.isEmpty()) {
				return new ResponseEntity<>("Não há tarefas relacionadas ao subpacote com o ID " + id + ".",
						HttpStatus.OK);
			}

			return new ResponseEntity<>(tarefas, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Erro interno do servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
