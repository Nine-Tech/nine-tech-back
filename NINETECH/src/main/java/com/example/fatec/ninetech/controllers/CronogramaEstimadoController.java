package com.example.fatec.ninetech.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.repositories.CronogramaEstimadoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;

import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/cronograma")
public class CronogramaEstimadoController {

    @Autowired
    private CronogramaEstimadoInterface cronogramaEstimadoInterface;

    @Autowired
    private ProjetoInterface projetoInterface;

    @PostMapping("/criar")
    public ResponseEntity<String> criarCronogramaEstimado(@RequestBody CronogramaEstimado cronogramaEstimado) {
        // Validações iniciais
        if (cronogramaEstimado == null || cronogramaEstimado.getProjeto() == null) {
            return ResponseEntity.badRequest().body("CronogramaEstimado não está associado a nenhum projeto.");
        }

        Projeto projeto = cronogramaEstimado.getProjeto();
        Long projetoId = projeto.getId();
        Optional<Projeto> projetoOptional = projetoInterface.findById(projetoId);

        if (!projetoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        Projeto projetoExistente = projetoOptional.get();

        // Calcula a quantidade de meses
        int meses = calcularQuantidadeMeses(projetoExistente.getData_inicio(), projetoExistente.getData_final());

        // Validação da quantidade de porcentagens
        if (meses != cronogramaEstimado.getPorcentagens().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("A quantidade de porcentagens não corresponde à duração do projeto.");
        }

        // Validação das porcentagens em ordem crescente e menor ou igual a 100
        List<Integer> porcentagens = cronogramaEstimado.getPorcentagens();
        for (int i = 0; i < porcentagens.size(); i++) {
            int porcentagem = porcentagens.get(i);
            if (porcentagem > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A porcentagem no mês " + (i + 1) + " é maior que 100.");
            }
            if (i > 0 && porcentagem < porcentagens.get(i - 1)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("As porcentagens devem estar em ordem crescente ou iguais ao mês anterior.");
            }
        }

        // Salvar os cronogramas
        for (int i = 0; i < meses; i++) {
            CronogramaEstimado novoCronograma = new CronogramaEstimado();
            novoCronograma.setProjeto(projetoExistente);
            novoCronograma.setMes(i + 1);

            List<Integer> porcentagemList = new ArrayList<>();
            porcentagemList.add(porcentagens.get(i));
            novoCronograma.setPorcentagens(porcentagemList);

            // Salvar o cronograma individualmente
            cronogramaEstimadoInterface.save(novoCronograma);
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
    
    @GetMapping("/{projetoId}")
    public ResponseEntity<Object> getCronogramasByProjetoId(@PathVariable Long projetoId) {
        Optional<Projeto> projetoOptional = projetoInterface.findById(projetoId);

        if (!projetoOptional.isPresent()) {
            // Se nenhum projeto for encontrado, retorne uma mensagem de erro.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum projeto encontrado com este ID"); 
        }

        Projeto projetoExistente = projetoOptional.get();

        // Busque todas as porcentagens associadas ao projeto
        List<CronogramaEstimado> cronogramasDoProjeto = cronogramaEstimadoInterface.findByProjeto(projetoExistente);
        
        // Extraia apenas as porcentagens em um novo array
        List<Integer> porcentagensDoProjeto = new ArrayList<>();
        for (CronogramaEstimado cronograma : cronogramasDoProjeto) {
            porcentagensDoProjeto.addAll(cronograma.getPorcentagens());
        }

        if (porcentagensDoProjeto.isEmpty()) {
            // Se a lista de porcentagens estiver vazia, retorne uma mensagem de erro.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma porcentagem encontrada para este projeto"); 
        }

        // Retorne a lista de porcentagens em formato JSON.
        return ResponseEntity.ok(porcentagensDoProjeto);
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
    
    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarCronogramaEstimado(@RequestBody CronogramaEstimado cronogramaAtualizado) {
        // Validações iniciais
        if (cronogramaAtualizado == null || cronogramaAtualizado.getProjeto() == null) {
            return ResponseEntity.badRequest().body("CronogramaEstimado não está associado a nenhum projeto.");
        }

        Projeto projeto = cronogramaAtualizado.getProjeto();
        Long projetoId = projeto.getId();
        Optional<Projeto> projetoOptional = projetoInterface.findById(projetoId);

        if (!projetoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        Projeto projetoExistente = projetoOptional.get();

        // Calcula a quantidade de meses
        int meses = calcularQuantidadeMeses(projetoExistente.getData_inicio(), projetoExistente.getData_final());

        // Validação da quantidade de porcentagens
        if (meses != cronogramaAtualizado.getPorcentagens().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("A quantidade de porcentagens não corresponde à duração do projeto.");
        }

        // Validação das porcentagens
        List<Integer> porcentagensAtualizadas = cronogramaAtualizado.getPorcentagens();
        for (int i = 0; i < meses; i++) {
            int porcentagem = porcentagensAtualizadas.get(i);
            if (porcentagem > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A porcentagem no mês " + (i + 1) + " é maior que 100.");
            }
            if (i > 0 && porcentagem < porcentagensAtualizadas.get(i - 1)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("As porcentagens devem estar em ordem crescente ou iguais ao mês anterior.");
            }
        }

        // Obtenha os cronogramas existentes para atualização
        List<CronogramaEstimado> cronogramasExistente = cronogramaEstimadoInterface.findByProjeto(projetoExistente);

        // Atualize os cronogramas existentes com as novas porcentagens
        for (int i = 0; i < meses; i++) {
            CronogramaEstimado cronogramaExistente = cronogramasExistente.get(i);
            cronogramaExistente.setPorcentagens(Collections.singletonList(porcentagensAtualizadas.get(i)));
            cronogramaEstimadoInterface.save(cronogramaExistente);
        }

        return ResponseEntity.ok("Cronograma atualizado com sucesso!");
    }

}