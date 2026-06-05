# URL Shortener

Backend-проект на Spring Boot для сокращения ссылок.

## Стек
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Docker / Docker Compose
- Maven

## Возможности
- создание коротких ссылок;
- редирект по short code;
- получение списка ссылок с пагинацией;
- просмотр статистики по ссылке;
- удаление ссылки;
- кеширование оригинального URL в Redis.

## Запуск через Docker
```bash
docker compose up -d db redis
```

## Локальный запуск
1. Подними PostgreSQL и Redis.
2. Проверь настройки в `src/main/resources/application.properties`.
3. Запусти:
```bash
mvn spring-boot:run
```

## API

### Создать ссылку
`POST /api/links`

```json
{
  "originalUrl": "https://example.com/some/very/long/link"
}
```

### Получить список
`GET /api/links?page=0&size=20`

### Получить статистику
`GET /api/links/{id}/stats`

### Удалить ссылку
`DELETE /api/links/{id}`

### Редирект
`GET /{shortCode}`

## Пример ответа
```json
{
  "id": "c3c3c23f-8e53-4b3a-a2d5-1f8b5b9d1f8a",
  "originalUrl": "https://example.com/some/very/long/link",
  "shortCode": "aB3kLm91",
  "shortUrl": "http://localhost:8080/aB3kLm91",
  "clicks": 0,
  "createdAt": "2026-06-05T12:00:00"
}
```

## Идея для улучшения
- добавить авторизацию;
- добавить историю кликов;
- добавить rate limiting;
- добавить Swagger/OpenAPI;
- добавить интеграционные тесты.
