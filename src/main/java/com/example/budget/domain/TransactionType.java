package com.example.budget.domain;

/** Natureza da transação no orçamento. */
public enum TransactionType {
    INCOME,   // receita (entra dinheiro)
    EXPENSE;  // despesa (sai dinheiro)

    public static TransactionType fromString(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Tipo de transação não informado");
        }
        return switch (raw.trim().toUpperCase()) {
            case "INCOME", "RECEITA", "ENTRADA" -> INCOME;
            case "EXPENSE", "DESPESA", "SAIDA", "SAÍDA", "GASTO" -> EXPENSE;
            default -> throw new IllegalArgumentException("Tipo inválido: " + raw);
        };
    }
}
