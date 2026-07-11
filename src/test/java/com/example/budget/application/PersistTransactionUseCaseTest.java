package com.example.budget.application;

import com.example.budget.domain.Category;
import com.example.budget.domain.Transaction;
import com.example.budget.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PersistTransactionUseCaseTest {

    private InMemoryTransactionRepository repository;
    private PersistTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        useCase = new PersistTransactionUseCase(repository);
    }

    @Test
    void persisteUmaDespesaValida() {
        Transaction result = useCase.execute(new PersistTransactionUseCase.Command(
                "Almoço", new BigDecimal("42.50"), "EXPENSE", "FOOD", LocalDate.now()));

        assertEquals(TransactionType.EXPENSE, result.type());
        assertEquals(Category.FOOD, result.category());
        assertEquals(new BigDecimal("42.50"), result.amount());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void aceitaSinonimosEmPortugues() {
        Transaction result = useCase.execute(new PersistTransactionUseCase.Command(
                "Salário", new BigDecimal("5000"), "receita", "salario", LocalDate.now()));

        assertEquals(TransactionType.INCOME, result.type());
        assertEquals(Category.SALARY, result.category());
    }

    @Test
    void rejeitaValorZeroOuNegativo() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                new PersistTransactionUseCase.Command(
                        "Erro", BigDecimal.ZERO, "EXPENSE", "FOOD", LocalDate.now())));
    }

    @Test
    void rejeitaDataNoFuturo() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                new PersistTransactionUseCase.Command(
                        "Futuro", new BigDecimal("10"), "EXPENSE", "FOOD",
                        LocalDate.now().plusDays(1))));
    }
}
