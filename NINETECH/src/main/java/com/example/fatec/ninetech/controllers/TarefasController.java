package com.example.fatec.ninetech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.LoggerProjetoPorcentagensReais;
import com.example.fatec.ninetech.models.LoggerSubpacotesPorcentagensReais;
import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.models.Tarefas;
import com.example.fatec.ninetech.repositories.LoggerProjetoInterface;
import com.example.fatec.ninetech.repositories.LoggerSubpacotesInterface;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import com.example.fatec.ninetech.repositories.TarefasInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tarefas")
public class TarefasController {

	@Autowired
	private TarefasInterface interfaceTarefas;

	@Autowired
	private SubpacotesInterface interfaceSubpacotes;

	@Autowired
	private PacotesInterface interfacePacotes;

	@Autowired
	private ProjetoInterface interfaceProjeto;

	@Autowired
	private LoggerProjetoInterface interfaceLoggerProjeto;

	@Autowired
	private LoggerSubpacotesInterface interfaceLoggerSubpacotes;

	@PostMapping
	public ResponseEntity<Object> cadastrar(@RequestBody Tarefas tarefas) {
		try {
			// Obter o ID do Subpacote da Tarefa
			Long subpacoteId = tarefas.getSubpacotes().getId();

			// ACHAR O ID DO PACOTE RELACIONADO AO SUBPACOTE DA TAREFA ADICIONADA
			/////////////////
			// Obter o Subpacote da Tarefa
			Optional<Subpacotes> subpacoteRelacionado = interfaceSubpacotes.findById(subpacoteId);

			Subpacotes subpacoteX = subpacoteRelacionado.get();

			// Obtém o Pacotes associado ao Subpacotes
			Pacotes pacotes = subpacoteX.getPacotes();

			Long pacoteId = pacotes.getId();
			////////////////

			// ACHAR O ID DO PROJETO, RELACIONADO AO PACOTE QUE ESTA RELACIONADO AO
			// SUBPACOTE QUE ESTA RELACIONADO A TAREFA INCLUIDA
			/////////////// ---------------------------------------------
			// Obter o Pacote do Subpcote
			Optional<Pacotes> pacoteRelacionado = interfacePacotes.findById(pacoteId);

			Pacotes pacoteX = pacoteRelacionado.get();

			// Obtem o Projeto associado ao Pacote
			Projeto projeto = pacoteX.getProjeto();

			Long projetoId = projeto.getId();
			/////////////// ---------------------------------------------

			Double valor_homem_hora = this.interfaceProjeto.findById(projetoId).get().getValor_homem_hora();

			// Calcular o valor com base na fórmula
			double valorCalculado = (tarefas.getHh() * valor_homem_hora) + tarefas.getMaterial();
			tarefas.setValor(valorCalculado);
			// Calcular a porcentagem com base na fórmula
			double execucaoNumerica = tarefas.getExecucao() ? 1 : 0; // 1 se for true, 0 se for false
			double porcentagemCalculada = ((execucaoNumerica * tarefas.getPeso()) / tarefas.getPeso()) * 100;
			tarefas.setPorcentagem(porcentagemCalculada);

			// salvando nova tarefa
			Tarefas novaTarefa = interfaceTarefas.save(tarefas);

			// Obter as tarefas relacionadas ao Subpacote indicado
			List<Tarefas> tarefasRelacionadas = interfaceTarefas.findBySubpacotes_Id(subpacoteId);

			// Obter Tarefas Relacionadas ao Pacote
			List<Tarefas> tarefasDoPacote = interfaceTarefas.findBySubpacotes_Pacotes_Id(pacoteId);

			// Obter Tarefas Relacionadas ao Projeto
			List<Tarefas> tarefasdoProjeto = interfaceTarefas.findBySubpacotes_Pacotes_Projeto_Id(projetoId);

			// Calcular a soma dos valores e pesos das tarefas relacionadas ao
			// Subpacote///////////////////////
			double somaValores = 0.0;
			double somaPesos = 0.0;
			double somaPesosTotal = 0.0;

			for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
				double execucaoNumericaTarefa = tarefaRelacionada.getExecucao() ? 1.0 : 0.0;
				somaValores += execucaoNumericaTarefa * tarefaRelacionada.getValor();
				somaPesos += execucaoNumericaTarefa * tarefaRelacionada.getPeso();
			}

			for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
				somaPesosTotal += tarefaRelacionada.getPeso();
			}

