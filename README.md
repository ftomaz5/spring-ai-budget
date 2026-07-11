# 💰 Spring AI Budget — Assistente Financeiro por Voz

API de orçamento pessoal ("budgeting") inteligente construída com **Spring Boot + Spring AI**.
O usuário fala (ou digita), a API entende a intenção com um LLM via **Tool Calling** e executa
casos de uso reais de domínio: registrar transações, listar por categoria e — a evolução deste
projeto — **calcular o resumo de saldo por período**.

Projeto final da trilha `05-spring-ai`.

---

## 🎯 O que o projeto faz

Fluxo completo ponta a ponta:

```
áudio  ──▶  Transcrição (Whisper)  ──▶  LLM + Tool Calling  ──▶  Use Case (domínio)  ──▶  Banco
                                                │
                                                └──▶  resposta em texto  ──▶  (opcional) Text-to-Speech ──▶ áudio
```

O modelo não inventa dados: ele é obrigado, via *system prompt*, a chamar as ferramentas do
domínio para criar ou consultar transações. Cada ferramenta é um caso de uso real da aplicação.

---

## 🧱 Arquitetura (DDD em 3 camadas)

```
com.example.budget
├── domain                 # regras de negócio puras, sem framework
│   ├── Transaction        # entidade central (invariantes no construtor)
│   ├── TransactionId      # value object
│   ├── TransactionType    # INCOME / EXPENSE (com sinônimos em PT)
│   ├── Category           # FOOD, TRANSPORT, HOUSING, ... (com sinônimos em PT)
│   ├── BalanceSummary     # value object do resumo de saldo  ← evolução
│   └── TransactionRepository   # porta de saída
│
├── application            # casos de uso (orquestração)
│   ├── PersistTransactionUseCase          # registra transação + validação
│   ├── ListTransactionsByCategoryUseCase  # consulta por categoria
│   └── GetBalanceSummaryUseCase           # resumo de saldo por período  ← evolução
│
└── infrastructure         # detalhes (framework, IO)
    ├── ai
    │   ├── BudgetTools          # métodos @Tool expostos ao LLM (Tool Calling)
    │   ├── AssistantService     # ChatClient + system prompt + registro das tools
    │   ├── TranscriptionService # áudio → texto (Whisper)
    │   └── SpeechService        # texto → áudio (TTS)
    ├── persistence
    │   ├── TransactionEntity, TransactionJpaRepository
    │   └── TransactionRepositoryAdapter   # implementa a porta com JPA
    └── web
        └── AssistantController  # endpoints REST (texto e voz)
```

A camada `domain` não conhece Spring nem JPA. A `infrastructure` implementa as portas.
Isso mantém as regras de negócio testáveis de forma isolada (ver testes).

---

## 🚀 A melhoria implementada — Resumo de saldo por período

A base do projeto trazia apenas *criar* e *listar por categoria*. A evolução adiciona uma
**nova ferramenta de Tool Calling**: `getBalanceSummary`, apoiada pelo caso de uso
`GetBalanceSummaryUseCase`.

O que ela entrega:

- Soma **receitas** e **despesas** dentro de um intervalo de datas e devolve o **saldo**.
- Se o usuário não informar o período, assume **do primeiro dia do mês atual até hoje**.
- Retorna também a **quantidade de transações** consideradas.
- Novo *value object* `BalanceSummary` mantém a regra `saldo = receitas − despesas` no domínio.

Junto vieram **validações de negócio** antes de persistir:

- valor da transação precisa ser **maior que zero**;
- **data não pode estar no futuro**;
- no resumo, **data inicial não pode ser posterior à final**.

Assim o usuário pode perguntar coisas como *"qual foi meu saldo em junho?"* ou *"quanto
gastei essa semana?"* e o LLM chama a ferramenta certa com o período correto.

---

## 🛠️ Tecnologias

| Camada          | Tecnologia                                   |
|-----------------|----------------------------------------------|
| Linguagem       | Java 21                                       |
| Framework       | Spring Boot 3.3                                |
| IA              | Spring AI 1.0.0-M3 (ChatClient, Tool Calling, Audio) |
| Modelos         | OpenAI — `gpt-4o-mini`, `whisper-1`, `tts-1`  |
| Persistência    | Spring Data JPA + PostgreSQL                   |
| Build           | Gradle                                        |
| Banco (dev)     | Docker Compose (Postgres 16)                   |
| Testes          | JUnit 5 (banco em memória via H2)              |

---

## ▶️ Como rodar

### Pré-requisitos
- Java 21, Docker e uma `OPENAI_API_KEY`.

### Passos

```bash
# 1. Suba o banco
docker compose up -d

# 2. Configure a chave da OpenAI
cp .env.example .env
export OPENAI_API_KEY="sk-sua-chave"   # ou preencha o .env e exporte

# 3. Rode a aplicação
./gradlew bootRun
```

A API sobe em `http://localhost:8080`.

---

## 📡 Endpoints

| Método | Rota                              | Descrição                                        |
|--------|-----------------------------------|--------------------------------------------------|
| POST   | `/api/assistant/chat`             | Entrada por **texto**, resposta em texto         |
| POST   | `/api/assistant/voice`            | Entrada por **áudio**, resposta em texto         |
| POST   | `/api/assistant/voice-to-voice`   | Entrada por **áudio**, resposta em **áudio (MP3)** |

### Exemplos

Registrar uma despesa por texto:

```bash
curl -X POST http://localhost:8080/api/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Gastei 42,50 no almoço hoje"}'
```

Consultar o saldo (usa a ferramenta nova):

```bash
curl -X POST http://localhost:8080/api/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Qual foi meu saldo neste mês?"}'
```

Enviar um áudio:

```bash
curl -X POST http://localhost:8080/api/assistant/voice \
  -F "audio=@meu-audio.mp3"
```

---

## ✅ Como testar

```bash
./gradlew test
```

Os testes cobrem os fluxos principais com um repositório em memória (sem tocar no banco):

- **`PersistTransactionUseCaseTest`** — persistência válida, sinônimos em português
  (`receita`/`salario`), rejeição de valor zero/negativo e rejeição de data no futuro.
- **`GetBalanceSummaryUseCaseTest`** — cálculo correto de receitas/despesas/saldo, exclusão de
  transações fora do período, período invertido inválido e período sem transações.

---

## 📚 O que eu aprendi

- **Tool Calling como ponte entre linguagem natural e casos de uso**: expor um método `@Tool`
  bem descrito é o que permite ao LLM decidir *sozinho* qual caso de uso executar e com quais
  argumentos. A qualidade da `description` importa tanto quanto o código.
- **DDD mantém a IA como detalhe**: colocando as regras no `domain` e tratando Spring AI como
  infraestrutura, consegui testar toda a lógica de saldo e validação sem subir o modelo nem o
  banco — os testes rodam em milissegundos.
- **Validar antes de persistir** evita que o modelo, ao interpretar um áudio ambíguo, grave
  lixo no banco (valor negativo, data no futuro).
- **O pipeline de voz é composição de peças simples**: transcrição, chat e síntese são serviços
  independentes; o controller apenas os encadeia.

---

## 📄 Licença

Projeto educacional — sinta-se livre para usar como referência.
