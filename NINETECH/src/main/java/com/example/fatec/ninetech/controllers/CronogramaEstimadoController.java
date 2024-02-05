package com.example.fatec.ninetech.controllers;

import com.example.fatec.ninetech.helpers.CronogramaEstimadoPostRequest;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoRequest;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoResponse;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoResponseDTO;
import com.example.fatec.ninetech.helpers.CronogramaProjetoEstimadoResponse;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.CronogramaProjetoEstimado;
import com.example.fatec.ninetech.models.LoggerProjetoPorcentagensReais;
import com.example.fatec.ninetech.models.LoggerSubpacotesPorcentagensReais;
import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.repositories.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RestController
@RequestMapping("/cronograma")
public class CronogramaEstimadoController {

	@PersistenceContext
	private EntityManager em; // Injeta o EntityManager

	@Autowired
	private CronogramaEstimadoInterface cronogramaEstimadoInterface;

	@Autowired
	private SubpacotesInterface subpacotesInterface;

	@Autowired
	private ProjetoInterface projetoInterface;

	@Autowired
	private PacotesInterface wbeInterface;

	@Autowired
	private CronogramaProjetoEstimadoInterface CronogramaProjetoEstimadoInterface;

	@Autowired
	private LoggerSubpacotesInterface interfaceLoggerSubpacotes;

	@Autowired
	private LoggerProjetoInterface interfaceLoggerProjeto;

	@Transactional
	@PostMapping("/{id_subpacote}")
	public ResponseEntity<String> criarCronogramaEstimado(@PathVariable("id_subpacote") Long id_subpacote,
			@RequestBody CronogramaEstimadoRequest request) {
		try {
			Optional<Subpacotes> subpacote_query = this.subpacotesInterface.findById(id_subpacote);
			Optional<Projeto> projeto_query = this.projetoInterface.findById(request.getId_projeto());

			if (subpacote_query.isPresent() && projeto_query.isPresent()) {
				Subpacotes subpacote = subpacote_query.get();
				Projeto projeto = projeto_query.get();
				List<Integer> porcentagens = request.getPorcentagens();

				int mes = 1; // Começa no mês 1
				int mesMaximoProjeto = 0; // Inicializa a variável

				for (Integer porcentagem : porcentagens) {
					CronogramaEstimado novoCronogramaEstimado = new CronogramaEstimado(mes, porcentagem, projeto,
							subpacote);

					this.cronogramaEstimadoInterface.save(novoCronogramaEstimado);

					// Atualize a variável mesMaximoProjeto se o mês atual for maior do que o valor
					// anterior
					mesMaximoProjeto = calcularMesMaximoProjeto(projeto.getId());

					mes++; // Incrementa o mês automaticamente
				}

				// Atualize a variável MesMaximoProjeto no projeto
				projeto.setMesMaximoProjeto(mesMaximoProjeto);

				// Atualize o número total de meses no projeto com base no maior número de meses
				// dos subpacotes
				projeto.setNumeroTotalMeses(Math.max(projeto.getNumeroTotalMeses(), mesMaximoProjeto));
				this.projetoInterface.save(projeto);

				calcularPorcentagemMediaPorMes(request.getId_projeto());
				return ResponseEntity.ok("Cronograma criado com sucesso!");
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projeto ou subpacote não existente");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Um erro ocorreu: " + e);
		}
	}

	// calculo o numero de meses maximo do projeto com base no cronograma do
	// subpacote
	@Transactional
	private int calcularMesMaximoProjeto(Long idProjeto) {
		// Consulte os subpacotes do projeto que correspondem ao projeto_id
		List<CronogramaEstimado> subpacotesDoProjeto2 = this.cronogramaEstimadoInterface.findByProjetoId(idProjeto);

		int mesMaximoProjeto = 0;

		// Encontre o maior valor de mes entre os subpacotes do projeto
		for (CronogramaEstimado cronogramaEstimado : subpacotesDoProjeto2) {
			mesMaximoProjeto = Math.max(mesMaximoProjeto, cronogramaEstimado.getMes());
		}

		return mesMaximoProjeto;
	}

