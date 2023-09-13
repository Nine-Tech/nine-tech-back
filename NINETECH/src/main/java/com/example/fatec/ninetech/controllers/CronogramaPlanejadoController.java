package com.example.fatec.ninetech.controllers;

import java.time.LocalDate;
import java.time.Period;
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
public class CronogramaPlanejadoController {

    @Autowired
    private CronogramaEstimadoInterface cronogramaEstimadoInterface;

    @Autowired
    private ProjetoInterface projetoInterface;

    @PostMapping("/criar")
    public ResponseEntity<String> criarCronograma(@RequestBody CronogramaEstimado cronogramaEstimado) {
    	Long projetoId = (Long) requestBody.get("projetoId");
    	
        Optional<Projeto> projetoOptional = projetoInterface.findById(projetoId);

        if (!projetoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        Projeto projeto = projetoOptional.get();
        // Calcula a quantidade de meses com base na data de início e fim
        int meses = calcularQuantidadeMeses(cronogramaEstimado.getDataInicio(), cronogramaEstimado.getDataFim());

        if (meses == cronogramaEstimado.getPorcentagens().size()) {
            // Todas as verificações passaram, então salve os cronogramas
            for (Integer porcentagem : cronogramaEstimado.getPorcentagens()) {
                CronogramaEstimado cronograma = new CronogramaEstimado();
                cronograma.setProjeto(projeto);
                cronograma.setMes(CronogramaEstimado.getDataInicio().getMonthValue());
                cronograma.setPorcentagem(porcentagem);
                cronogramaEstimadoInterface.save(cronograma);
            }
            return ResponseEntity.ok("Cronograma criado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A quantidade de porcentagens não corresponde à duração do projeto.");
        }
    }

    // Método para calcular a quantidade de meses entre duas datas
    private int calcularQuantidadeMeses(LocalDate dataInicio, LocalDate dataFim) {
        Period period = Period.between(dataInicio, dataFim);
        return period.getMonths() + 1;
    }
}
