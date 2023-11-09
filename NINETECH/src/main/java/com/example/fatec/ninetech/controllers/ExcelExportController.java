package com.example.fatec.ninetech.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap; // Importe esta classe
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/export")
public class ExcelExportController {

    @Autowired
    private ProjetoInterface interfaceProjeto;

    @Autowired
    private SubpacotesInterface interfaceSubpacotes;

    @Autowired
    private PacotesInterface interfacePacotes;

    @GetMapping("/{projeto_id}")
    public List<Map<String, Object>> getExcelData(@PathVariable("projeto_id") Long projetoId) {
        Projeto projeto = interfaceProjeto.findById(projetoId).orElse(null);

        if (projeto != null) {
            List<Map<String, Object>> excelData = new ArrayList<>();

            // Adiciona os dados do projeto à lista
            Map<String, Object> projetoMap = new LinkedHashMap<>(); // Alteração aqui
            projetoMap.put("WBS", projeto.getNome());
            projetoMap.put("% Real Executada", projeto.getPorcentagem());
            excelData.add(projetoMap);

            List<Pacotes> pacotesList = interfacePacotes.findByProjetoId(projetoId);

            for (Pacotes pacote : pacotesList) {
                // Adiciona os dados do pacote à lista
                Map<String, Object> pacoteMap = new LinkedHashMap<>(); // Alteração aqui
                pacoteMap.put("WBS", pacote.getNome());
                pacoteMap.put("% Real Executada", pacote.getPorcentagem());
                excelData.add(pacoteMap);

                // Verifica se há subpacotes e adiciona os dados à lista
                List<Subpacotes> subpacotesList = interfaceSubpacotes.findByPacotesId(pacote.getId());

                for (Subpacotes subpacote : subpacotesList) {
                    if (!pacote.getNome().equals(subpacote.getNome())) {
                        Map<String, Object> subpacoteMap = new LinkedHashMap<>(); // Alteração aqui
                        subpacoteMap.put("WBS", subpacote.getNome());
                        subpacoteMap.put("% Real Executada", subpacote.getPorcentagem());
                        excelData.add(subpacoteMap);
                    }
                }
            }

            return excelData;
        } else {
            // Projeto não encontrado com o ID informado
            // Trate esse caso conforme apropriado, por exemplo, retornando uma mensagem de erro.
            return Collections.emptyList();
        }
    }
}
