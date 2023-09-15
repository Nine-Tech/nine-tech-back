package com.example.fatec.ninetech.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

        int meses = calcularQuantidadeMeses(projetoExistente.getData_inicio(), projetoExistente.getData_final());

        // Validação da quantidade de meses
        if (meses != cronogramaEstimado.getPorcentagens().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A quantidade de porcentagens não corresponde à duração do projeto.");
        }

        // Salvar os cronogramas
        for (int i = 0; i < meses; i++) {
            CronogramaEstimado cronograma = new CronogramaEstimado();
            cronograma.setProjeto(projetoExistente);
            cronograma.setMes(i + 1); // Mês começa de 1

            List<Integer> porcentagemList = new ArrayList<>();
            porcentagemList.add(cronogramaEstimado.getPorcentagens().get(i));

            cronograma.setPorcentagens(porcentagemList);
            cronogramaEstimadoInterface.save(cronograma);
        }

        return ResponseEntity.ok("Cronograma criado com sucesso!");
    }

    private int calcularQuantidadeMeses(LocalDate dataInicio, LocalDate dataFim) {
        Period period = Period.between(dataInicio, dataFim);
        return period.getMonths() + 1;
    }
}