# Spring Boot AI

## Run application

* Install [Ollama](https://docs.ollama.com/quickstart) in development environment and run `gemma4` model.

* Start PostgreSQL Server with PgVector

```shell
docker compose -f pgvector-docker-compose.yaml up -d
```

```shell
gradle clean bootRun
```

## Cleanup

* Stop Application

Press Ctrl+C to stop the application. Stop the PostgreSQL database using following command.

```shell
docker compose -f pgvector-docker-compose.yaml down -v
```

## Reference

- [AI Concepts](https://docs.spring.io/spring-ai/reference/concepts.html)
- [Spring AI](https://spring.io/projects/spring-ai)
- [Spring AI Ollama Chat](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Spring AI by HowToDoInJava](https://howtodoinjava.com/spring-ai/)