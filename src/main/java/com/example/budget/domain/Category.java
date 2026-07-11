package com.example.budget.domain;

/** Categorias de orçamento suportadas. */
public enum Category {
    FOOD,
    TRANSPORT,
    HOUSING,
    LEISURE,
    HEALTH,
    EDUCATION,
    SALARY,
    OTHER;

    public static Category fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return OTHER;
        }
        return switch (raw.trim().toUpperCase()) {
            case "FOOD", "ALIMENTACAO", "ALIMENTAÇÃO", "COMIDA", "MERCADO" -> FOOD;
            case "TRANSPORT", "TRANSPORTE", "UBER", "GASOLINA", "COMBUSTIVEL" -> TRANSPORT;
            case "HOUSING", "MORADIA", "ALUGUEL", "CASA", "CONTAS" -> HOUSING;
            case "LEISURE", "LAZER", "DIVERSAO", "VIAGEM" -> LEISURE;
            case "HEALTH", "SAUDE", "SAÚDE", "FARMACIA", "MEDICO" -> HEALTH;
            case "EDUCATION", "EDUCACAO", "EDUCAÇÃO", "CURSO", "ESCOLA" -> EDUCATION;
            case "SALARY", "SALARIO", "SALÁRIO", "RENDA" -> SALARY;
            default -> OTHER;
        };
    }
}
