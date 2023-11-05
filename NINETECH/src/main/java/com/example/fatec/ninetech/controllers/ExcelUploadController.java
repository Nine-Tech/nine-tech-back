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

import javax.management.relation.RelationNotFoundException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.example.fatec.ninetech.models.Subpacotes;

import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;
import com.example.fatec.ninetech.repositories.LiderDeProjetoInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/upload")
public class ExcelUploadController {

	@Autowired
	private PacotesInterface interfacePacotes;

	@Autowired
	private SubpacotesInterface interfaceSubpacotes;

	@Autowired
	private EngenheiroChefeInterface interfaceEngenheiroChefe;

	@Autowired
	private ProjetoInterface interfaceProjeto;

	@Autowired
	private LiderDeProjetoInterface interfaceLiderDeProjeto;

	@PostMapping
	public ResponseEntity<List<Pacotes>> processarExcel(@RequestParam("file") MultipartFile file,
			@RequestParam("dataTermino") LocalDate dataTermino,
			@RequestParam("hhValue") double hhValue) {
		try (InputStream is = file.getInputStream();
				XSSFWorkbook workbook = new XSSFWorkbook(is)) {

			XSSFSheet sheet = workbook.getSheetAt(1);

			Iterator<Row> rowIterator = sheet.iterator();

			Row linhaDoCabecalho = rowIterator.next();
			if (!validadorDeCabecalho(linhaDoCabecalho)) {
				return ResponseEntity.badRequest().build();
			}

			List<Pacotes> wbes = new ArrayList<>();
			List<Subpacotes> subpacotesLista = new ArrayList<>();
			Projeto projetoRecemCriado = null;
			Long idPai = null;
			boolean pacoteAnteriorTemFilhos = false;
			int espacosIniciais = 0;
			String wbe = "";

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Cell colunaDoWBS = row.getCell(1);

				if (colunaDoWBS != null) {
					wbe = colunaDoWBS.getStringCellValue();
					System.out.println(wbe);

					espacosIniciais = 0;

					// Contar os espaços no início da string
					while (espacosIniciais < wbe.length() && wbe.charAt(espacosIniciais) == ' ') {
						espacosIniciais++;
					}

					Pacotes dadosWBE = new Pacotes();
					dadosWBE.setNome(wbe);
					dadosWBE.setProjeto(projetoRecemCriado);

					if (espacosIniciais == 0) {
						// Se for 0, salvar no projetoRecemCriado
						if (projetoRecemCriado == null) {

							EngenheiroChefe idEngenheiroChefe = interfaceEngenheiroChefe.findById(1L).orElse(null);
							Projeto dadosProjeto = new Projeto();
							dadosProjeto.setNome(wbe);
							dadosProjeto.setEngenheiroChefe(idEngenheiroChefe);

							LocalDate dataInicioAgora = LocalDate.now();
							dadosProjeto.setData_inicio(dataInicioAgora);
							
							dadosProjeto.setData_final(dataTermino);
							dadosProjeto.setValor_homem_hora(hhValue);

							projetoRecemCriado = interfaceProjeto.save(dadosProjeto);
						}

						if (espacosIniciais == 1 && !pacoteAnteriorTemFilhos) {
							Subpacotes dadosSubpacote = new Subpacotes();
							Optional<Pacotes> EncontrarPacotePai = interfacePacotes.findById(idPai);
							dadosSubpacote.setPacotes(EncontrarPacotePai.get());
							dadosSubpacote.setNome(wbe);
							Subpacotes subpacoteSalvo = interfaceSubpacotes.save(dadosSubpacote);
							subpacotesLista.add(subpacoteSalvo);
						}

						pacoteAnteriorTemFilhos = false; // Redefina para o próximo pacote.

					} else if (espacosIniciais == 1) {

						Pacotes dadosPacote = new Pacotes();
						dadosPacote.setNome(wbe);
						dadosPacote.setProjeto(projetoRecemCriado);
						Pacotes pacoteSalvo = interfacePacotes.save(dadosPacote);
						wbes.add(pacoteSalvo);
						idPai = pacoteSalvo.getId();

						if (pacoteAnteriorTemFilhos) {
							Subpacotes dadosSubpacote = new Subpacotes();
							Optional<Pacotes> EncontrarPacotePai = interfacePacotes.findById(idPai);
							dadosSubpacote.setPacotes(EncontrarPacotePai.get());
							dadosSubpacote.setNome(wbe);
							Subpacotes subpacoteSalvo = interfaceSubpacotes.save(dadosSubpacote);
							subpacotesLista.add(subpacoteSalvo);
						}

						pacoteAnteriorTemFilhos = false; // Indica que o pacote atual não tem filhos.

					} else {

						// Se for 4, adicionar filho = true e salvar
						Subpacotes dadosSubpacote = new Subpacotes();
						Optional<Pacotes> EncontrarPacotePai = interfacePacotes.findById(idPai);
						dadosSubpacote.setPacotes(EncontrarPacotePai.get());
						dadosSubpacote.setNome(wbe);
						Subpacotes subpacoteSalvo = interfaceSubpacotes.save(dadosSubpacote);
						subpacotesLista.add(subpacoteSalvo);

						// Se não tiver filhos, setar pacoteAnteriorTemFilhos para false
						pacoteAnteriorTemFilhos = true;

					}
				} else {

					if (espacosIniciais == 1 && !pacoteAnteriorTemFilhos) {
						Subpacotes dadosSubpacote = new Subpacotes();
						Optional<Pacotes> EncontrarPacotePai = interfacePacotes.findById(idPai);
						dadosSubpacote.setPacotes(EncontrarPacotePai.get());
						dadosSubpacote.setNome(wbe);
						Subpacotes subpacoteSalvo = interfaceSubpacotes.save(dadosSubpacote);
						subpacotesLista.add(subpacoteSalvo);
					}

				}
			}

			return ResponseEntity.ok(wbes);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Retorna todo o Projeto
	@GetMapping("/todosfilhos/{id}")
	public ResponseEntity<List<Object>> listarPacotesESeusSubpacotes(@PathVariable Long id) {
		Optional<Projeto> projetoOptional = interfaceProjeto.findById(id);

		if (projetoOptional.isPresent()) {
			Projeto projeto = projetoOptional.get();
			Long projetoId = projeto.getId();

			List<Object> pacotesESeusSubpacotes = new ArrayList<>();

			List<Pacotes> pacotes = interfacePacotes.findByProjetoId(projetoId);

			for (Pacotes pacote : pacotes) {
				List<Subpacotes> subpacotes = interfaceSubpacotes.findByPacotesId(pacote.getId());

				Map<String, Object> pacoteMap = new HashMap<>();
				pacoteMap.put("nome", pacote.getNome());
				pacoteMap.put("porcentagem", pacote.getPorcentagem());
				pacoteMap.put("valor_total", pacote.getValor_total());
				pacotesESeusSubpacotes.add(pacoteMap);

				for (Subpacotes subpacote : subpacotes) {
					pacotesESeusSubpacotes.add(subpacote);
				}
			}

			return new ResponseEntity<>(pacotesESeusSubpacotes, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Retorna Pais e Filhos pelo Get pelo Id do Projeto
	@GetMapping("/{id}")
	@JsonIgnoreProperties({ "wbes" })
	public ResponseEntity<List<Pacotes>> listarWBEsPorProjetoId(@PathVariable Long id) {
		try {
			List<Pacotes> wbes = interfacePacotes.findByProjetoId(id);
			if (!wbes.isEmpty()) {
				return new ResponseEntity<>(wbes, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Retorna os Filhos pelo Get pelo Id do Pacote Pai
	@GetMapping("/pacotes/{id}")
	@JsonIgnoreProperties({ "wbes" })
	public ResponseEntity<List<Pacotes>> listarWBEsPorPacoteId(@PathVariable Long id) {
		try {
			List<Pacotes> wbes = interfacePacotes.findAll();
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
	@JsonIgnoreProperties({ "wbes" })
	public ResponseEntity<List<Pacotes>> listarWBEsPorLiderId(@PathVariable Long idLider) {
		try {
			List<Pacotes> wbes = interfacePacotes.findAll();
			if (!wbes.isEmpty()) {
				return new ResponseEntity<>(wbes, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<List<Pacotes>> apagarWBS(@PathVariable Long id) {
		Optional<Pacotes> encontrarPorIdWBS = interfacePacotes.findById(id);

		if (encontrarPorIdWBS.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		interfacePacotes.delete(encontrarPorIdWBS.get());

		List<Pacotes> wbesRestantes = interfacePacotes.findAll();

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
