package com.example.budget.application;

import com.example.budget.domain.Category;
import com.example.budget.domain.Transaction;
import com.example.budget.domain.TransactionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Implementação em memória do repositório, usada apenas nos testes de use case. */
class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> store = new ArrayList<>();

    @Override
    public Transaction save(Transaction transaction) {
        store.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findByCategory(Category category) {
        return store.stream().filter(t -> t.category() == category).toList();
    }

    @Override
    public List<Transaction> findByDateBetween(LocalDate from, LocalDate to) {
        return store.stream()
                .filter(t -> !t.date().isBefore(from) && !t.date().isAfter(to))
                .toList();
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(store);
    }
}
