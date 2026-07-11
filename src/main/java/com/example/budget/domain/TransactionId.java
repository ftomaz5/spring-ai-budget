package com.example.budget.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object que identifica unicamente uma transação. */
public record TransactionId(UUID value) {

    public TransactionId {
        Objects.requireNonNull(value, "TransactionId não pode ser nulo");
    }

    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(String raw) {
        return new TransactionId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
