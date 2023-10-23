package com.example.fatec.ninetech.controllers;

import java.util.List;
import java.util.Optional;

import com.example.fatec.ninetech.helpers.HomemHoraPostRequest;
import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.models.Tarefas;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import com.example.fatec.ninetech.repositories.TarefasInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.repositories.ProjetoInterface;

@RestController
@RequestMapping("/projeto")
public class ProjetoController {

    @Autowired
    private ProjetoInterface projetoInterface;

    @Autowired
    private PacotesInterface pacotesInterface;

    @Autowired
    private SubpacotesInterface subpacotesInterface;

    @Autowired
    private TarefasInterface tarefasInterface;

    @PostMapping
    public ResponseEntity<Projeto> criarProjeto(@RequestBody Projeto projeto) {
        try {
            Projeto novoProjeto = projetoInterface.save(projeto);
            return new ResponseEntity<>(novoProjeto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Projeto>> listarProjetos() {
        List<Projeto> projetos = projetoInterface.findAll();
        return new ResponseEntity<>(projetos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> lerProjeto(@PathVariable Long id) {
        try {
            return projetoInterface.findById(id)
                .map(projeto -> new ResponseEntity<>(projeto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizarProjeto(@PathVariable Long id, @RequestBody Projeto projetoAtualizado) {
        try {
            return projetoInterface.findById(id)
                .map(projeto -> {
                    projeto.setNome(projetoAtualizado.getNome());
                    projeto.setData_inicio(projetoAtualizado.getData_inicio());
                    projeto.setData_final(projetoAtualizado.getData_final());
                    // Atualize outros campos, se necessário
                    Projeto projetoAtualizadoSalvo = projetoInterface.save(projeto);
                    return new ResponseEntity<>(projetoAtualizadoSalvo, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        try {
            projetoInterface.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CRUD Homem Hora - FEAT 74
    @GetMapping("/homem_hora/{id}")
    public ResponseEntity<?> getValorHomemHora(@PathVariable Long id) {
        try {
            Optional<Projeto> projeto = this.projetoInterface.findById(id);
            if (projeto.isPresent()) {
                // Se existe um projeto como ID passado na variável de caminho
                return ResponseEntity.status(HttpStatus.FOUND).body(projeto.get().getValor_homem_hora());
            }

            // Senão não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado");
        } catch (Exception e) {
            // Caso algum erro ocorra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Um erro ocorreu: " + e);
        }
    }

    @PutMapping("/homem_hora/{id}")
    public ResponseEntity<?> putValorHomemHora(
            @PathVariable Long id,
            @RequestBody HomemHoraPostRequest objeto_homem_hora
    ) {
        try {
            Double valor_homem_hora = objeto_homem_hora.getValor_homem_hora();

            Optional<Projeto> query_projeto = this.projetoInterface.findById(id);
            if (query_projeto.isPresent()) {
                Projeto projeto = query_projeto.get();
                projeto.setValor_homem_hora(valor_homem_hora);

                Double valor_total_projeto = 0.0;
                this.projetoInterface.save(projeto);

                // Recupere todos os pacotes com o projeto ID informado
                Optional<List<Pacotes>> query_pacotes = this.pacotesInterface.findAllByProjetoId(id);
                if (query_pacotes.isPresent()) {
                    List<Pacotes> pacotes = query_pacotes.get();

                    for (Pacotes pacote : pacotes) {
                        Double valor_total_pacote = 0.0; // Variável acumuladora dos valores de subpacote
                        Long id_pacote = pacote.getId();

                        Optional<List<Subpacotes>> query_subpacotes = this.subpacotesInterface.findAllByPacotesId(id_pacote);
                        if (query_subpacotes.isPresent()) {
                            List<Subpacotes> subpacotes = query_subpacotes.get();

                            for (Subpacotes subpacote : subpacotes) {
                                Double valor_total_subpacote = 0.0; // Variável acumuladora dos valores de tarefas
                                Long id_subpacote = subpacote.getId();

                                Optional<List<Tarefas>> query_tarefas = this.tarefasInterface.findAllBySubpacotesId(id_subpacote);
                                if (query_tarefas.isPresent()) {
                                    List<Tarefas> tarefas = query_tarefas.get();

                                    for (Tarefas tarefa : tarefas) {
                                        // Calculando o valor da tarefa com o novo
                                        // valor_hora_homem
                                        Double hh = tarefa.getHh();
                                        Double material = tarefa.getMaterial();
                                        Double total = (hh * valor_homem_hora) + material;
                                        tarefa.setValor(total);

                                        valor_total_subpacote += total;
                                        // Persistindo mudanças na tarefa
                                        this.tarefasInterface.save(tarefa);
                                    }
                                }
                                // Update no valor em subpacotes
                                subpacote.setValor_total(valor_total_subpacote);
                                valor_total_pacote += valor_total_subpacote;
                                this.subpacotesInterface.save(subpacote);
                            }
                        }
                        // Update no valor em pacotes
                        pacote.setValor_total(valor_total_pacote);
                        valor_total_projeto += valor_total_pacote;
                        this.pacotesInterface.save(pacote);
                    }
                }
                projeto.setValor_total(valor_total_projeto);
                this.projetoInterface.save(projeto); // Update do valor total do projeto
                return ResponseEntity.status(HttpStatus.OK).body("Homem hora e valores atualizados");
            }
            // Se não for encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado");
        } catch (Exception e) {
            // Caso ocorra um erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Um erro aconteceu: " + e);
        }
    }

}