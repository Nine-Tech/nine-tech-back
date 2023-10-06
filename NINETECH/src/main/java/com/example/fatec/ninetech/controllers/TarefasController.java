package com.example.fatec.ninetech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fatec.ninetech.models.Tarefas;
import com.example.fatec.ninetech.repositories.TarefasInterface;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tarefas")
public class TarefasController {

    @Autowired
    private TarefasInterface interfaceTarefas;

	@PostMapping
	public ResponseEntity<Object> cadastrar(@RequestBody Tarefas tarefas) {
        try {
            Tarefas novaTarefa = interfaceTarefas.save(tarefas);
            return new ResponseEntity<>(novaTarefa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefas tarefaAtualizada) {
	    try {
	        Optional<Tarefas> tarefaExistente = interfaceTarefas.findById(id);

	        if (tarefaExistente.isPresent()) {
	            Tarefas tarefa = tarefaExistente.get();
	            
	            tarefa.setDescricao(tarefaAtualizada.getDescricao());
	            tarefa.setData(tarefaAtualizada.getData());
	            tarefa.setDescricao(tarefaAtualizada.getDescricao());
	            tarefa.setExecucao(tarefaAtualizada.getExecucao());
	            tarefa.setHh(tarefaAtualizada.getHh());
	            tarefa.setMaterial(tarefaAtualizada.getMaterial());
	            tarefa.setNome(tarefaAtualizada.getNome());
	            tarefa.setPeso(tarefaAtualizada.getPeso());
	            tarefa.setPorcentagem(tarefaAtualizada.getPorcentagem());
	            tarefa.setValor(tarefaAtualizada.getValor());

	            Tarefas tarefaAtualizadaNoBanco = interfaceTarefas.save(tarefa);
	            
	            return new ResponseEntity<>(tarefaAtualizadaNoBanco, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

    // Método para excluir uma tarefa pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluirTarefa(@PathVariable Long id) {
        try {
            Optional<Tarefas> tarefa = interfaceTarefas.findById(id);

            if (tarefa.isPresent()) {
                interfaceTarefas.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para obter todas as tarefas
    @GetMapping
    public ResponseEntity<List<Tarefas>> listarTodasTarefas() {
        try {
            List<Tarefas> tarefas = interfaceTarefas.findAll();
            return new ResponseEntity<>(tarefas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para obter uma tarefa pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obterTarefaPorId(@PathVariable Long id) {
        try {
            Optional<Tarefas> tarefa = interfaceTarefas.findById(id);

            if (tarefa.isPresent()) {
                return new ResponseEntity<>(tarefa.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
