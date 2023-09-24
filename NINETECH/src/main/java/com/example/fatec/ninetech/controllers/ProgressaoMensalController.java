package com.example.fatec.ninetech.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.ProgressaoMensal;
import com.example.fatec.ninetech.repositories.ProgressaoMensalRepositorio;

@RestController
@RequestMapping("/progressaomensal")
public class ProgressaoMensalController {
	
	@Autowired
	private ProgressaoMensalRepositorio repoProgressaoMensal;
	
	@GetMapping
	public List<ProgressaoMensal> listar(){
		return repoProgressaoMensal.findAll();
	}
	
	@GetMapping("/{id}")
	public ProgressaoMensal buscar(@PathVariable Long id) {
		return repoProgressaoMensal.findById(id).get();	
	}
	
	@PostMapping("/cadastrar")
	public ResponseEntity<ProgressaoMensal> cadastrar(@RequestBody ProgressaoMensal progressaomensal) {
		ProgressaoMensal salvarProgressaoMensal = repoProgressaoMensal.save(progressaomensal);
        return ResponseEntity.ok(salvarProgressaoMensal);
	}
	
	@DeleteMapping("/{id}")
	// Verificar pelo id e excluir 
	public void deletar(@PathVariable Long id) {
		repoProgressaoMensal.deleteById(id);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ProgressaoMensal> alterar(@PathVariable Long id, @RequestBody ProgressaoMensal progressaomensal) {
	    // Verificar se o registro com o ID fornecido existe no banco de dados
	    if (repoProgressaoMensal.existsById(id)) {
	        ProgressaoMensal progressao = repoProgressaoMensal.findById(id).get();
	        
	        // Verificar se o campo "peso" foi fornecido no corpo da requisição
	        if (progressaomensal.getPeso() != null) {
	            progressao.setPeso(progressaomensal.getPeso());
	        }
	        
	        // Verificar se o campo "execucao" foi fornecido no corpo da requisição
	        if (progressaomensal.getExecucao() != progressao.getExecucao()) {
	            progressao.setExecucao(progressaomensal.getExecucao());
	        }
	        
	        // Salvar as alterações no registro
	        ProgressaoMensal updatedProgressao = repoProgressaoMensal.save(progressao);
	        return ResponseEntity.ok(updatedProgressao);
	    } else {
	        return null;
	    }
	}
	
	@GetMapping("/calculo")
	public Double CalculoProgressaoMensal() {
	    // Buscar todas as ProgressaoMensal com execucao igual a 1
	    boolean execucao = true;
	    List<ProgressaoMensal> progressoes = repoProgressaoMensal.findByExecucao(execucao);
	    
	    // Inicializar a soma dos pesos com execucao igual a 1
	    double somaPesosExecucao1 = 0.0;
	    
	    // Inicializar a soma de todos os valores de execucao
	    double somaExecucaoTotal = 0.0;
	    
	    // Calcular a soma dos pesos com execucao igual a 1
	    for (ProgressaoMensal progressao : progressoes) {
	        double peso = Double.parseDouble(progressao.getPeso());
	        somaPesosExecucao1 += peso;
	    }
	    
	    // Buscar todas as ProgressaoMensal
	    List<ProgressaoMensal> todasProgressoes = repoProgressaoMensal.findAll();
	    
	    // Calcular a soma de todos os valores de execucao
	    for (ProgressaoMensal progressao : todasProgressoes) {
	        double peso = Double.parseDouble(progressao.getPeso());
	        somaExecucaoTotal += peso;
	    }
	    
	    // Calcular o resultado
	    double resultado = (somaPesosExecucao1 / somaExecucaoTotal) * 100;
	    
	    return resultado;
	}
}
