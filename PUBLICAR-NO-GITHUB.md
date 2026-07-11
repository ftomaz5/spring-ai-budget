# Como publicar este projeto no seu GitHub

O código já está pronto. Faltam dois passos que precisam ser feitos por você
(criar o repositório e dar push, pois envolvem a sua conta).

## 1. Gerar o Gradle Wrapper (uma vez)

Este pacote inclui os arquivos do wrapper, mas o binário `gradle-wrapper.jar`
precisa ser gerado na sua máquina (tenha o Gradle 8.10+ instalado):

```bash
cd spring-ai-budget
gradle wrapper --gradle-version 8.10.2
```

> Alternativa: ao abrir o projeto no IntelliJ IDEA, ele gera o wrapper automaticamente.

## 2. Publicar no GitHub

O repositório já foi criado em: **https://github.com/ftomaz5/spring-ai-budget**

Descompacte o `spring-ai-budget.zip`, abra um terminal **dentro** da pasta
`spring-ai-budget` e rode:

```bash
git init
git add .
git commit -m "feat: assistente financeiro por voz com Spring AI + Tool Calling (evolução: resumo de saldo por período)"
git branch -M main
git remote add origin https://github.com/ftomaz5/spring-ai-budget.git
git push -u origin main
```

Se o Git pedir login, use seu usuário do GitHub e um **Personal Access Token**
(Settings → Developer settings → Tokens) no lugar da senha.

Pronto — o projeto estará no seu GitHub com o README na página inicial.
