# Kotlin Peresdacha Backend

Ktor backend для магазина с JWT, PostgreSQL (Exposed + Flyway), Redis cache, RabbitMQ, Swagger, тестами и Docker.

## Реализовано
- Аутентификация: `POST /auth/register`, `POST /auth/login`
- Пользовательские маршруты: `GET /products`, `GET /products/{id}`, `POST /orders`, `GET /orders`, `DELETE /orders/{id}`
- Админ-маршруты: `POST /products`, `PUT /products/{id}`, `DELETE /products/{id}`, `GET /stats/orders`
- Миграции Flyway (users, products, orders, order_items, audit_logs)
- Асинхронный worker для RabbitMQ (лог + fake email)
- Swagger: `http://localhost:8080/swagger`

---

## Запуск (Docker)

```bash
git clone <YOUR_REPO_URL>
cd Kotlin_Peresdacha
docker compose up --build
```

После старта будут доступны:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger`
- RabbitMQ UI: `http://localhost:15672` (guest/guest)

Остановка:
```bash
docker compose down
```

Полный сброс (с удалением томов):
```bash
docker compose down -v
```

---

## Тесты
```bash
./gradlew test
```

## CI
GitHub Actions workflow: `.github/workflows/ci.yml`

## Деплой
Подготовлен для Railway/Render/DigitalOcean (Dockerfile + переменные окружения).

Пример env:
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `REDIS_URL`
- `RABBIT_HOST`
- `JWT_SECRET`
