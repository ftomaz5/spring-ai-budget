package com.example.budget.domain;

import java.time.LocalDate;
import java.util.List;

/** Porta de saída (DDD). A infraestrutura fornece a implementação (JPA). */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByCategory(Category category);

    /** Transações cujo campo date está no intervalo [from, to] inclusivo. */
    List<Transaction> findByDateBetween(LocalDate from, LocalDate to);

    List<Transaction> findAll();
}
