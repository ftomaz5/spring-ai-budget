package com.example.budget.application;

import com.example.budget.domain.Category;
import com.example.budget.domain.Transaction;
import com.example.budget.domain.TransactionRepository;
import com.example.budget.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Caso de uso: registrar uma nova transação, com validação de negócio. */
@Service
public class PersistTransactionUseCase {

    private final TransactionRepository repository;

    public PersistTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(Command command) {
        BigDecimal amount = command.amount();
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero");
        }
        TransactionType type = TransactionType.fromString(command.type());
        Category category = Category.fromString(command.category());
        LocalDate date = command.date() == null ? LocalDate.now() : command.date();
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da transação não pode estar no futuro");
        }

        Transaction transaction = Transaction.create(
                command.description(), amount, type, category, date);
        return repository.save(transaction);
    }

    /** Comando de entrada do caso de uso. */
    public record Command(
            String description,
            BigDecimal amount,
            String type,
            String category,
            LocalDate date) {
    }
}
