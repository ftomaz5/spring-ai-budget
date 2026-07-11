package com.example.budget.application;

import com.example.budget.domain.Category;
import com.example.budget.domain.Transaction;
import com.example.budget.domain.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso: listar transações de uma categoria. */
@Service
public class ListTransactionsByCategoryUseCase {

    private final TransactionRepository repository;

    public ListTransactionsByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> execute(String categoryRaw) {
        Category category = Category.fromString(categoryRaw);
        return repository.findByCategory(category);
    }
}
