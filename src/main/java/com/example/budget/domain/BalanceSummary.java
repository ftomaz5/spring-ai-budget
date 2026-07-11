package com.example.budget.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resultado do resumo de saldo por período (Value Object).
 * totalIncome - totalExpense = balance.
 */
public record BalanceSummary(
        LocalDate from,
        LocalDate to,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        long transactionCount) {

    public static BalanceSummary of(LocalDate from,
                                    LocalDate to,
                                    BigDecimal totalIncome,
                                    BigDecimal totalExpense,
                                    long count) {
        BigDecimal balance = totalIncome.subtract(totalExpense);
        return new BalanceSummary(from, to, totalIncome, totalExpense, balance, count);
    }
}
