package com.example.fatec.ninetech.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatec.ninetech.models.EngenheiroChefe;
import com.example.fatec.ninetech.repositories.EngenheiroChefeInterface;

@RestController
@RequestMapping("/engenheiro")
public class EngenheiroChefeController {

    @Autowired
    private EngenheiroChefeInterface interfaceEngenheiroChefe;
	
	@GetMapping
    public ResponseEntity<List<EngenheiroChefe>> listarLideres() {
        List<EngenheiroChefe> engenheiros = interfaceEngenheiroChefe.findAll();
        return new ResponseEntity<>(engenheiros, HttpStatus.OK);
    }

}
