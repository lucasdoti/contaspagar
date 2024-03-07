package com.example.contaspagar;

import com.example.contaspagar.repository.ContaRepository;
import com.example.contaspagar.service.ContaService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    void importarContasViaCSV_DeveImportarContasCorretamente() throws IOException {
        String csvData = "data_vencimento,data_pagamento,valor,descricao,situacao\n" +
                "2022-03-15,,100.00,Conta de Março,PENDENTE\n" +
                "2022-04-10,2022-04-12,150.50,Conta de Abril,PAGA";

        MultipartFile file = new MockMultipartFile("contas.csv", csvData.getBytes(StandardCharsets.UTF_8));

        when(contaRepository.saveAll(any())).thenReturn(new ArrayList<>());

        contaService.importarContasViaCSV(file);

        Mockito.verify(contaRepository, Mockito.times(1)).saveAll(any());
    }

    @Test
    void importarContasViaCSV_LancaExcecaoQuandoArquivoVazio() {
        MultipartFile file = new MockMultipartFile("contas.csv", "".getBytes());

        try {
            contaService.importarContasViaCSV(file);
        } catch (IllegalArgumentException e) {
            assertEquals("Arquivo CSV vazio.", e.getMessage());
        }
    }

    @Test
    void importarContasViaCSV_LancaExcecaoQuandoCamposInvalidos() throws IOException {
        String csvData = "data_vencimento,data_pagamento,valor,descricao,situacao\n" +
                "2022-03-15,,100.00,,PENDENTE\n" + // Campo descricao está vazio
                "2022-04-10,2022-04-12,abc,Conta de Abril,PAGA"; // Campo valor contém valor não numérico

        MultipartFile file = new MockMultipartFile("contas.csv", csvData.getBytes(StandardCharsets.UTF_8));

        try {
            contaService.importarContasViaCSV(file);
        } catch (IllegalArgumentException e) {
            assertEquals("Erro ao processar o arquivo CSV. Linha 1: O campo 'descricao' não pode estar vazio. " +
                    "Linha 2: O campo 'valor' deve ser um valor numérico.", e.getMessage());
        }
    }

    @Test
    void importarContasViaCSV_LancaExcecaoQuandoDatasInvalidas() throws IOException {
        String csvData = "data_vencimento,data_pagamento,valor,descricao,situacao\n" +
                "2022-03-15,2022-02-01,100.00,Conta de Março,PENDENTE\n"; // data_pagamento anterior a data_vencimento

        MultipartFile file = new MockMultipartFile("contas.csv", csvData.getBytes(StandardCharsets.UTF_8));

        try {
            contaService.importarContasViaCSV(file);
        } catch (IllegalArgumentException e) {
            assertEquals("Erro ao processar o arquivo CSV. Linha 1: A data de pagamento não pode ser anterior à data de vencimento.", e.getMessage());
        }
    }
}
