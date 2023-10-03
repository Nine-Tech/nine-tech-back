package com.example.fatec.ninetech.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.CronogramaEstimadoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.WBSInterface;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cronograma")
public class CronogramaEstimadoController {

    @Autowired
    private CronogramaEstimadoInterface cronogramaEstimadoInterface;

    @Autowired
    private ProjetoInterface projetoInterface;

    @Autowired
    private WBSInterface wbeInterface;

    @PostMapping("/{projeto_id}")
    public ResponseEntity<String> criarCronogramaEstimado(
            @PathVariable("projeto_id") Long projeto_id,
            @Validated @RequestBody CronogramaEstimado request) {
        // Validações iniciais
        if (projeto_id == null) {
            return ResponseEntity.badRequest().body("O ID do projeto não foi fornecido no URL.");
        }

        Optional<Projeto> projetoOptional = projetoInterface.findById(projeto_id);

        Projeto projetoExistente = projetoOptional.orElse(null);

        if (projetoExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        // Verifique se o projeto já possui cronogramas associados
        List<CronogramaEstimado> cronogramasExistente = cronogramaEstimadoInterface.findByProjetoId(projeto_id);

        if (!cronogramasExistente.isEmpty()) {
            return ResponseEntity.badRequest().body("O projeto já possui cronogramas associados.");
        }

        List<List<Integer>> porcentagens = request.getPorcentagens();

        // Validação: Verificar se a quantidade de listas corresponde à quantidade de WBEs
        List<WBE> wbesDoProjeto = wbeInterface.findByProjetoId(projeto_id);

        if (wbesDoProjeto.isEmpty() || wbesDoProjeto.size() != porcentagens.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A quantidade de listas de porcentagens não corresponde à quantidade de WBEs encontrados.");
        }

        // Verifique se todas as porcentagens estão corretas
        for (List<Integer> porcentagensDoWBE : porcentagens) {
            if (porcentagensDoWBE.size() != 12) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("A quantidade de porcentagens para um dos WBEs não corresponde a 12 meses.");
            }
        }

        // Agora, podemos salvar as porcentagens, pois todas estão corretas
        for (WBE wbe : wbesDoProjeto) {
            int wbeIndex = wbesDoProjeto.indexOf(wbe);

            List<Integer> porcentagensDoWBE = porcentagens.get(wbeIndex);
            Long wbeId = wbe.getId();

            // Salve as porcentagens no banco de dados para este WBE
            CronogramaEstimado novoCronograma = new CronogramaEstimado();
            novoCronograma.setProjeto(projetoExistente);
            novoCronograma.setWbeId(wbeId);
            novoCronograma.setMes1(porcentagensDoWBE.get(0)); // Mês 1
            novoCronograma.setMes2(porcentagensDoWBE.get(1)); // Mês 2
            novoCronograma.setMes3(porcentagensDoWBE.get(2)); // Mês 3
            novoCronograma.setMes4(porcentagensDoWBE.get(3)); // Mês 4
            novoCronograma.setMes5(porcentagensDoWBE.get(4)); // Mês 5
            novoCronograma.setMes6(porcentagensDoWBE.get(5)); // Mês 6
            novoCronograma.setMes7(porcentagensDoWBE.get(6)); // Mês 7
            novoCronograma.setMes8(porcentagensDoWBE.get(7)); // Mês 8
            novoCronograma.setMes9(porcentagensDoWBE.get(8)); // Mês 9
            novoCronograma.setMes10(porcentagensDoWBE.get(9)); // Mês 10
            novoCronograma.setMes11(porcentagensDoWBE.get(10)); // Mês 11
            novoCronograma.setMes12(porcentagensDoWBE.get(11)); // Mês 12
            cronogramaEstimadoInterface.save(novoCronograma);
        }

        return ResponseEntity.ok("Cronograma criado com sucesso!");
    }
    
    @Transactional
    @DeleteMapping("/{projeto_id}")
    public ResponseEntity<String> deletarCronogramaPorProjeto(@PathVariable Long projeto_id) {
        Projeto projeto = projetoInterface.findById(projeto_id).orElse(null);

        // Verifique se o projeto existe
        if (projeto == null) {
            return ResponseEntity.notFound().build();
        }

        // Use o método personalizado para excluir cronogramas por projeto ID
        cronogramaEstimadoInterface.deleteByProjeto(projeto);

        return ResponseEntity.ok("Cronograma excluído com sucesso para o projeto ID: " + projeto_id);
    }
    
