package com.example.fatec.ninetech.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.Projeto;
import com.example.fatec.ninetech.repositories.ProjetoInterface;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;



import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;



@RestController
@RequestMapping("/export")
public class ExcelExportController {

    @Autowired
    private ProjetoInterface interfaceProjeto;

    @GetMapping("/exportToExcel/{projeto_id}")
    public void exportToExcel(@PathVariable("projeto_id") Long projetoId) {
        Projeto projeto = interfaceProjeto.findById(projetoId).orElse(null);

        if (projeto != null) {
            String nomeProjeto = projeto.getNome();
            double porcentagemRealExecutada = projeto.getPorcentagem();

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Projeto Data");

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
                cellC3.setCellValue(porcentagemRealExecutada);

                try (FileOutputStream fileOut = new FileOutputStream("projeto_data.xlsx")) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Projeto não encontrado com o ID informado
            // Trate esse caso conforme apropriado, por exemplo, retornando uma mensagem de erro.
        }
    }
}
