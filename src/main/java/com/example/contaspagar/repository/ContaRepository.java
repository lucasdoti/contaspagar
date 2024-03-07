package com.example.contaspagar.repository;

import com.example.contaspagar.model.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Page<Conta> findByDataVencimentoAndDescricaoContaining(LocalDate dataVencimento, String descricao, Pageable pageable);

    Page<Conta> findByDataVencimento(LocalDate dataVencimento, Pageable pageable);

    Page<Conta> findByDescricaoContaining(String descricao, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Conta c WHERE c.dataPagamento BETWEEN :inicio AND :fim")
    BigDecimal obterValorTotalPagoPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}

