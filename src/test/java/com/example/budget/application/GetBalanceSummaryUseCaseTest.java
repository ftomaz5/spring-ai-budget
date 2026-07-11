package com.example.budget.application;

import com.example.budget.domain.BalanceSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GetBalanceSummaryUseCaseTest {

    private InMemoryTransactionRepository repository;
    private PersistTransactionUseCase persist;
    private GetBalanceSummaryUseCase summary;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        persist = new PersistTransactionUseCase(repository);
        summary = new GetBalanceSummaryUseCase(repository);
    }

    @Test
    void calculaSaldoNoPeriodo() {
        LocalDate today = LocalDate.now();
        persist.execute(new PersistTransactionUseCase.Command("Salário", new BigDecimal("5000"), "INCOME", "SALARY", today));
        persist.execute(new PersistTransactionUseCase.Command("Aluguel", new BigDecimal("1500"), "EXPENSE", "HOUSING", today));
        persist.execute(new PersistTransactionUseCase.Command("Mercado", new BigDecimal("500"), "EXPENSE", "FOOD", today));

        BalanceSummary result = summary.execute(today.minusDays(1), today.plusDays(0));

        assertEquals(0, new BigDecimal("5000").compareTo(result.totalIncome()));
        assertEquals(0, new BigDecimal("2000").compareTo(result.totalExpense()));
        assertEquals(0, new BigDecimal("3000").compareTo(result.balance()));
        assertEquals(3, result.transactionCount());
    }

    @Test
    void ignoraTransacoesForaDoPeriodo() {
        LocalDate today = LocalDate.now();
        persist.execute(new PersistTransactionUseCase.Command("Antiga", new BigDecimal("100"), "EXPENSE", "FOOD", today.minusMonths(2)));
        persist.execute(new PersistTransactionUseCase.Command("Recente", new BigDecimal("80"), "EXPENSE", "FOOD", today));

        BalanceSummary result = summary.execute(today.minusDays(5), today);

        assertEquals(1, result.transactionCount());
        assertEquals(0, new BigDecimal("80").compareTo(result.totalExpense()));
    }

    @Test
    void periodoInvalidoLancaErro() {
        LocalDate today = LocalDate.now();
        assertThrows(IllegalArgumentException.class,
                () -> summary.execute(today, today.minusDays(3)));
    }

    @Test
    void semTransacoesRetornaSaldoZero() {
        BalanceSummary result = summary.execute(LocalDate.now().minusDays(10), LocalDate.now());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.balance()));
        assertEquals(0, result.transactionCount());
    }
}
