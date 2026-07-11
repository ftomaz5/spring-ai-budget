package com.example.budget.application;

import com.example.budget.domain.BalanceSummary;
import com.example.budget.domain.Transaction;
import com.example.budget.domain.TransactionRepository;
import com.example.budget.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * EVOLUÇÃO DO PROJETO.
 * Caso de uso: calcular o resumo de saldo (receitas, despesas e saldo)
 * dentro de um período. Exposto ao LLM como uma nova ferramenta de Tool Calling.
 */
@Service
public class GetBalanceSummaryUseCase {

    private final TransactionRepository repository;

    public GetBalanceSummaryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public BalanceSummary execute(LocalDate from, LocalDate to) {
        LocalDate start = from == null ? LocalDate.now().withDayOfMonth(1) : from;
        LocalDate end = to == null ? LocalDate.now() : to;
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final");
        }

        List<Transaction> transactions = repository.findByDateBetween(start, end);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.type() == TransactionType.INCOME)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.type() == TransactionType.EXPENSE)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BalanceSummary.of(start, end, totalIncome, totalExpense, transactions.size());
    }
}
