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

## Запуск на macOS (рекомендуемый способ: Docker)

### 1) Установить зависимости
```bash
# Homebrew (если не установлен)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Docker Desktop + Git
brew install --cask docker
brew install git
```

Открой Docker Desktop вручную и дождись статуса **Engine running**.

### 2) Клонировать проект и перейти в папку
```bash
git clone <YOUR_REPO_URL>
cd Kotlin_Peresdacha
```

### 3) Поднять все сервисы
```bash
docker compose up --build
```

После старта будут доступны:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger`
- RabbitMQ UI: `http://localhost:15672` (guest/guest)

### 4) Остановить сервисы
```bash
docker compose down
```

Если нужно удалить тома и начать «с нуля»:
```bash
docker compose down -v
```

---

## Локальный запуск на macOS без Docker

> Нужны PostgreSQL, Redis, RabbitMQ и Java 21.

### 1) Установить Java 21 и сервисы
```bash
brew install --cask temurin@21
brew install postgresql@16 redis rabbitmq
```

### 2) Запустить сервисы
```bash
brew services start postgresql@16
brew services start redis
brew services start rabbitmq
```

### 3) Создать БД
```bash
createdb app
```

### 4) Запустить backend
```bash
export DB_URL=jdbc:postgresql://localhost:5432/app
export DB_USER=$(whoami)
export DB_PASSWORD=
export REDIS_URL=redis://localhost:6379
export RABBIT_HOST=localhost
export JWT_SECRET=super-secret

# если есть wrapper:
./gradlew run
# либо системный gradle:
gradle run
```

### 5) Запустить worker (в отдельном терминале)
```bash
export APP_MODE=worker
export RABBIT_HOST=localhost
./gradlew run
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
