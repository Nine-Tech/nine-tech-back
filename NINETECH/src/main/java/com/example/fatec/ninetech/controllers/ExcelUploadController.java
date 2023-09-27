package com.example.fatec.ninetech.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.WBE;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.WBSInterface;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
    private String dadosWBSRecemCriados;
    

    @PostMapping("/criarWBS")
    public ResponseEntity<String> processarExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(1);

            Iterator<Row> rowIterator = sheet.iterator();
            
            Row linhaDoCabecalho = rowIterator.next();
            if (!validadorDeCabecalho(linhaDoCabecalho)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo sem o padrão necessário");
            }

            List<Map<String, Object>> dadosProjetoEListaWBS = new ArrayList<>();
            Projeto projetoRecemCriado = null;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell colunaDoWBS = row.getCell(1);
                Cell colunaDoValor = row.getCell(4);
                Cell colunaDoHH = row.getCell(6);

                if (colunaDoWBS != null && colunaDoValor != null && colunaDoHH != null) {
                    String wbe = colunaDoWBS.getStringCellValue();
                    double valor = colunaDoValor.getNumericCellValue();
                    double hh = colunaDoHH.getNumericCellValue();

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
                    } else {
                        WBE dadosWBE = new WBE();
                        dadosWBE.setHh(hh);
                        dadosWBE.setValor(valor);
                        dadosWBE.setWbe(wbe);
                        dadosWBE.setProjeto(projetoRecemCriado);

                        interfaceWBS.save(dadosWBE);

                        Map<String, Object> dadosWBSMap = new HashMap<>();
                        dadosWBSMap.put("projeto", projetoRecemCriado);
                        dadosWBSMap.put("wbe", dadosWBE);

                        dadosProjetoEListaWBS.add(dadosWBSMap);
                    }
                } else {
                    break;
                }
            }

            ObjectMapper mapeadorDeObjeto = new ObjectMapper();
            String dadosWBSJSON = mapeadorDeObjeto.writeValueAsString(dadosProjetoEListaWBS);

            return ResponseEntity.ok(dadosWBSJSON);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo.");
        }
    }


	@GetMapping("/listarWBS/{id}")
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
	
//	@GetMapping("/listarWBSLider/{idLider}")
//	@JsonIgnoreProperties({"wbes"})
//	public ResponseEntity<List<WBE>> listarWBEsPorLiderId(@PathVariable Long idLider) {
//	    try {
//	        List<WBE> wbes = interfaceWBS.findByLiderDeProjeto_Id(idLider);
//	        if (!wbes.isEmpty()) {
//	            return new ResponseEntity<>(wbes, HttpStatus.OK);
//	        } else {
//	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//	        }
//	    } catch (Exception e) {
//	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	}
	
	// Isolar as variáveis e salvar apenas as que mudaram, se não ele seta para nulo
	@PutMapping("/atualizarWBS/{id}")
	public ResponseEntity<String> atualizarWBS(@PathVariable Long id, @RequestBody WBE atualizadoWBS) {
		// Trecho repetitivo, criar Helper?
		Optional<WBE> encontrarPorIdWBS = interfaceWBS.findById(id);

		if (encontrarPorIdWBS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tabela não encontrada.");
		}
		// Fim do trecho repetitivo

		WBE atualizandoWBS = encontrarPorIdWBS.get();

		if (atualizadoWBS.getWbe() != null) {
			atualizandoWBS.setWbe(atualizadoWBS.getWbe());
		}
		if (atualizadoWBS.getValor() != null) {
			atualizandoWBS.setValor(atualizadoWBS.getValor());
		}
		if (atualizadoWBS.getHh() != null) {
			atualizandoWBS.setHh(atualizadoWBS.getHh());
		}
		if (atualizandoWBS.getProjeto() != null){
			atualizandoWBS.setProjeto(atualizadoWBS.getProjeto());
		}

		interfaceWBS.save(atualizandoWBS);

		return ResponseEntity.ok("WBS atualizado com sucesso.");
	}

	@DeleteMapping("/apagarWBS/{id}")
	public ResponseEntity<String> apagarWBS(@PathVariable Long id) {
		// Trecho repetitivo, criar Helper?
		Optional<WBE> encontrarPorIdWBS = interfaceWBS.findById(id);

		if (encontrarPorIdWBS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id não encontrado.");
		}
		// Fim do trecho repetitivo

		interfaceWBS.delete(encontrarPorIdWBS.get());

		return ResponseEntity.ok("WBS excluído com sucesso.");
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
