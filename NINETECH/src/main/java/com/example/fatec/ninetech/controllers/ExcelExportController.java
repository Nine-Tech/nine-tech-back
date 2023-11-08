package com.example.fatec.ninetech.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.Pacotes;
import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.models.Subpacotes;
import com.example.fatec.ninetech.repositories.PacotesInterface;
import com.example.fatec.ninetech.repositories.ProjetoInterface;
import com.example.fatec.ninetech.repositories.SubpacotesInterface;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExcelExportController {

	@Autowired
	private ProjetoInterface interfaceProjeto;

	@Autowired
	private SubpacotesInterface interfaceSubpacotes;

	@Autowired
	private PacotesInterface interfacePacotes;

	@GetMapping("/exportToExcel/{projeto_id}")
	public void exportToExcel(@PathVariable("projeto_id") Long projetoId) {
		Projeto projeto = interfaceProjeto.findById(projetoId).orElse(null);

		if (projeto != null) {
			String nomeProjeto = projeto.getNome();
			double porcentagemRealExecutada = projeto.getPorcentagem();

			try (Workbook workbook = new XSSFWorkbook()) {
				Sheet sheet = workbook.createSheet("WBS - Progresso Real");

				// Criação das células B2 e C2 com os valores "WBS" e "% Real Executada"
				Row headerRow = sheet.createRow(1);
				Cell cellB2 = headerRow.createCell(1);
				cellB2.setCellValue("WBS");
				Cell cellC2 = headerRow.createCell(2);
				cellC2.setCellValue("% Real Executada");

				// Criação das células B3 e C3 com os valores do projeto
				Row dataRow = sheet.createRow(2);
				Cell cellB3 = dataRow.createCell(1);
				cellB3.setCellValue(nomeProjeto);
				Cell cellC3 = dataRow.createCell(2);
				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				cellC3.setCellValue(decimalFormat.format(porcentagemRealExecutada));

				List<Pacotes> pacotesList = interfacePacotes.findByProjetoId(projetoId);

				int rowNumber = 3; // Iniciar a partir da quarta linha (B4 e C4)
				for (Pacotes pacote : pacotesList) {
					Row pacoteRow = sheet.createRow(rowNumber);

					// Célula Bx (onde x é a linha atual)
					Cell nomePacoteCell = pacoteRow.createCell(1);
					nomePacoteCell.setCellValue(pacote.getNome());

					// Célula Cx (onde x é a linha atual)
					Cell porcentagemPacoteCell = pacoteRow.createCell(2);
					porcentagemPacoteCell.setCellValue(decimalFormat.format(pacote.getPorcentagem()));

					// Verificar se existe subpacote relacionado ao pacote atual
					List<Subpacotes> subpacotesList = interfaceSubpacotes.findByPacotesId(pacote.getId());

					if (!subpacotesList.isEmpty()) {
						for (Subpacotes subpacote : subpacotesList) {
							// Se o nome do pacote for igual ao nome do subpacote, não adicionar o subpacote
							if (!pacote.getNome().equals(subpacote.getNome())) {
								Row subpacoteRow = sheet.createRow(rowNumber + 1);

								Cell nomeSubpacoteCell = subpacoteRow.createCell(1);
								nomeSubpacoteCell.setCellValue(subpacote.getNome());

								// Célula Ex (onde x é a linha atual)
								Cell porcentagemSubpacoteCell = subpacoteRow.createCell(2);
								porcentagemSubpacoteCell.setCellValue(decimalFormat.format(subpacote.getPorcentagem()));

								rowNumber++;
							}
						}
					}

					rowNumber++;
				}

				String filmeName = nomeProjeto + "_exportado.xlsx";

				try (FileOutputStream fileOut = new FileOutputStream(filmeName)) {
					workbook.write(fileOut);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		else {
			// Projeto não encontrado com o ID informado
			// Trate esse caso conforme apropriado, por exemplo, retornando uma mensagem de
			// erro.
		}
	}
}
