package com.example.fatec.ninetech.controllers;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoPostRequest;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoRequest;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.CronogramaEstimado;
import com.example.fatec.ninetech.repositories.CronogramaEstimadoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.PacotesInterface;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cronograma")
public class CronogramaEstimadoController {

    @Autowired
    private CronogramaEstimadoInterface cronogramaEstimadoInterface;

    @Autowired
    private SubpacotesInterface subpacotesInterface;

    @Autowired
    private ProjetoInterface projetoInterface;

    @Autowired
    private PacotesInterface wbeInterface;

    @PostMapping("/{id_subpacote}")
    public ResponseEntity<String> criarCronogramaEstimado(
            @PathVariable("id_subpacote") Long id_subpacote,
            @RequestBody CronogramaEstimadoRequest request) {
        try {
            if (cronogramaEstimadoInterface.existsBySubpacoteId(id_subpacote)) {
                return ResponseEntity.badRequest().body("Cronograma with the same id_subpacote already exists.");
            }


            Optional<Subpacotes> subpacote_query = this.subpacotesInterface.findById(id_subpacote);
            Optional<Projeto> projeto_query = this.projetoInterface.findById(request.getId_projeto());      

            if (subpacote_query.isPresent() && projeto_query.isPresent()) {
                Projeto projeto = projeto_query.get();
                Subpacotes subpacote = subpacote_query.get();

                CronogramaEstimado novoCronogramaEstimado  = new CronogramaEstimado(
                        request.getMes1(),
                        request.getMes2(),
                        request.getMes3(),
                        request.getMes4(),
                        request.getMes5(),
                        request.getMes6(),
                        request.getMes7(),
                        request.getMes8(),
                        request.getMes9(),
                        request.getMes10(),
                        request.getMes11(),
                        request.getMes12(),
                        projeto,
                        subpacote,
                        request.getPorcentagens()
                );

                this.cronogramaEstimadoInterface.save(novoCronogramaEstimado);
                return ResponseEntity.ok("Cronograma criado com sucesso!");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projeto ou subpacote não existente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Um erro ocorreu: " + e);
        }
    }

    
    @GetMapping("/pacote/{id_pacote}")
    public ResponseEntity<?> getCronogramaPorPagote(
            @PathVariable("id_pacote") Long id_pacote
    ) {
      try {
          List<CronogramaEstimado> cronogramaEstimado = this.cronogramaEstimadoInterface.findByProjetoId(id_pacote);

          if (cronogramaEstimado.isEmpty()) {
              return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimado);
          }

          return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimado);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e);
      }
    };
    
    @GetMapping("/{id_subpacote}")
    public ResponseEntity<?> getCronograma(
            @PathVariable("id_subpacote") Long id_subpacote
    ) {
      try {
          Optional<CronogramaEstimado> cronogramaEstimado = this.cronogramaEstimadoInterface.findBySubpacoteId(id_subpacote);

          if (cronogramaEstimado.isEmpty()) {
              return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimado);
          }

          System.out.println(cronogramaEstimado);

          return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimado);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e);
      }
    };


    @PutMapping("/{id_subpacote}")
    public ResponseEntity<?> editCronograma(
            @PathVariable("id_subpacote") Long id_subpacote,
            @Validated @RequestBody CronogramaEstimadoPostRequest request
    ) {
        try {
            if (this.cronogramaEstimadoInterface.existsByIdAndSubpacoteId(request.getId(), id_subpacote)) {
                Optional<Subpacotes> query_subpacote = this.subpacotesInterface.findById(id_subpacote);
                Optional<Projeto> query_projeto = this.projetoInterface.findById(request.getId_projeto());

                if (query_subpacote.isPresent() && query_projeto.isPresent()) {
                    Subpacotes subpacote = query_subpacote.get();
                    Projeto projeto = query_projeto.get();

                    CronogramaEstimado updateCronogramaEstimado = new CronogramaEstimado(
                            request.getId(),
                            request.getMes1(),
                            request.getMes2(),
                            request.getMes3(),
                            request.getMes4(),
                            request.getMes5(),
                            request.getMes6(),
                            request.getMes7(),
                            request.getMes8(),
                            request.getMes9(),
                            request.getMes10(),
                            request.getMes11(),
                            request.getMes12(),
                            projeto,
                            subpacote,
                            request.getPorcentagens()
                    );

                    CronogramaEstimado cronogramaEstimadoAlterado = this.cronogramaEstimadoInterface.save(updateCronogramaEstimado);

                    return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimadoAlterado);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subpacote não encontrado");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no servidor: " + e);
        }
    }

    @DeleteMapping("/{id_subpacote}")
    public ResponseEntity<?> deleteCronograma(
            @PathVariable("id_subpacote") Long id_subpacote
    ) {
        try {
            if (this.cronogramaEstimadoInterface.existsBySubpacoteId(id_subpacote)) {
                this.cronogramaEstimadoInterface.deleteBySubpacoteId(id_subpacote);
                return ResponseEntity.status(HttpStatus.OK).body("Cronograma removido");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cronograma não existente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e);
        }
    }
    
    private boolean isOrdenada(List<Integer> porcentagens) {
        for (int i = 1; i < porcentagens.size(); i++) {
            if (porcentagens.get(i) < porcentagens.get(i - 1)) {
                return false;
            }
        }
        return true;
    }
}
