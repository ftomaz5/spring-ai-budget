package com.example.budget.infrastructure.persistence;

import com.example.budget.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByCategory(Category category);

    List<TransactionEntity> findByDateBetween(LocalDate from, LocalDate to);
}
