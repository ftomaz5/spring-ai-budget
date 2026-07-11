package com.example.budget.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade central do domínio. Uma transação financeira do orçamento.
 * As invariantes de negócio são garantidas no próprio construtor.
 */
public class Transaction {

    private final TransactionId id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Category category;
    private final LocalDate date;

    public Transaction(TransactionId id,
                       String description,
                       BigDecimal amount,
                       TransactionType type,
                       Category category,
                       LocalDate date) {
        this.id = Objects.requireNonNull(id, "id é obrigatório");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória");
        }
        Objects.requireNonNull(amount, "amount é obrigatório");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo (use o tipo para receita/despesa)");
        }
        this.description = description.trim();
        this.amount = amount;
        this.type = Objects.requireNonNull(type, "type é obrigatório");
        this.category = category == null ? Category.OTHER : category;
        this.date = date == null ? LocalDate.now() : date;
    }

    /** Fábrica de criação de uma nova transação (gera o id). */
    public static Transaction create(String description,
                                     BigDecimal amount,
                                     TransactionType type,
                                     Category category,
                                     LocalDate date) {
        return new Transaction(TransactionId.newId(), description, amount, type, category, date);
    }

    /** Valor com sinal: despesas contam negativo, receitas positivo. */
    public BigDecimal signedAmount() {
        return type == TransactionType.EXPENSE ? amount.negate() : amount;
    }

    public TransactionId id() { return id; }
    public String description() { return description; }
    public BigDecimal amount() { return amount; }
    public TransactionType type() { return type; }
    public Category category() { return category; }
    public LocalDate date() { return date; }
}
