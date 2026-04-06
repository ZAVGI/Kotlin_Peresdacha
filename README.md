# Kotlin Peresdacha Backend

Ktor backend для магазина с JWT, PostgreSQL (Exposed + Flyway), Redis cache, RabbitMQ, Swagger, тестами и Docker.

## Реализовано
- Аутентификация: `POST /auth/register`, `POST /auth/login`
- Пользовательские маршруты: `GET /products`, `GET /products/{id}`, `POST /orders`, `GET /orders`, `DELETE /orders/{id}`
- Админ-маршруты: `POST /products`, `PUT /products/{id}`, `DELETE /products/{id}`, `GET /stats/orders`
- Миграции Flyway (users, products, orders, order_items, audit_logs)
- Асинхронный worker для RabbitMQ (лог + fake email)
- Swagger: `http://localhost:8080/swagger`

## Локальный запуск
```bash
docker compose up --build
```

## Запуск без Docker
```bash
./gradlew run
```

## Тесты
```bash
./gradlew test
```

## Деплой
Подготовлен для Railway/Render/DigitalOcean (Dockerfile + переменные окружения).

Пример env:
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `REDIS_URL`
- `RABBIT_HOST`
- `JWT_SECRET`
