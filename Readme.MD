# Spring Boot AI Chatbot

## Use Case

This project is a **Spring Boot chatbot that lets users upload their own files and ask questions about them**, using
Retrieval-Augmented Generation (RAG) with a local LLM served by [LM Studio](https://lmstudio.ai/).

Typical flow:

1. **Login** — Access is protected by Spring Security form login (`/login`); each request runs under an authenticated
   user.
2. **Simple UI** — A Thymeleaf-based chat page (`home.html`) lets a logged-in user upload files and chat with the
   assistant directly in the browser.

From the user's perspective, that's it — log in, upload a file, and start chatting. Under the hood, here's what actually
happens on each of those actions:

3. **Upload a document** — `POST /api/upload` accepts a file (PDF, DOCX, TXT, etc., up to 50MB). The file is parsed with
   Apache Tika, split into chunks, embedded (via the `text-embedding-embeddinggemma-300m-qat` model loaded in LM
   Studio),
   and stored in a **PGVector** vector store.
4. **Chat about the uploaded content** — `POST /api/ai/chat` sends the user's question to a
   `ChatClient` backed by the `google/gemma-4-e4b` model loaded in LM Studio. A `QuestionAnswerAdvisor` retrieves the
   most relevant chunks from the vector store (top 6 matches, similarity threshold 0.3) and injects them into the
   prompt, so answers are grounded in the user's own uploaded documents rather than the model's general knowledge.
5. **Conversation memory** — Spring AI session memory (`SessionAdvisors`) keeps a per-user rolling chat history (keyed
   by the authenticated user's name), so follow-up questions retain context. History is held in memory, so it is lost
   when the application restarts.

In short: it's a self-hosted *chat with your documents* assistant — useful for querying manuals, reports, or notes
without sending data to a third-party AI service, since both the LLM and the vector store run locally.

## Prerequisites

- Java 25 and Gradle
- Docker (for PostgreSQL with PgVector)
- [LM Studio](https://lmstudio.ai/) with the local server enabled

## Run application

### 1. Set up LM Studio

- Download the chat model `google/gemma-4-e4b` and the embedding model `text-embedding-embeddinggemma-300m-qat`
  (768 dimensions).
```shell
lms get google/gemma-4-e4b
lms get text-embedding-embeddinggemma-300m-qat
```
- In the **Developer** tab, load both models and start the server on port **1235**. Alternatively, enable
  Just-in-Time model loading.
- `src/main/resources/application.yaml` is configured as below. If your port or model identifiers differ, update them
  (`curl http://localhost:1235/v1/models` lists what LM Studio serves):

```yaml
spring:
  ai:
    openai:
      base-url: http://localhost:1235/v1
      api-key: lm-studio # any non-empty value; LM Studio ignores it
      chat:
        model: google/gemma-4-e4b
      embedding:
        model: text-embedding-embeddinggemma-300m-qat
```

> The embedding dimension must match `spring.ai.vectorstore.pgvector.dimensions` (768) and the `vector(768)` column in
> `pgvector-docker-compose.yaml`. If you use a model with a different dimension, update both. After switching embedding
> models, recreate the database (see Cleanup) and re-upload your documents, because vectors from different models are
> not compatible.

### 2. Start PostgreSQL with PgVector

Use the provided docker compose file.

```
docker compose -f pgvector-docker-compose.yaml up -d
```

### 3. Start the application

```
gradle clean bootRun
```

## Cleanup

- Stop Application

Press Ctrl+C to stop the application. Stop the PostgreSQL database using following command.

```
docker compose -f pgvector-docker-compose.yaml down -v
```

## Troubleshooting

- `400: No models loaded` — LM Studio has no model loaded. Load the chat and embedding models in the Developer tab, or
  enable Just-in-Time loading, and check that the model names in `application.yaml` match LM Studio's identifiers.

## Reference

- [AI Concepts](https://docs.spring.io/spring-ai/reference/concepts.html)
- [Spring AI](https://spring.io/projects/spring-ai)
- [Spring AI OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
- [Spring AI Session Memory](https://docs.spring.io/spring-ai/reference/api/session.html)
- [LM Studio Developer Docs](https://lmstudio.ai/docs/developer)
- [Spring AI by HowToDoInJava](https://howtodoinjava.com/spring-ai/)