	// Após criar os objetos CronogramaEstimado, você pode calcular a média das
	// porcentagens para cada mês:
	@Transactional
	private void calcularPorcentagemMediaPorMes(Long idProjeto) {
		// Consulte os subpacotes do projeto que correspondem ao projeto_id
		List<CronogramaEstimado> subpacotesDoProjeto3 = this.cronogramaEstimadoInterface.findByProjetoId(idProjeto);

		// Use o Java Stream API para agrupar os registros por mês
		Map<Integer, List<CronogramaEstimado>> porMes = subpacotesDoProjeto3.stream()
				.collect(Collectors.groupingBy(CronogramaEstimado::getMes));

		for (Map.Entry<Integer, List<CronogramaEstimado>> entry : porMes.entrySet()) {
			int mes = entry.getKey();
			System.out.println("mes");
			System.out.println(mes);
			List<CronogramaEstimado> cronogramasDoMes = entry.getValue();

			// Soma as porcentagens para o mês atual em todos os subpacotes
			int somaPorcentagens = cronogramasDoMes.stream().mapToInt(CronogramaEstimado::getPorcentagem).sum();
			System.out.println("soma porcentagens");
			System.out.println(somaPorcentagens);

			// Consulte os subpacotes do projeto que correspondem ao projeto_id
			List<CronogramaEstimado> subpacotesDoProjeto = this.cronogramaEstimadoInterface.findByProjetoId(idProjeto);

			// Use um conjunto (Set) para armazenar os id_subpacotes diferentes
			Set<Long> idSubpacotesDiferentes = subpacotesDoProjeto.stream()
					.map(cronogramaEstimado -> cronogramaEstimado.getSubpacote().getId()).collect(Collectors.toSet());

			// Agora você tem um conjunto de id_subpacotes diferentes
			int quantidadeIdSubpacotesDiferentes = idSubpacotesDiferentes.size();

			System.out.println("Quantidade de id_subpacotes diferentes relacionados ao projeto_id: "
					+ quantidadeIdSubpacotesDiferentes);

			// Calcula a porcentagem média para o mês atual
			double porcentagemMedia = (double) somaPorcentagens / quantidadeIdSubpacotesDiferentes;
			System.out.println("conta da porcentagem media");
			System.out.println(porcentagemMedia);

			// Verifique se já existe um registro para o mês atual e projeto_id
			CronogramaProjetoEstimado registroExistente = this.CronogramaProjetoEstimadoInterface
					.findByMesAndProjetoId(mes, idProjeto);
			System.out.println(" resgistro existente");
			System.out.println(registroExistente);

			if (registroExistente != null) {
				System.out.println("Conta if");
				System.out.println(porcentagemMedia);

				// Se já existe, atualize o valor da porcentagem
				if (!em.contains(registroExistente)) {
					registroExistente = em.merge(registroExistente); // Mesclar a entidade para gerenciamento do JPA
				}
				registroExistente.setPorcentagem(porcentagemMedia);

				// Sincronize as alterações com o banco de dados
				em.flush();

				System.out.println("Registro após a sincronização com o banco de dados:");
				System.out.println(registroExistente);
			} else {
				// Se não existe, crie um novo registro
				Projeto projeto = this.projetoInterface.findById(idProjeto).orElse(null);
				System.out.println("Conta else");
				System.out.println(porcentagemMedia);
				if (projeto != null) {
					CronogramaProjetoEstimado cronogramaProjetoMedia = new CronogramaProjetoEstimado(mes,
							porcentagemMedia, projeto);
					this.CronogramaProjetoEstimadoInterface.save(cronogramaProjetoMedia);
				}
			}

		}
	}

