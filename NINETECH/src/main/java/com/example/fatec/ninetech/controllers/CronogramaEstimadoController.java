package com.example.fatec.ninetech.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.CronogramaEstimadoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.WBSInterface;

import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/cronograma")
public class CronogramaEstimadoController {
	
    @Autowired
    private CronogramaEstimadoInterface cronogramaEstimadoInterface;

    @Autowired
    private ProjetoInterface projetoInterface;
    
	@Autowired
	private WBSInterface wbeInterface;

	@PostMapping("/criar")
	public ResponseEntity<String> criarCronogramaEstimado(@RequestBody CronogramaEstimado request) {
	    // Validações iniciais
	    if (request == null || request.getProjeto() == null) {
	        return ResponseEntity.badRequest().body("CronogramaEstimado não está associado a nenhum projeto.");
	    }

	    Projeto projeto = request.getProjeto();
	    Long projetoId = projeto.getId();

	    Optional<Projeto> projetoOptional = projetoInterface.findById(projetoId);

	    Projeto projetoExistente = null;

	    if (projetoOptional.isPresent()) {
	        projetoExistente = projetoOptional.get();
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
	    }
	    
	    List<List<Integer>> porcentagens = request.getPorcentagens();

	    // Obtém a lista de IDs de WBE associados ao projeto
	    List<WBE> wbesDoProjeto = wbeInterface.findByProjetoId(projetoId);

	    if (wbesDoProjeto.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Não existem WBEs associados a este projeto.");
	    }

	    // Calcula a quantidade de meses
	    int meses = calcularQuantidadeMeses(projetoExistente.getData_inicio(), projetoExistente.getData_final());

	 // Para cada WBE associado ao projeto
	    for (int wbeIndex = 0; wbeIndex < wbesDoProjeto.size(); wbeIndex++) {
	        WBE wbe = wbesDoProjeto.get(wbeIndex);

	        // Obtenha as porcentagens correspondentes ao WBE atual
	        List<Integer> porcentagensDoWBE = porcentagens.get(wbeIndex);

	        // Verifique se a lista de porcentagensDoWBE tem a quantidade correta de meses
	        if (porcentagensDoWBE.size() != meses) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("A quantidade de porcentagens para o WBE " + wbe.getId() + " não corresponde à duração do projeto.");
	        }

	        for (int i = 0; i < meses; i++) {
	            CronogramaEstimado novoCronograma = new CronogramaEstimado();
	            novoCronograma.setProjeto(projetoExistente);
	            novoCronograma.getWBE().add(wbe);
	            novoCronograma.setMes(i + 1);
	            int porcentagem = porcentagensDoWBE.get(i);
	            novoCronograma.setWbeId(wbe.getId());
	            novoCronograma.setPorcentagem(porcentagem);

	            cronogramaEstimadoInterface.save(novoCronograma);
	        }
	    }

	    return ResponseEntity.ok("Cronograma criado com sucesso!");
	}

	private int calcularQuantidadeMeses(LocalDate dataInicio, LocalDate dataFim) {
	    int anos = dataFim.getYear() - dataInicio.getYear();
	    int meses = dataFim.getMonthValue() - dataInicio.getMonthValue();
	    int dias = dataFim.getDayOfMonth() - dataInicio.getDayOfMonth();

	    // Se houver dias restantes no último mês, conte-o como um mês completo
	    if (dias > 0) {
	        meses++;
	    }

	    // Some os meses de anos completos
	    int totalMeses = (anos * 12) + meses;
	    return totalMeses;
	}
	
	@GetMapping("/cronograma-por-wbe/{projetoId}")
	public Map<Long, Map<String, Object>> getCronogramaPorWBE(@PathVariable Long projetoId) {
	    // Consulta ao banco de dados para recuperar os registros com base no projetoId
	    List<CronogramaEstimado> cronogramas = cronogramaEstimadoInterface.findByProjetoId(projetoId);

	    // Processamento para agrupar as porcentagens por wbe_id e obter o nome do wbe
	    Map<Long, Map<String, Object>> cronogramaPorWBE = new HashMap<>();
	    for (CronogramaEstimado cronograma : cronogramas) {
	        Long wbeId = cronograma.getWbeId();
	        Integer porcentagem = cronograma.getPorcentagem();

	        // Verifique se o wbeId já existe no mapa
	        if (!cronogramaPorWBE.containsKey(wbeId)) {
	            Map<String, Object> wbeInfo = new HashMap<>();
	            cronogramaPorWBE.put(wbeId, wbeInfo);

	            // Consulta ao banco de dados para obter o nome do wbe
	            WBE wbe = wbeInterface.findById(wbeId).orElse(null);
	            if (wbe != null) {
	                wbeInfo.put("nome", wbe.getWbe());
	                wbeInfo.put("porcentagens", new ArrayList<>()); // Inicialize a lista de porcentagens
	            }
	        }

	        // Adicione a porcentagem à lista correspondente ao wbe_id
	        Map<String, Object> wbeInfo = cronogramaPorWBE.get(wbeId);
	        if (wbeInfo != null) {
	            List<Integer> porcentagens = (List<Integer>) wbeInfo.get("porcentagens");
	            porcentagens.add(porcentagem);
	        }
	    }

	    return cronogramaPorWBE;
	}
	
    @Transactional
    @DeleteMapping("/deletar/{projetoId}")
    public ResponseEntity<String> deletarCronogramasByProjetoId(@PathVariable Long projetoId) {
        Optional<Projeto> projetoOptional = projetoInterface.findById(projetoId);

        if (!projetoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }
        
        Projeto projetoExistente = projetoOptional.get();

        // Deletar todos os cronogramas associados ao projeto
        cronogramaEstimadoInterface.deleteByProjeto(projetoExistente);

        return ResponseEntity.ok("Cronogramas do projeto foram deletados com sucesso.");
    }

}