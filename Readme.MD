# Spring Boot AI Chatbot

## Use Case

This project is a **Spring Boot chatbot that lets users upload their own files and ask questions about them**, using
Retrieval-Augmented Generation (RAG) with a local Ollama LLM.

Typical flow:

1. **Login** — Access is protected by Spring Security form login (`/login`); each request runs under an authenticated
   user.
2. **Simple UI** — A Thymeleaf-based chat page (`home.html`) lets a logged-in user upload files and chat with the
   assistant directly in the browser.

From the user's perspective, that's it — log in, upload a file, and start chatting. Under the hood, here's what actually
happens on each of those actions:

3. **Upload a document** — `POST /api/upload` accepts a file (PDF, DOCX, TXT, etc., up to 50MB). The file is parsed with
   Apache Tika, split into chunks, embedded (via the `nomic-embed-text`
   Ollama model), and stored in a **PGVector** vector store.
4. **Chat about the uploaded content** — `POST /api/ai/chat` sends the user's question to a
   `ChatClient` backed by the `gemma4` Ollama model. A `QuestionAnswerAdvisor` retrieves the most relevant chunks from
   the vector store (top 6 matches, similarity threshold 0.3) and injects them into the prompt, so answers are grounded
   in the user's own uploaded documents rather than the model's general knowledge.
5. **Conversation memory** — A `MessageChatMemoryAdvisor` keeps a per-user rolling chat history (keyed by the
   authenticated principal), so follow-up questions retain context.

In short: it's a self-hosted *chat with your documents* assistant — useful for querying manuals, reports, or notes
without sending data to a third-party AI service, since both the LLM and the vector store run locally.

## Run application

- Install [Ollama](https://docs.ollama.com/quickstart) in development environment and pull `gemma4` model.

- Start PostgreSQL Server with PgVector, Use provided docker compose file.

```
docker compose -f pgvector-docker-compose.yaml up -d
```

```
gradle clean bootRun
```

## Cleanup

- Stop Application

Press Ctrl+C to stop the application. Stop the PostgreSQL database using following command.

```
docker compose -f pgvector-docker-compose.yaml down -v
```

## Reference

- [AI Concepts](https://docs.spring.io/spring-ai/reference/concepts.html)
- [Spring AI](https://spring.io/projects/spring-ai)
- [Spring AI Ollama Chat](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Spring AI by HowToDoInJava](https://howtodoinjava.com/spring-ai/)