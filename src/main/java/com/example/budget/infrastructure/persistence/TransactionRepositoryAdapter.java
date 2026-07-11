package com.example.budget.infrastructure.persistence;

import com.example.budget.domain.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/** Adaptador que implementa a porta de domínio usando JPA. */
@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpa;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Transaction save(Transaction t) {
        TransactionEntity entity = new TransactionEntity(
                t.id().value(), t.description(), t.amount(), t.type(), t.category(), t.date());
        return toDomain(jpa.save(entity));
    }

    @Override
    public List<Transaction> findByCategory(Category category) {
        return jpa.findByCategory(category).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Transaction> findByDateBetween(LocalDate from, LocalDate to) {
        return jpa.findByDateBetween(from, to).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Transaction> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    private Transaction toDomain(TransactionEntity e) {
        return new Transaction(
                new TransactionId(e.getId()),
                e.getDescription(),
                e.getAmount(),
                e.getType(),
                e.getCategory(),
                e.getDate());
    }
}