			// Calcular valor_total e porcentagem no Subpacote
			double valorTotalCalculado = somaValores;
			double porcentagemSubpacote = (somaPesos / somaPesosTotal) * 100.0;

			// Atualizar o Subpacote com os novos valores
			Optional<Subpacotes> subpacoteOptional = interfaceSubpacotes.findById(subpacoteId);

			Subpacotes subpacote = subpacoteOptional.get();

			subpacote.setValor_total(valorTotalCalculado);
			subpacote.setPorcentagem(porcentagemSubpacote);
			interfaceSubpacotes.save(subpacote);

			LoggerSubpacotesPorcentagensReais loggerSubpacote = new LoggerSubpacotesPorcentagensReais();
			loggerSubpacote.setData(LocalDate.now());
			loggerSubpacote.setPorcentagem(porcentagemSubpacote);
			loggerSubpacote.setProjeto(projeto);
			loggerSubpacote.setSubpacotes(subpacote);
			interfaceLoggerSubpacotes.save(loggerSubpacote);

			// Atualizar Valores do Pacote////////////////////////////////////
			double somaValoresPacote = 0.0;
			double somaPesosPacote = 0.0;
			double somaPesosTotalPacote = 0.0;

			for (Tarefas tarefaRelacionadaPacote : tarefasDoPacote) {
				double execucaoNumericaTarefaPacote = tarefaRelacionadaPacote.getExecucao() ? 1.0 : 0.0;
				somaValoresPacote += execucaoNumericaTarefaPacote * tarefaRelacionadaPacote.getValor();
				somaPesosPacote += execucaoNumericaTarefaPacote * tarefaRelacionadaPacote.getPeso();
			}

			for (Tarefas tarefaRelacionadaPacote : tarefasDoPacote) {
				somaPesosTotalPacote += tarefaRelacionadaPacote.getPeso();
			}

			// Calcular valor_total e porcentagem no Pacote
			double valorTotalCalculadoPacote = somaValoresPacote;
			double porcentagemSubpacotePacote = (somaPesosPacote / somaPesosTotalPacote) * valor_homem_hora;

			// Atualizar o Pacote com os novos valores
			Optional<Pacotes> pacoteOptional = interfacePacotes.findById(pacoteId);

			Pacotes pacote = pacoteOptional.get();
			pacote.setValor_total(valorTotalCalculadoPacote);
			pacote.setPorcentagem(porcentagemSubpacotePacote);
			interfacePacotes.save(pacote);

			// Atualizar Valores do PROJETO////////////////////////////////////
			double somaValoresProjeto = 0.0;
			double somaPesosProjeto = 0.0;
			double somaPesosTotalProjeto = 0.0;

			for (Tarefas tarefaRelacionadaProjeto : tarefasdoProjeto) {
				double execucaoNumericaTarefaProjeto = tarefaRelacionadaProjeto.getExecucao() ? 1.0 : 0.0;
				somaValoresProjeto += execucaoNumericaTarefaProjeto * tarefaRelacionadaProjeto.getValor();
				somaPesosProjeto += execucaoNumericaTarefaProjeto * tarefaRelacionadaProjeto.getPeso();
			}

			for (Tarefas tarefaRelacionadaProjeto : tarefasdoProjeto) {
				somaPesosTotalProjeto += tarefaRelacionadaProjeto.getPeso();
			}

			// Calcular valor_total e porcentagem no Pacote
			double valorTotalCalculadoProjeto = somaValoresProjeto;
			double porcentagemSubpacoteProjeto = (somaPesosProjeto / somaPesosTotalProjeto) * 100.0;

			// Atualizar o Pacote com os novos valores
			Optional<Projeto> projetoOptional = interfaceProjeto.findById(projetoId);

