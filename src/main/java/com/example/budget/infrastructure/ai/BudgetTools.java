package com.example.budget.infrastructure.ai;

import com.example.budget.application.GetBalanceSummaryUseCase;
import com.example.budget.application.ListTransactionsByCategoryUseCase;
import com.example.budget.application.PersistTransactionUseCase;
import com.example.budget.domain.BalanceSummary;
import com.example.budget.domain.Transaction;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Ferramentas expostas ao LLM via Tool Calling. Cada método @Tool é uma capacidade
 * que o modelo pode invocar para executar um caso de uso real do domínio.
 */
@Component
public class BudgetTools {

    private final PersistTransactionUseCase persistTransaction;
    private final ListTransactionsByCategoryUseCase listByCategory;
    private final GetBalanceSummaryUseCase getBalanceSummary;

    public BudgetTools(PersistTransactionUseCase persistTransaction,
                       ListTransactionsByCategoryUseCase listByCategory,
                       GetBalanceSummaryUseCase getBalanceSummary) {
        this.persistTransaction = persistTransaction;
        this.listByCategory = listByCategory;
        this.getBalanceSummary = getBalanceSummary;
    }

    @Tool(description = "Registra uma nova transação financeira (receita ou despesa) no orçamento do usuário.")
    public String addTransaction(
            @ToolParam(description = "Descrição curta da transação, ex: 'Almoço no restaurante'") String description,
            @ToolParam(description = "Valor positivo em reais, ex: 42.50") BigDecimal amount,
            @ToolParam(description = "Tipo da transação: INCOME para receita ou EXPENSE para despesa") String type,
            @ToolParam(description = "Categoria: FOOD, TRANSPORT, HOUSING, LEISURE, HEALTH, EDUCATION, SALARY ou OTHER") String category,
            @ToolParam(description = "Data no formato ISO yyyy-MM-dd. Se não informada, usa a data de hoje.", required = false) String date) {

        LocalDate parsedDate = (date == null || date.isBlank()) ? null : LocalDate.parse(date);
        Transaction saved = persistTransaction.execute(new PersistTransactionUseCase.Command(
                description, amount, type, category, parsedDate));
        return "Transação registrada: %s de R$ %s em %s (%s), id=%s"
                .formatted(saved.type(), saved.amount(), saved.date(), saved.category(), saved.id());
    }

    @Tool(description = "Lista as transações de uma categoria específica do orçamento.")
    public String listTransactionsByCategory(
            @ToolParam(description = "Categoria a consultar, ex: FOOD, TRANSPORT, HEALTH") String category) {

        List<Transaction> transactions = listByCategory.execute(category);
        if (transactions.isEmpty()) {
            return "Nenhuma transação encontrada na categoria " + category;
        }
        StringBuilder sb = new StringBuilder("Transações na categoria " + category + ":\n");
        for (Transaction t : transactions) {
            sb.append("- %s | %s | R$ %s | %s\n"
                    .formatted(t.date(), t.description(), t.amount(), t.type()));
        }
        return sb.toString();
    }

    @Tool(description = "Calcula o resumo de saldo (total de receitas, total de despesas e saldo) em um período. "
            + "Se as datas não forem informadas, considera do primeiro dia do mês atual até hoje.")
    public String getBalanceSummary(
            @ToolParam(description = "Data inicial no formato ISO yyyy-MM-dd.", required = false) String from,
            @ToolParam(description = "Data final no formato ISO yyyy-MM-dd.", required = false) String to) {

        LocalDate parsedFrom = (from == null || from.isBlank()) ? null : LocalDate.parse(from);
        LocalDate parsedTo = (to == null || to.isBlank()) ? null : LocalDate.parse(to);
        BalanceSummary s = getBalanceSummary.execute(parsedFrom, parsedTo);
        return ("Resumo de %s a %s: receitas R$ %s, despesas R$ %s, saldo R$ %s (%d transações).")
                .formatted(s.from(), s.to(), s.totalIncome(), s.totalExpense(), s.balance(), s.transactionCount());
    }
}
