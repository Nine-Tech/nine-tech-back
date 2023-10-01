package com.example.fatec.ninetech.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.models.LiderDeProjeto;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.WBSInterface;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/upload")
public class ExcelUploadController {

	@Autowired
	private WBSInterface interfaceWBS;
	
	@Autowired
	private EngenheiroChefeInterface interfaceEngenheiroChefe;
	
	@Autowired
	private ProjetoInterface interfaceProjeto;
	
	@Autowired
	private LiderDeProjetoInterface interfaceLiderDeProjeto;
    
    @PostMapping
    public ResponseEntity<List<WBE>> processarExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(1);

            Iterator<Row> rowIterator = sheet.iterator();
            
            Row linhaDoCabecalho = rowIterator.next();
            if (!validadorDeCabecalho(linhaDoCabecalho)) {
                return ResponseEntity.badRequest().build();
            }

            List<WBE> wbes = new ArrayList<>();
            Projeto projetoRecemCriado = null;
            Long idPai = null;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell colunaDoWBS = row.getCell(1);
                Cell colunaDoValor = row.getCell(4);
                Cell colunaDoHH = row.getCell(6);
                Cell colunaDoMaterial = row.getCell(7);
                
                if (colunaDoWBS != null && colunaDoValor != null && colunaDoHH != null && colunaDoMaterial != null) {
                    String wbe = colunaDoWBS.getStringCellValue();
                    double valor = colunaDoValor.getNumericCellValue();
                    double hh = colunaDoHH.getNumericCellValue();
                    double material = colunaDoMaterial.getNumericCellValue();

                    int espacosIniciais = 0;

                    // Contar os espaços no início da string
                    while (espacosIniciais < wbe.length() && wbe.charAt(espacosIniciais) == ' ') {
                        espacosIniciais++;
                    }

                    WBE dadosWBE = new WBE();
                    dadosWBE.setHh(hh);
                    dadosWBE.setValor(valor);
                    dadosWBE.setWbe(wbe);
                    dadosWBE.setProjeto(projetoRecemCriado);
                    dadosWBE.setMaterial(material);

                    if (espacosIniciais == 0) {
                        // Se for 0, salvar no projetoRecemCriado
                        if (projetoRecemCriado == null) {
                            EngenheiroChefe idEngenheiroChefe = interfaceEngenheiroChefe.findById(1L).orElse(null);
                            Projeto dadosProjeto = new Projeto();
                            dadosProjeto.setNome(wbe);
                            dadosProjeto.setEngenheiroChefe(idEngenheiroChefe);

                            LocalDate dataInicioAgora = LocalDate.now();
                            dadosProjeto.setData_inicio(dataInicioAgora);

                            LocalDate dataFinalSomado11Meses = dataInicioAgora.plusMonths(12);
                            dadosProjeto.setData_final(dataFinalSomado11Meses);

                            projetoRecemCriado = interfaceProjeto.save(dadosProjeto);
                        }
                    } else if (espacosIniciais == 1) {
                        // Se for 1, salvar normalmente
                    	dadosWBE.setFilho(false);
                        WBE wbeSalvo = interfaceWBS.save(dadosWBE);
                        wbes.add(wbeSalvo);
                        idPai = wbeSalvo.getId();
                    } else if (espacosIniciais == 4) {
                        // Se for 4, adicionar filho = true e salvar
                        dadosWBE.setFilho(true);
                        Optional<WBE> encontrarWBSPai = interfaceWBS.findById(idPai);
                        dadosWBE.setWbePai(encontrarWBSPai.get());
                        WBE wbeSalvo = interfaceWBS.save(dadosWBE);
                        wbes.add(wbeSalvo);
                    }

                } else {
                    break;
                }
            }

            return ResponseEntity.ok(wbes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

	@GetMapping("/{id}")
	@JsonIgnoreProperties({"wbes"})
	public ResponseEntity<List<WBE>> listarWBEsPorProjetoId(@PathVariable Long id) {
	    try {
	        List<WBE> wbes = interfaceWBS.findByProjetoId(id);
	        if (!wbes.isEmpty()) {
	            return new ResponseEntity<>(wbes, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("/lideres/{idLider}")
	@JsonIgnoreProperties({"wbes"})
	public ResponseEntity<List<WBE>> listarWBEsPorLiderId(@PathVariable Long idLider) {
	    try {
	        List<WBE> wbes = interfaceWBS.findByLiderDeProjetoId(idLider);
	        if (!wbes.isEmpty()) {
	            return new ResponseEntity<>(wbes, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	// Isolar as variáveis e salvar apenas as que mudaram, se não ele seta para nulo
	@PutMapping("/{id}")
	public ResponseEntity<WBE> atualizarWBS(@PathVariable Long id, @RequestBody WBE atualizadoWBS) {
		//Verificando se WBE, Projeto e LiderDeProjeto existem
	    Optional<WBE> encontrarPorIdWBS = interfaceWBS.findById(id);
	    Optional<Projeto> projetoOptional = interfaceProjeto.findById(atualizadoWBS.getProjeto().getId());
	    Optional<LiderDeProjeto> liderDeProjetoOptional = interfaceLiderDeProjeto.findById(atualizadoWBS.getLiderDeProjeto().getId());

	    if (encontrarPorIdWBS.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    WBE atualizandoWBS = encontrarPorIdWBS.get();

	    if (atualizadoWBS.getWbe() != null) {
	        atualizandoWBS.setWbe(atualizadoWBS.getWbe());
	    }
	    if (atualizadoWBS.getValor() != null) {
	        atualizandoWBS.setValor(atualizadoWBS.getValor());
	    }
	    if (atualizadoWBS.getMaterial() != null) {
	        atualizandoWBS.setMaterial(atualizadoWBS.getMaterial());
	    }
	    if (atualizadoWBS.getHh() != null) {
	        atualizandoWBS.setHh(atualizadoWBS.getHh());
	    }
	    if (projetoOptional.isPresent()) {
	        atualizandoWBS.setProjeto(projetoOptional.get());
	    }

	    if (liderDeProjetoOptional.isPresent()) {
	        atualizandoWBS.setLiderDeProjeto(liderDeProjetoOptional.get());
	    }

	    WBE wbeAtualizado = interfaceWBS.save(atualizandoWBS);

	    return ResponseEntity.ok(wbeAtualizado);
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<List<WBE>> apagarWBS(@PathVariable Long id) {
	    Optional<WBE> encontrarPorIdWBS = interfaceWBS.findById(id);

	    if (encontrarPorIdWBS.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    interfaceWBS.delete(encontrarPorIdWBS.get());
	    
	    List<WBE> wbesRestantes = interfaceWBS.findAll();

	    return ResponseEntity.ok(wbesRestantes);
	}


	private boolean validadorDeCabecalho(Row linhaDoCabecalho) {
		if (linhaDoCabecalho == null) {
			return false;
		}

		// Verifique se as células nas colunas 1, 4 e 7 contêm os cabeçalhos esperados
		// (coluna 1 = 0, coluna 2 = 1, etc...)
		Cell coluna1 = linhaDoCabecalho.getCell(1);
		Cell coluna4 = linhaDoCabecalho.getCell(4);
		Cell coluna7 = linhaDoCabecalho.getCell(6);

		return coluna1 != null && coluna1.getStringCellValue().equals("WBS") && coluna4 != null
				&& coluna4.getStringCellValue().equals("Valor") && coluna7 != null
				&& coluna7.getStringCellValue().equals("HH");
	}
}