			Projeto projeto1 = projetoOptional.get();
			projeto1.setValor_total(valorTotalCalculadoProjeto);
			projeto1.setPorcentagem(porcentagemSubpacoteProjeto);
			interfaceProjeto.save(projeto1);

			LoggerProjetoPorcentagensReais loggerProjeto = new LoggerProjetoPorcentagensReais();
			loggerProjeto.setData(LocalDate.now());
			loggerProjeto.setPorcentagem(porcentagemSubpacoteProjeto);
			loggerProjeto.setProjeto(projeto1);
			interfaceLoggerProjeto.save(loggerProjeto);

			// Verifica se a data da nova tarefa é maior que a data_final do projeto
			LocalDate dataTarefa = tarefas.getData();
			LocalDate dataFinalProjeto = projeto1.getData_final();

			if (dataTarefa.isAfter(dataFinalProjeto)) {
				// Atualiza a data_final do projeto com a data da nova tarefa
				projeto1.setData_final(dataTarefa);
				interfaceProjeto.save(projeto1);
			}

			return new ResponseEntity<>(novaTarefa, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Erro ao processar a requisição: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
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

				// Calcular a porcentagem com base na fórmula
				double execucaoNumerica = tarefaAtualizada.getExecucao() ? 1 : 0; // 1 se for true, 0 se for false
				double porcentagemCalculada = (execucaoNumerica * tarefaAtualizada.getPeso())
						/ tarefaAtualizada.getPeso();
				tarefa.setPorcentagem(porcentagemCalculada);

				Tarefas tarefaAtualizadaNoBanco = interfaceTarefas.save(tarefa);

				// Obter o ID do Subpacote da Tarefa
				Long subpacoteId = tarefa.getSubpacotes().getId();

				// ACHAR O ID DO PACOTE RELACIONADO AO SUBPACOTE DA TAREFA ADICIONADA
				/////////////////
				// Obter o Subpacote da Tarefa
				Optional<Subpacotes> subpacoteRelacionado = interfaceSubpacotes.findById(subpacoteId);

				Subpacotes subpacoteX = subpacoteRelacionado.get();

				// Obtém o Pacotes associado ao Subpacotes
				Pacotes pacotes = subpacoteX.getPacotes();

				Long pacoteId = pacotes.getId();
				////////////////

				// ACHAR O ID DO PROJETO, RELACIONADO AO PACOTE QUE ESTA RELACIONADO AO
				// SUBPACOTE QUE ESTA RELACIONADO A TAREFA INCLUIDA
				/////////////// ---------------------------------------------
				// Obter o Pacote do Subpcote
				Optional<Pacotes> pacoteRelacionado = interfacePacotes.findById(pacoteId);

				Pacotes pacoteX = pacoteRelacionado.get();

				// Obtem o Projeto associado ao Pacote
				Projeto projeto = pacoteX.getProjeto();

				Long projetoId = projeto.getId();
				/////////////// ---------------------------------------------

				Double valor_homem_hora = this.interfaceProjeto.findById(projetoId).get().getValor_homem_hora();

				// Calcular o valor com base na fórmula
				double valorCalculado = (tarefaAtualizada.getHh() * valor_homem_hora) + tarefaAtualizada.getMaterial();
				tarefa.setValor(valorCalculado);

				// Obter as tarefas relacionadas ao Subpacote indicado
				List<Tarefas> tarefasRelacionadas = interfaceTarefas.findBySubpacotes_Id(subpacoteId);

				// Obter os Subpacotes relacionado ao Pacote achado no SubPacote
				List<Subpacotes> subpacotesRelacionados = interfaceSubpacotes.findByPacotesId(pacoteId);

				// Obter os Pacotes relacionados ao Projeto achado no Pacote
				List<Pacotes> pacotesRelacionados = interfacePacotes.findByProjeto_Id(projetoId);

				// Obter Tarefas Relacionadas ao Pacote
				List<Tarefas> tarefasDoPacote = interfaceTarefas.findBySubpacotes_Pacotes_Id(pacoteId);

				// Obter Tarefas Relacionadas ao Projeto
				List<Tarefas> tarefasdoProjeto = interfaceTarefas.findBySubpacotes_Pacotes_Projeto_Id(projetoId);

				// Calcular a soma dos valores e pesos das tarefas relacionadas ao
				// Subpacote///////////////////////
				double somaValores = 0.0;
				double somaPesos = 0.0;
				double somaPesosTotal = 0.0;

				for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
					double execucaoNumericaTarefa = tarefaRelacionada.getExecucao() ? 1.0 : 0.0;
					somaValores += execucaoNumericaTarefa * tarefaRelacionada.getValor();
					somaPesos += execucaoNumericaTarefa * tarefaRelacionada.getPeso();
				}

