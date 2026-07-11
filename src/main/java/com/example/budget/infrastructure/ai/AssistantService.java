package com.example.budget.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Orquestra a conversa com o LLM. Registra as ferramentas de orçamento e um
 * system prompt que orienta o modelo a usar Tool Calling em vez de inventar dados.
 */
@Service
public class AssistantService {

    private static final String SYSTEM_PROMPT = """
            Você é um assistente financeiro pessoal em português do Brasil.
            Ajude o usuário a registrar e consultar transações do orçamento dele.
            Use SEMPRE as ferramentas disponíveis para criar transações, listar por
            categoria ou calcular o saldo de um período — nunca invente valores.
            Responda de forma curta, clara e amigável, sempre em reais (R$).
            A data de hoje deve ser assumida quando o usuário não informar uma data.
            """;

    private final ChatClient chatClient;

    public AssistantService(ChatClient.Builder builder, BudgetTools budgetTools) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(budgetTools)
                .build();
    }

    /** Interpreta o texto do usuário, executa as ferramentas necessárias e devolve a resposta. */
    public String handle(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