	@Transactional
	@PutMapping("/{id_subpacote}")
	public ResponseEntity<String> atualizarCronogramaEstimado(@PathVariable("id_subpacote") Long id_subpacote,
			@RequestBody CronogramaEstimadoRequest request) {
		try {
			Optional<Subpacotes> subpacote_query = this.subpacotesInterface.findById(id_subpacote);
			Optional<Projeto> projeto_query = this.projetoInterface.findById(request.getId_projeto());

			if (subpacote_query.isPresent() && projeto_query.isPresent()) {
				Subpacotes subpacote = subpacote_query.get();
				Projeto projeto = projeto_query.get();
				List<Integer> porcentagens = request.getPorcentagens();
				System.out.println("lista" + porcentagens);
				System.out.println("lista" + porcentagens.toString());

				// Consulte os cronogramas existentes para o id_subpacote
				List<CronogramaEstimado> cronogramasExistentes = this.cronogramaEstimadoInterface
						.findByProjetoIdAndSubpacoteId(projeto.getId(), subpacote.getId());

				// Atualiza os cronogramas existentes com base nos novos valores
				for (int mes = 1; mes <= porcentagens.size(); mes++) {
					int porcentagem = porcentagens.get(mes - 1);
					System.out.println("porcent" + porcentagem);

					int finalMes = mes; // Criar uma cópia efetivamente final de mes
					Optional<CronogramaEstimado> cronogramaExistente = cronogramasExistentes.stream()
							.filter(c -> c.getMes() == finalMes).findFirst();

					if (cronogramaExistente.isPresent()) {
						CronogramaEstimado cronograma = cronogramaExistente.get();
						cronograma.setPorcentagem(porcentagem);
						this.cronogramaEstimadoInterface.save(cronograma);
					} else {
						CronogramaEstimado novoCronogramaEstimado = new CronogramaEstimado(mes, porcentagem, projeto,
								subpacote);
						this.cronogramaEstimadoInterface.save(novoCronogramaEstimado);
					}
				}

				// Remova os cronogramas que não estão mais presentes no novo conjunto de
				// porcentagens
				for (CronogramaEstimado cronograma : cronogramasExistentes) {
					if (cronograma.getMes() > porcentagens.size()) {
						this.cronogramaEstimadoInterface.delete(cronograma);
					}
				}

				// Atualize o mês máximo do projeto
				projeto.setMesMaximoProjeto(porcentagens.size());

				// Recalcule a porcentagem média por mês
				calcularPorcentagemMediaPorMes(request.getId_projeto());

				return ResponseEntity.ok("Cronograma atualizado com sucesso!");
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projeto ou subpacote não existente");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Um erro ocorreu: " + e);
		}
	}