				for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
					somaPesosTotal += tarefaRelacionada.getPeso();
				}

				// Calcular valor_total e porcentagem no Subpacote
				double valorTotalCalculado = somaValores;
				double porcentagemSubpacote = (somaPesos / somaPesosTotal) * 100.0;

				// Atualizar o Subpacote com os novos valores
				Optional<Subpacotes> subpacoteOptional = interfaceSubpacotes.findById(subpacoteId);

				Subpacotes subpacote = subpacoteOptional.get();
				subpacote.setValor_total(valorTotalCalculado);
				subpacote.setPorcentagem(porcentagemSubpacote);
				interfaceSubpacotes.save(subpacote);

				LoggerSubpacotesPorcentagensReais loggerSubpacote = new LoggerSubpacotesPorcentagensReais();
				loggerSubpacote.setData(LocalDate.now());
				loggerSubpacote.setPorcentagem(porcentagemSubpacote);
				loggerSubpacote.setProjeto(projeto);
				loggerSubpacote.setSubpacotes(subpacote);
				interfaceLoggerSubpacotes.save(loggerSubpacote);

				// Atualizar Valores do Pacote////////////////////////////////////
				double somaValoresPacote = 0.0;
				double somaPesosPacote = 0.0;
				double somaPesosTotalPacote = 0.0;

				for (Tarefas tarefaRelacionadaPacote : tarefasDoPacote) {
					double execucaoNumericaTarefaPacote = tarefaRelacionadaPacote.getExecucao() ? 1.0 : 0.0;
					somaValoresPacote += execucaoNumericaTarefaPacote * tarefaRelacionadaPacote.getValor();
					somaPesosPacote += execucaoNumericaTarefaPacote * tarefaRelacionadaPacote.getPeso();
				}

				for (Tarefas tarefaRelacionadaPacote : tarefasDoPacote) {
					somaPesosTotalPacote += tarefaRelacionadaPacote.getPeso();
				}

				// Calcular valor_total e porcentagem no Pacote
				double valorTotalCalculadoPacote = somaValoresPacote;
				double porcentagemSubpacotePacote = (somaPesosPacote / somaPesosTotalPacote) * 100.0;

				// Atualizar o Pacote com os novos valores
				Optional<Pacotes> pacoteOptional = interfacePacotes.findById(pacoteId);

				Pacotes pacote = pacoteOptional.get();
				pacote.setValor_total(valorTotalCalculadoPacote);
				pacote.setPorcentagem(porcentagemSubpacotePacote);
				interfacePacotes.save(pacote);

				// Atualizar Valores do PROJETO////////////////////////////////////
				double somaValoresProjeto = 0.0;
				double somaPesosProjeto = 0.0;
				double somaPesosTotalProjeto = 0.0;

				for (Tarefas tarefaRelacionadaProjeto : tarefasdoProjeto) {
					double execucaoNumericaTarefaProjeto = tarefaRelacionadaProjeto.getExecucao() ? 1.0 : 0.0;
					somaValoresProjeto += execucaoNumericaTarefaProjeto * tarefaRelacionadaProjeto.getValor();
					somaPesosProjeto += execucaoNumericaTarefaProjeto * tarefaRelacionadaProjeto.getPeso();
				}

				for (Tarefas tarefaRelacionadaProjeto : tarefasdoProjeto) {
					somaPesosTotalProjeto += tarefaRelacionadaProjeto.getPeso();
				}

				// Calcular valor_total e porcentagem no Pacote
				double valorTotalCalculadoProjeto = somaValoresProjeto;
				double porcentagemSubpacoteProjeto = (somaPesosProjeto / somaPesosTotalProjeto) * 100.0;

				// Atualizar o Pacote com os novos valores
				Optional<Projeto> projetoOptional = interfaceProjeto.findById(projetoId);

				Projeto projeto1 = projetoOptional.get();
				projeto1.setValor_total(valorTotalCalculadoProjeto);
				projeto1.setPorcentagem(porcentagemSubpacoteProjeto);
				interfaceProjeto.save(projeto1);

				LoggerProjetoPorcentagensReais loggerProjeto = new LoggerProjetoPorcentagensReais();
				loggerProjeto.setData(LocalDate.now());
				loggerProjeto.setPorcentagem(porcentagemSubpacoteProjeto);
				loggerProjeto.setProjeto(projeto1);
				interfaceLoggerProjeto.save(loggerProjeto);

				return new ResponseEntity<>(tarefaAtualizadaNoBanco, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Erro ao processar a requisição: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
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

				Optional<Subpacotes> subpacoteRelacionado = interfaceSubpacotes.findById(subpacoteId);

				Subpacotes subpacoteX = subpacoteRelacionado.get();

				// Obtém o Pacotes associado ao Subpacotes
				Pacotes pacotes = subpacoteX.getPacotes();

				Long pacoteId = pacotes.getId();

				// ACHAR O ID DO PROJETO, RELACIONADO AO PACOTE QUE ESTA RELACIONADO AO
				// SUBPACOTE QUE ESTA RELACIONADO A TAREFA INCLUIDA
				/////////////// ---------------------------------------------
				// Obter o Pacote do Subpcote
				Optional<Pacotes> pacoteRelacionado = interfacePacotes.findById(pacoteId);

				Pacotes pacoteX = pacoteRelacionado.get();

				// Obtem o Projeto associado ao Pacote
				Projeto projeto = pacoteX.getProjeto();

				Long projetoId = projeto.getId();
				/////////////// ---------------------------------------------

				interfaceTarefas.deleteById(id);

				// Buscar todas as tarefas relacionadas ao subpacote
				List<Tarefas> tarefasRelacionadas = interfaceTarefas.findBySubpacotes_Id(subpacoteId);

				// Obter Tarefas Relacionadas ao Pacote
				List<Tarefas> tarefasDoPacote = interfaceTarefas.findBySubpacotes_Pacotes_Id(pacoteId);

				// Obter Tarefas Relacionadas ao Projeto
				List<Tarefas> tarefasdoProjeto = interfaceTarefas.findBySubpacotes_Pacotes_Projeto_Id(projetoId);

				// ----------------------------------------------------------------------------------------------------------

				// Calcular a soma dos valores e pesos das tarefas relacionadas ao
				// Subpacote///////////////////////
				double somaValores = 0.0;
				double somaPesos = 0.0;
				double somaPesosTotal = 0.0;

				for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
					double execucaoNumericaTarefa = tarefaRelacionada.getExecucao() ? 1.0 : 0.0;
					somaValores += execucaoNumericaTarefa * tarefaRelacionada.getValor();
					somaPesos += execucaoNumericaTarefa * tarefaRelacionada.getPeso();
				}

				for (Tarefas tarefaRelacionada : tarefasRelacionadas) {
					somaPesosTotal += tarefaRelacionada.getPeso();
				}

				// Calcular valor_total e porcentagem no Subpacote
				double valorTotalCalculado = somaValores;
				double porcentagemSubpacote = (somaPesos / somaPesosTotal) * 100.0;

				// Atualizar o Subpacote com os novos valores
				Optional<Subpacotes> subpacoteOptional = interfaceSubpacotes.findById(subpacoteId);

				Subpacotes subpacote = subpacoteOptional.get();
				subpacote.setValor_total(valorTotalCalculado);
				subpacote.setPorcentagem(porcentagemSubpacote);
				interfaceSubpacotes.save(subpacote);

				// Atualizar Valores do Pacote////////////////////////////////////
				double somaValoresPacote = 0.0;
				double somaPesosPacote = 0.0;
				double somaPesosTotalPacote = 0.0;

				for (Tarefas tarefaRelacionadaPacote : tarefasDoPacote) {
					double execucaoNumericaTarefaPacote = tarefaRelacionadaPacote.getExecucao() ? 1.0 : 0.0;
					somaValoresPacote += execucaoNumericaTarefaPacote * tarefaRelacionadaPacote.getValor();
					somaPesosPacote += execucaoNumericaTarefaPacote * tarefaRelacionadaPacote.getPeso();
				}

				for (Tarefas tarefaRelacionadaPacote : tarefasDoPacote) {
					somaPesosTotalPacote += tarefaRelacionadaPacote.getPeso();
				}

				// Calcular valor_total e porcentagem no Pacote
				double valorTotalCalculadoPacote = somaValoresPacote;
				double porcentagemSubpacotePacote = (somaPesosPacote / somaPesosTotalPacote) * 100.0;

				// Atualizar o Pacote com os novos valores
				Optional<Pacotes> pacoteOptional = interfacePacotes.findById(pacoteId);

				Pacotes pacote = pacoteOptional.get();
				pacote.setValor_total(valorTotalCalculadoPacote);
				pacote.setPorcentagem(porcentagemSubpacotePacote);
				interfacePacotes.save(pacote);

				// Atualizar Valores do PROJETO////////////////////////////////////
				double somaValoresProjeto = 0.0;
				double somaPesosProjeto = 0.0;
				double somaPesosTotalProjeto = 0.0;

				for (Tarefas tarefaRelacionadaProjeto : tarefasdoProjeto) {
					double execucaoNumericaTarefaProjeto = tarefaRelacionadaProjeto.getExecucao() ? 1.0 : 0.0;
					somaValoresProjeto += execucaoNumericaTarefaProjeto * tarefaRelacionadaProjeto.getValor();
					somaPesosProjeto += execucaoNumericaTarefaProjeto * tarefaRelacionadaProjeto.getPeso();
				}

				for (Tarefas tarefaRelacionadaProjeto : tarefasdoProjeto) {
					somaPesosTotalProjeto += tarefaRelacionadaProjeto.getPeso();
				}

				// Calcular valor_total e porcentagem no Pacote
				double valorTotalCalculadoProjeto = somaValoresProjeto;
				double porcentagemSubpacoteProjeto = (somaPesosProjeto / somaPesosTotalProjeto) * 100.0;

				// Atualizar o Pacote com os novos valores
				Optional<Projeto> projetoOptional = interfaceProjeto.findById(projetoId);

				Projeto projeto1 = projetoOptional.get();
				projeto1.setValor_total(valorTotalCalculadoProjeto);
				projeto1.setPorcentagem(porcentagemSubpacoteProjeto);
				interfaceProjeto.save(projeto1);

				return new ResponseEntity<>(tarefasRelacionadas, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("A tarefa com o ID " + id + " não foi encontrada.", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Erro ao processar a requisição: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// // Método para obter todas as tarefas
	// @GetMapping
	// public ResponseEntity<List<Tarefas>> listarTodasTarefas() {
	// try {
	// List<Tarefas> tarefas = interfaceTarefas.findAll();
	// return new ResponseEntity<>(tarefas, HttpStatus.OK);
	// } catch (Exception e) {
	// return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	// }

	// // Método para obter uma tarefa pelo ID
	// @GetMapping("/{id}")
	// public ResponseEntity<Object> obterTarefaPorId(@PathVariable Long id) {
	// try {
	// Optional<Tarefas> tarefa = interfaceTarefas.findById(id);
	//
	// if (tarefa.isPresent()) {
	// return new ResponseEntity<>(tarefa.get(), HttpStatus.OK);
	// } else {
	// return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	// }
	// } catch (Exception e) {
	// return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	// }

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
