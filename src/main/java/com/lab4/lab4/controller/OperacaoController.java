package com.lab4.lab4.controller;

import com.lab4.lab4.dto.OperacaoDTO;
import com.lab4.lab4.entity.Operacao;
import com.lab4.lab4.service.CalculadoraNotasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/operacoes")
public class OperacaoController {

    @Autowired
    private CalculadoraNotasService service;

    @PostMapping("/calcular")
    public ResponseEntity<OperacaoDTO> calcular(@RequestBody OperacaoDTO dto) {
        OperacaoDTO resultado = service.calcular(dto);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operacao> buscarPorId(@PathVariable Long id) {
        Optional<Operacao> operacao = service.buscarPorId(id);
        return operacao.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
