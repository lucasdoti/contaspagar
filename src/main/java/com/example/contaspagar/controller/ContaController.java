package com.example.contaspagar.controller;

import com.example.contaspagar.model.Conta;
import com.example.contaspagar.model.SituacaoConta;
import com.example.contaspagar.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/contas")
public class ContaController {
    @Autowired
    private ContaService contaService;

    @PostMapping
    public ResponseEntity<Conta> cadastrarConta(@RequestBody Conta conta) {
        Conta novaConta = contaService.cadastrarConta(conta);
        return new ResponseEntity<>(novaConta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conta> atualizarConta(@PathVariable Long id, @RequestBody Conta conta) {
        Conta contaAtualizada = contaService.atualizarConta(id, conta);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Void> alterarSituacaoConta(@PathVariable Long id, @RequestParam SituacaoConta situacao) {
        contaService.alterarSituacaoConta(id, situacao);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<Conta>> obterContas(
            @RequestParam(required = false) LocalDate dataVencimento,
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Conta> contas = contaService.obterContas(dataVencimento, descricao, (Pageable) PageRequest.of(page, size));
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> obterContaPorId(@PathVariable Long id) {
        Conta conta = contaService.obterContaPorId(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/valor-total-pago")
    public ResponseEntity<BigDecimal> obterValorTotalPagoPorPeriodo(
            @RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim) {
        BigDecimal valorTotalPago = contaService.obterValorTotalPagoPorPeriodo(inicio, fim);
        return ResponseEntity.ok(valorTotalPago);
    }

    @PostMapping("/importar-csv")
    public ResponseEntity<Void> importarContasViaCSV(@RequestParam("file") MultipartFile file) {
        contaService.importarContasViaCSV(file);
        return ResponseEntity.ok().build();
    }
}


