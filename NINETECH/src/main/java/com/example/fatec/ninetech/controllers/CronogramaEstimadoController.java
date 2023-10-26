package com.example.fatec.ninetech.controllers;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoPostRequest;
import com.example.fatec.ninetech.helpers.CronogramaEstimadoRequest;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
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
import com.example.fatec.ninetech.repositories.CronogramaEstimadoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.repositories.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



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
    
    @Autowired
    private CronogramaProjetoEstimadoInterface CronogramaProjetoEstimadoInterface;
    

    @PostMapping("/{id_subpacote}")
    public ResponseEntity<String> criarCronogramaEstimado(
        @PathVariable("id_subpacote") Long id_subpacote,
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
                    CronogramaEstimado novoCronogramaEstimado = new CronogramaEstimado(
                        mes,
                        porcentagem,
                        projeto,
                        subpacote
                    );

                    this.cronogramaEstimadoInterface.save(novoCronogramaEstimado);

                    // Atualize a variável mesMaximoProjeto se o mês atual for maior do que o valor anterior
                    mesMaximoProjeto = calcularMesMaximoProjeto(projeto.getId());

                    mes++; // Incrementa o mês automaticamente
                }

                // Atualize a variável MesMaximoProjeto no projeto
                projeto.setMesMaximoProjeto(mesMaximoProjeto);

                // Atualize o número total de meses no projeto com base no maior número de meses dos subpacotes
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
    // calculo o numero de meses maximo do projeto com base no cronograma do subpacote
    private int calcularMesMaximoProjeto(Long idProjeto) {
        // Consulte os subpacotes do projeto que correspondem ao projeto_id
        List<CronogramaEstimado> subpacotesDoProjeto = this.cronogramaEstimadoInterface.findByProjetoId(idProjeto);

        int mesMaximoProjeto = 0;

        // Encontre o maior valor de mes entre os subpacotes do projeto
        for (CronogramaEstimado cronogramaEstimado : subpacotesDoProjeto) {
            mesMaximoProjeto = Math.max(mesMaximoProjeto, cronogramaEstimado.getMes());
        }
        System.out.println(mesMaximoProjeto);
        return mesMaximoProjeto;
    }
    
 // Após criar os objetos CronogramaEstimado, você pode calcular a média das porcentagens para cada mês:
    private void calcularPorcentagemMediaPorMes(Long idProjeto) {
        // Consulte os subpacotes do projeto que correspondem ao projeto_id
        List<CronogramaEstimado> subpacotesDoProjeto = this.cronogramaEstimadoInterface.findByProjetoId(idProjeto);
        System.out.println(subpacotesDoProjeto);

        // Use o Java Stream API para agrupar os registros por mês
        Map<Integer, List<CronogramaEstimado>> porMes = subpacotesDoProjeto.stream()
                .collect(Collectors.groupingBy(CronogramaEstimado::getMes));

     // Calcula a média da porcentagem para cada mês
        for (Map.Entry<Integer, List<CronogramaEstimado>> entry : porMes.entrySet()) {
            int mes = entry.getKey();
            List<CronogramaEstimado> cronogramasDoMes = entry.getValue();

            // Calcula a média da porcentagem para o mês atual
            double mediaPorcentagem = cronogramasDoMes.stream()
                    .collect(Collectors.averagingInt(CronogramaEstimado::getPorcentagem));

            // Crie um novo objeto CronogramaProjetoEstimado com os valores apropriados
            CronogramaProjetoEstimado cronogramaProjetoMedia = new CronogramaProjetoEstimado();
            cronogramaProjetoMedia.setMes(mes);
            cronogramaProjetoMedia.setPorcentagem((double) mediaPorcentagem);

            // Obtenha o objeto projeto a partir do ID do projeto
            Projeto projeto = this.projetoInterface.findById(idProjeto).orElse(null);
            if (projeto != null) {
                cronogramaProjetoMedia.setProjeto(projeto);

                this.CronogramaProjetoEstimadoInterface.save(cronogramaProjetoMedia);
                System.out.println(cronogramaProjetoMedia);
            } else {
                System.out.println("Projeto não encontrado para o ID: " + idProjeto);
            }
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


//    @PutMapping("/{id_subpacote}")
//    public ResponseEntity<?> editCronograma(
//            @PathVariable("id_subpacote") Long id_subpacote,
//            @Validated @RequestBody CronogramaEstimadoPostRequest request
//    ) {
//        try {
//            if (this.cronogramaEstimadoInterface.existsByIdAndSubpacoteId(request.getId(), id_subpacote)) {
//                Optional<Subpacotes> query_subpacote = this.subpacotesInterface.findById(id_subpacote);
//                Optional<Projeto> query_projeto = this.projetoInterface.findById(request.getId_projeto());
//
//                if (query_subpacote.isPresent() && query_projeto.isPresent()) {
//                    Subpacotes subpacote = query_subpacote.get();
//                    Projeto projeto = query_projeto.get();
//
//                    CronogramaEstimado updateCronogramaEstimado = new CronogramaEstimado(
//                            request.getId(),
//                            request.getMes1(),
//                            projeto,
//                            subpacote,
//                            request.getPorcentagens()
//                    );
//
//                    CronogramaEstimado cronogramaEstimadoAlterado = this.cronogramaEstimadoInterface.save(updateCronogramaEstimado);
//
//                    return ResponseEntity.status(HttpStatus.OK).body(cronogramaEstimadoAlterado);
//                }
//            }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subpacote não encontrado");
//        } catch(Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no servidor: " + e);
//        }
//    }

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
