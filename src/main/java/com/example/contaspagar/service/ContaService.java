package com.example.contaspagar.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.contaspagar.model.Conta;
import com.example.contaspagar.model.SituacaoConta;
import com.example.contaspagar.repository.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    public Conta cadastrarConta(Conta conta) {
        conta.setSituacao(SituacaoConta.PENDENTE);
        return contaRepository.save(conta);
    }

    public Conta atualizarConta(Long id, Conta contaAtualizada) {
        Conta contaExistente = obterContaPorId(id);
        contaExistente.setDataVencimento(contaAtualizada.getDataVencimento());
        contaExistente.setDataPagamento(contaAtualizada.getDataPagamento());
        contaExistente.setValor(contaAtualizada.getValor());
        contaExistente.setDescricao(contaAtualizada.getDescricao());

        return contaRepository.save(contaExistente);
    }

    public void alterarSituacaoConta(Long id, SituacaoConta situacao) {
        Conta conta = obterContaPorId(id);
        conta.setSituacao(situacao);
        contaRepository.save(conta);
    }

    public Page<Conta> obterContas(LocalDate dataVencimento, String descricao, Pageable pageable) {
        if (dataVencimento != null && descricao != null) {
            return contaRepository.findByDataVencimentoAndDescricaoContaining(dataVencimento, descricao, pageable);
        } else if (dataVencimento != null) {
            return contaRepository.findByDataVencimento(dataVencimento, pageable);
        } else if (descricao != null) {
            return contaRepository.findByDescricaoContaining(descricao, pageable);
        } else {
            return contaRepository.findAll((org.springframework.data.domain.Pageable) pageable);
        }
    }

    public Conta obterContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada com o ID: " + id));
    }

    public BigDecimal obterValorTotalPagoPorPeriodo(LocalDate inicio, LocalDate fim) {
        return contaRepository.obterValorTotalPagoPorPeriodo(inicio, fim);
    }

    @Transactional
    public void importarContasViaCSV(MultipartFile file) {
        try (CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(new InputStreamReader(file.getInputStream()))) {
            List<Conta> contasParaImportar = new ArrayList<>();

            for (CSVRecord record : parser) {
                Conta conta = new Conta();
                conta.setDataVencimento(LocalDate.parse(record.get("data_vencimento")));
                conta.setDataPagamento(record.get("data_pagamento").isEmpty() ? null : LocalDate.parse(record.get("data_pagamento")));
                conta.setValor(new BigDecimal(record.get("valor")));
                conta.setDescricao(record.get("descricao"));
                conta.setSituacao(SituacaoConta.PENDENTE);

                contasParaImportar.add(conta);
            }

            contaRepository.saveAll(contasParaImportar);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo CSV.", e);
        }
    }
}


