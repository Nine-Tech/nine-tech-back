package com.example.fatec.ninetech.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.ProgressaoMensal;
import com.example.fatec.ninetech.repositories.ProgressaoMensalInterface;

@RestController
@RequestMapping("/progressaomensal")
public class ProgressaoMensalController {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void executarComandoSQL(String comandoSQL) {
        jdbcTemplate.execute(comandoSQL);
    }

	@Autowired
	private ProgressaoMensalInterface repoProgressaoMensal;
	
	@GetMapping
	public ResponseEntity<List<ProgressaoMensal>> listar() {
	    List<ProgressaoMensal> progressoes = repoProgressaoMensal.findAll();

	    if (progressoes.isEmpty()) {
	        // Se a lista estiver vazia, retorne um ResponseEntity com status HTTP 204 (No Content)
	        return ResponseEntity.noContent().build();
	    } else {
	        // Se a lista contiver dados, retorne os dados com status HTTP 200 (OK)
	        return ResponseEntity.ok(progressoes);
	    }
	}
	
	@GetMapping("/{liderId}/{projetoId}")
	public ResponseEntity<List<Object[]>> buscarPorLiderEProjeto(@PathVariable Long liderId,@PathVariable Long projetoId) {

	    List<Object[]> progressoes = repoProgressaoMensal.buscarPorLiderEProjeto(liderId, projetoId);

	    if (!progressoes.isEmpty()) {
	        return ResponseEntity.ok(progressoes);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProgressaoMensal> buscar(@PathVariable Long id) {
	    ProgressaoMensal progressao = repoProgressaoMensal.findById(id).orElse(null);

	    if (progressao != null) {
	        // Se o registro for encontrado, retorne o registro com status HTTP 200 (OK)
	        return ResponseEntity.ok(progressao);
	    } else {
	        // Se o registro não for encontrado, retorne um ResponseEntity com status HTTP 404 (Not Found)
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<Object> cadastrar(@PathVariable Long id, @RequestBody ProgressaoMensal progressaomensal) {
	    // Verifica se o campo 'peso' foi fornecido na requisição
	    if (progressaomensal.getPeso() == null) {
	        return ResponseEntity.badRequest().body("Campo 'peso' é obrigatório.");
	    }
	    
	    String[] fibonacciValidador = {"0","1","2","3","5","8","13","20","40","100"};
	    
	    String peso = progressaomensal.getPeso();
	    boolean pesoValido = false;
	    
	    for (String pes: fibonacciValidador) {
	    	if(peso.equals(pes)) {
	    		pesoValido = true;
	    		break;
	    	}
	    }
	    
	    
	    
	    if (!pesoValido) {
	        return ResponseEntity.badRequest().body("Valor fornecido não é válido.");
	    }
	    
	    String data = progressaomensal.getData().toString();
	    // Se todos os campos necessários foram fornecidos, salva a progressão mensal
	    // INSERT INTO progressao_mensal (data, execucao, id_wbe, peso) VALUES (:data, :execucao, :id_wbe, :peso)
	    String comandoSQL = "INSERT INTO progressao_mensal (data, execucao, id_wbe, peso) " +
                "VALUES ('" + data + "', " + progressaomensal.getExecucao() + ", '" + id + "', '" + progressaomensal.getPeso() + "')";
	    this.executarComandoSQL(comandoSQL);
	    return ResponseEntity.ok(progressaomensal);
	}
	
	@DeleteMapping("/{id}")
	// Verificar pelo id e excluir 
	public ResponseEntity<ProgressaoMensal> deletar(@PathVariable Long id) {
		ProgressaoMensal progressao = repoProgressaoMensal.findById(id).orElse(null);
		if (progressao != null) {
			repoProgressaoMensal.deleteById(id);
			return ResponseEntity.ok(progressao);
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	
	
	
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody ProgressaoMensal progressaomensal) {
	    // Verificar se o registro com o ID fornecido existe no banco de dados
	    if (repoProgressaoMensal.existsById(id)) {
	        ProgressaoMensal progressao = repoProgressaoMensal.findById(id).get();
	        
	        // Verificar se o campo "peso" foi fornecido no corpo da requisição
	        if (progressaomensal.getPeso() != null) {
	            progressao.setPeso(progressaomensal.getPeso());
		        String[] fibonacciValidador = {"0","1","2","3","5","8","13","20","40","100"};
		        
			    String peso = progressaomensal.getPeso();
			    boolean pesoValido = false;
			    
			    for (String pes: fibonacciValidador) {
			    	if(peso.equals(pes)) {
			    		pesoValido = true;
			    		break;
			    	}
			    }
			    
			    if (!pesoValido) {
			        return ResponseEntity.badRequest().body("Valor fornecido não é válido.");
			    }
	        }
	        
	        // Verificar se o campo "execucao" foi fornecido no corpo da requisição
	        if (progressaomensal.getExecucao() != progressao.getExecucao()) {
	            progressao.setExecucao(progressaomensal.getExecucao());
	        }
	        String data = progressaomensal.getData().toString();
	        String comandoSQL = "UPDATE progressao_mensal SET data = '" + data + "', execucao =  " + progressaomensal.getExecucao() + ", peso = " + progressaomensal.getPeso() + " WHERE id = " + id ;
	        System.out.println(comandoSQL);
		    this.executarComandoSQL(comandoSQL);
	        // Salvar as alterações no registro
	        //ProgressaoMensal updatedProgressao = repoProgressaoMensal.save(progressao);
	        return ResponseEntity.ok(progressao);
	    } else {
	        return null;
	    }
	}
	
	@GetMapping("/calculo/{id}")
	public Double CalculoProgressaoMensal(@PathVariable Long id) {
	    // Buscar todas as ProgressaoMensal com execucao igual a 1
	    boolean execucao = true;
	    List<ProgressaoMensal> progressoes = repoProgressaoMensal.buscarPorIdExecucao(id,execucao);
	    
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