    @GetMapping("/{projeto_id}")
    public ResponseEntity<Object> getCronogramaEstimado(@PathVariable Long projeto_id) {
        Projeto projeto = projetoInterface.findById(projeto_id).orElse(null);

        if (projeto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        List<WBE> wbesDoProjeto = wbeInterface.findByProjetoId(projeto_id);

        if (wbesDoProjeto.isEmpty()) {
            return ResponseEntity.badRequest().body("O projeto não possui WBEs associados.");
        }

        Map<String, Object> wbesJson = new HashMap<>();

        for (WBE wbe : wbesDoProjeto) {
            Long wbeId = wbe.getId();

            Map<String, Object> wbeData = new HashMap<>();
            wbeData.put("nome", wbe.getWbe()); // Adicione o nome do WBE

            List<Integer> porcentagens = new ArrayList<>();

            // Verifique se o WBE já possui um cronograma associado
            List<CronogramaEstimado> cronogramasDoWBE = cronogramaEstimadoInterface.findByProjetoAndWbeId(projeto, wbeId);

            if (!cronogramasDoWBE.isEmpty()) {
                // Se o WBE possui cronograma(s), pegue o último cronograma (pode ajustar a lógica conforme necessário)
                CronogramaEstimado ultimoCronograma = cronogramasDoWBE.get(cronogramasDoWBE.size() - 1);
                
                porcentagens.add(ultimoCronograma.getMes1());
                porcentagens.add(ultimoCronograma.getMes2());
                porcentagens.add(ultimoCronograma.getMes3());
                porcentagens.add(ultimoCronograma.getMes4());
                porcentagens.add(ultimoCronograma.getMes5());
                porcentagens.add(ultimoCronograma.getMes6());
                porcentagens.add(ultimoCronograma.getMes7());
                porcentagens.add(ultimoCronograma.getMes8());
                porcentagens.add(ultimoCronograma.getMes9());
                porcentagens.add(ultimoCronograma.getMes10());
                porcentagens.add(ultimoCronograma.getMes11());
                porcentagens.add(ultimoCronograma.getMes12());
            } else {
                // Se o WBE não possui cronograma, crie um array com 12 porcentagens zeradas
                for (int mes = 1; mes <= 12; mes++) {
                    porcentagens.add(0);
                }
            }

            wbeData.put("porcentagens", porcentagens);
            wbesJson.put(wbeId.toString(), wbeData);
        }

        return ResponseEntity.ok(wbesJson);
    }
    
    @Transactional
    @PutMapping("/{projeto_id}")
    public ResponseEntity<String> atualizarCronogramaEstimado(
            @PathVariable("projeto_id") Long projeto_id,
            @Validated @RequestBody CronogramaEstimado request) {
        // Validações iniciais
        if (projeto_id == null) {
            return ResponseEntity.badRequest().body("O ID do projeto não foi fornecido no URL.");
        }

        Optional<Projeto> projetoOptional = projetoInterface.findById(projeto_id);

        Projeto projetoExistente = projetoOptional.orElse(null);

        if (projetoExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        // Antes de atualizar, exclua os cronogramas existentes para o projeto
        cronogramaEstimadoInterface.deleteByProjeto(projetoExistente);

        List<List<Integer>> porcentagens = request.getPorcentagens();

        // Validação: Verificar se a quantidade de listas corresponde à quantidade de WBEs
        List<WBE> wbesDoProjeto = wbeInterface.findByProjetoId(projeto_id);

        if (wbesDoProjeto.isEmpty() || wbesDoProjeto.size() != porcentagens.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A quantidade de listas de porcentagens não corresponde à quantidade de WBEs encontrados.");
        }

        // Verifique se todas as porcentagens estão corretas
        for (List<Integer> porcentagensDoWBE : porcentagens) {
            if (porcentagensDoWBE.size() != 12) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("A quantidade de porcentagens para um dos WBEs não corresponde a 12 meses.");
            }
        }

        // Agora, podemos salvar as porcentagens atualizadas, pois todas estão corretas
        for (WBE wbe : wbesDoProjeto) {
            int wbeIndex = wbesDoProjeto.indexOf(wbe);

            List<Integer> porcentagensDoWBE = porcentagens.get(wbeIndex);
            Long wbeId = wbe.getId();

            // Salve as porcentagens no banco de dados para este WBE
            CronogramaEstimado novoCronograma = new CronogramaEstimado();
            novoCronograma.setProjeto(projetoExistente);
            novoCronograma.setWbeId(wbeId);
            novoCronograma.setMes1(porcentagensDoWBE.get(0)); // Mês 1
            novoCronograma.setMes2(porcentagensDoWBE.get(1)); // Mês 2
            novoCronograma.setMes3(porcentagensDoWBE.get(2)); // Mês 3
            novoCronograma.setMes4(porcentagensDoWBE.get(3)); // Mês 4
            novoCronograma.setMes5(porcentagensDoWBE.get(4)); // Mês 5
            novoCronograma.setMes6(porcentagensDoWBE.get(5)); // Mês 6
            novoCronograma.setMes7(porcentagensDoWBE.get(6)); // Mês 7
            novoCronograma.setMes8(porcentagensDoWBE.get(7)); // Mês 8
            novoCronograma.setMes9(porcentagensDoWBE.get(8)); // Mês 9
            novoCronograma.setMes10(porcentagensDoWBE.get(9)); // Mês 10
            novoCronograma.setMes11(porcentagensDoWBE.get(10)); // Mês 11
            novoCronograma.setMes12(porcentagensDoWBE.get(11)); // Mês 12
            cronogramaEstimadoInterface.save(novoCronograma);
        }

        return ResponseEntity.ok("Cronograma atualizado com sucesso!");
    }
}