	@GetMapping("/{id_subpacote}")
	public ResponseEntity<CronogramaEstimadoResponseDTO> getCronograma(
			@PathVariable("id_subpacote") Long id_subpacote) {
		try {
			List<CronogramaEstimado> cronogramas = this.cronogramaEstimadoInterface.findBySubpacoteId(id_subpacote);

			if (cronogramas.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			// Obtenha o Projeto e o Subpacote associados aos cronogramas
			Projeto projeto = cronogramas.get(0).getProjeto();
			Subpacotes subpacote = cronogramas.get(0).getSubpacote();

			// Construa o objeto CronogramaEstimadoResponseDTO com os valores dos meses
			// dinâmicos
			CronogramaEstimadoResponseDTO responseDTO = new CronogramaEstimadoResponseDTO(cronogramas.get(0).getId(),
					projeto, subpacote, cronogramas);

			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/cronogramaestimado/{id_subpacote}")
	public ResponseEntity<List<CronogramaEstimadoResponse>> getCronogramaEstimadoBySubpacoteId(
			@PathVariable("id_subpacote") Long id_subpacote) {
		try {
			List<CronogramaEstimado> cronogramas = this.cronogramaEstimadoInterface.findBySubpacoteId(id_subpacote);

			if (cronogramas.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
			}

			// Mapeie os objetos CronogramaEstimado para um DTO (Data Transfer Object)
			// personalizado
			List<CronogramaEstimadoResponse> responseList = cronogramas.stream()
					.map(cronograma -> new CronogramaEstimadoResponse(cronograma.getMes(), cronograma.getPorcentagem()))
					.collect(Collectors.toList());

			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Collections.emptyList());
		}
	}

	@GetMapping("/cronogramaprojetoestimado/{id_projeto}")
	public ResponseEntity<List<CronogramaProjetoEstimadoResponse>> getCronogramaProjetoEstimadoByProjetoId(
			@PathVariable("id_projeto") Long id_projeto) {
		try {
			List<CronogramaProjetoEstimado> cronogramasProjetoEstimado = this.CronogramaProjetoEstimadoInterface
					.findByProjetoId(id_projeto);

			if (cronogramasProjetoEstimado.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
			}

			// Mapeie os objetos CronogramaProjetoEstimado para um DTO (Data Transfer
			// Object) personalizado
			List<CronogramaProjetoEstimadoResponse> responseList = cronogramasProjetoEstimado.stream()
					.map(cronogramaProjeto -> new CronogramaProjetoEstimadoResponse(cronogramaProjeto.getMes(),
							cronogramaProjeto.getPorcentagem()))
					.collect(Collectors.toList());

			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Collections.emptyList());
		}
	}

	/*
	 * @GetMapping("/{id_subpacote}") public ResponseEntity<?> getCronograma(
	 * 
	 * @PathVariable("id_subpacote") Long id_subpacote ) { try {
	 * List<CronogramaEstimado> cronogramaEstimado =
	 * this.cronogramaEstimadoInterface.findBySubpacoteId(id_subpacote);
	 * 
	 * if (cronogramaEstimado.isEmpty()) { return
	 * ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimado); }
	 * 
	 * System.out.println(cronogramaEstimado);
	 * 
	 * return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimado); } catch
	 * (Exception e) { return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e); }
	 * };
	 */

	@GetMapping("/cronogramaestimado/ultimosdias/{id_subpacote}")
	public ResponseEntity<List<CronogramaEstimadoResponse>> getUltimosDiasCronogramaEstimadoBySubpacoteId(
			@PathVariable("id_subpacote") Long id_subpacote) {
		try {
			List<LoggerSubpacotesPorcentagensReais> cronogramas = this.interfaceLoggerSubpacotes
					.findBySubpacotesId(id_subpacote);

			if (cronogramas.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
			}

			// Encontre datas únicas
			List<String> datasUnicas = cronogramas.stream()
					.map(cronograma -> cronograma.getData().format(DateTimeFormatter.ofPattern("yyyy-MM"))).distinct()
					.sorted().collect(Collectors.toList());

			// Encontre a maior porcentagem para cada data única
			List<CronogramaEstimadoResponse> responseList = new ArrayList<>();
			for (String dataUnica : datasUnicas) {
				Double maiorPorcentagem = cronogramas.stream()
						.filter(cronograma -> cronograma.getData().format(DateTimeFormatter.ofPattern("yyyy-MM"))
								.equals(dataUnica))
						.map(LoggerSubpacotesPorcentagensReais::getPorcentagem).max(Double::compareTo).orElse(0.0);
				responseList.add(new CronogramaEstimadoResponse(responseList.size() + 1, maiorPorcentagem));
			}

			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Collections.emptyList());
		}
	}

	@GetMapping("/cronogramaprojetoestimado/ultimosdias/{id_projeto}")
	public ResponseEntity<List<CronogramaEstimadoResponse>> getUltimosDiasCronogramaEstimadoByProjetoId(
			@PathVariable("id_projeto") Long id_projeto) {
		try {
			List<LoggerProjetoPorcentagensReais> cronogramas = this.interfaceLoggerProjeto.findByProjetoId(id_projeto);

			if (cronogramas.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
			}

			// Encontre datas únicas
			List<String> datasUnicas = cronogramas.stream()
					.map(cronograma -> cronograma.getData().format(DateTimeFormatter.ofPattern("yyyy-MM"))).distinct()
					.sorted().collect(Collectors.toList());

			// Encontre a maior porcentagem para cada data única
			List<CronogramaEstimadoResponse> responseList = new ArrayList<>();
			for (String dataUnica : datasUnicas) {
				Double maiorPorcentagem = cronogramas.stream()
						.filter(cronograma -> cronograma.getData().format(DateTimeFormatter.ofPattern("yyyy-MM"))
								.equals(dataUnica))
						.map(LoggerProjetoPorcentagensReais::getPorcentagem).max(Double::compareTo).orElse(0.0);
				responseList.add(new CronogramaEstimadoResponse(responseList.size() + 1, maiorPorcentagem));
			}

			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Collections.emptyList());
		}
	}

}
