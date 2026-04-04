# Adboard — учебный проект (Spring Boot)

Кратко о репозитории по этапам **заданий 1–3**: подготовка проекта, REST API, база данных и бизнес-логика.

---

## Задание 1. Подготовка репозитория

### Что сделано

- **Java-проект на Spring Boot** версии **3.5.5** (см. `adboard/pom.xml`, родитель `spring-boot-starter-parent`).
- **Код опубликован на GitHub** — клонируйте репозиторий и открывайте каталог `adboard` как Maven-модуль.
- **Простые контроллеры** для проверки работы Spring MVC:
  - `GET /hello` — текстовый ответ (`HelloController`);
  - `GET /info` — JSON с названием сервиса и версией (`InfoController`).

### Запуск (после клонирования)

```text
cd adboard
mvnw.cmd spring-boot:run
```

На Linux/macOS: `./mvnw spring-boot:run`. Порт по умолчанию: **8081**; если он занят уже запущенным экземпляром — в `.env` или в среде задайте **`SERVER_PORT=8082`** (см. `application.properties`).

**PostgreSQL в Docker на `localhost:5432`** (как контейнер `postgres` в Docker Desktop): скопируйте `.env.example` в `.env`, укажите **`SPRING_DATASOURCE_PASSWORD`**, оставьте **`ADBOARD_SKIP_DOCKER_COMPOSE=1`**, затем из корня репозитория: `.\scripts\run-dev.ps1` — второй контейнер Postgres из `docker-compose.yml` не поднимается.

---

## Задание 2. Работа с REST

### Тема

**Онлайн-доска объявлений (adboard):** пользователи, категории, объявления, переписка по объявлению, жалобы на объявления.

### Сущности и CRUD

Для **каждой** сущности реализованы методы контроллера: **создание**, **получение** (список и по id), **изменение**, **удаление**.

| Сущность | Базовый путь API |
|----------|------------------|
| Пользователь | `/api/users` |
| Категория | `/api/categories` |
| Объявление | `/api/listings` |
| Сообщение | `/api/messages` |
| Жалоба | `/api/reports` |

Дополнительно к заданию 1: эндпоинты `GET /hello`, `GET /info`.

### Что сервис может развивать дальше (идеи)

- Аутентификация и роли (владелец объявления / модератор).
- Поиск и фильтры объявлений (категория, цена, текст).
- Загрузка изображений к объявлениям.
- Уведомления о новых сообщениях.
- Очередь жалоб для модерации и аналитика.

### Postman

Коллекция с парами запросов (CRUD и сценарии): файл **`adboard/adboard-postman-collection.json`**.  
Импорт: Postman → **Import** → выбрать JSON. Переменная **`baseUrl`**: `http://localhost:8081`; при необходимости задайте id сущностей (`userId`, `categoryId`, и т.д.).

---

## Задание 3. Базы данных и бизнес-операции

### База данных

- **Реляционная СУБД: PostgreSQL** (драйвер в `pom.xml`, настройки в `adboard/src/main/resources/application.properties`).
- **Чувствительные данные** (пароль и при необходимости URL/логин) для продакшена задавайте через **переменные окружения**, а не храните в открытом виде в репозитории:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`

### Таблицы, связи, ограничения

Таблицы соответствуют JPA-сущностям в `adboard/src/main/java/ru/rkjrth/adboard/entity/`. Схема обновляется Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

| Сущность | Связи | Примеры ограничений |
|----------|--------|---------------------|
| **User** | Владелец объявлений, отправитель/получатель сообщений, автор жалоб | Уникальный **email** |
| **Category** | Категория у многих объявлений | Уникальное **name** |
| **Listing** | Принадлежит User и Category | FK на `owner_id`, `category_id`; статусы и даты |
| **Message** | Отправитель, получатель (User), объявление (Listing) | FK на пользователей и объявление |
| **Report** | Автор (User), объявление (Listing) | FK на автора и объявление |

### Тестовые данные

Набор тестовых записей удобно создать **через сценарий в Postman** (создать пользователей → категорию → объявление → сообщения и жалобы). При необходимости для проверки можно добавить SQL-скрипт с `INSERT` или использовать `data.sql` в профиле Spring — в репозитории основной путь проверки — API и коллекция.

### CRUD и БД

Контроллеры из задания 2 **читают и пишут в PostgreSQL** через сервисы и **Spring Data JPA** (репозитории в `repository/`), а не в память.

### Бизнес-операции (не меньше 5)

Реализованы **6** операций с предметной логикой. Операция **поиска/выборки** учитывается, если затрагивает **не менее двух сущностей** (например, сообщения по объявлению).

| Операция | HTTP |
|----------|------|
| Публикация объявления | `POST /api/listings/{id}/publish` |
| Закрытие объявления | `POST /api/listings/{id}/close` |
| Сообщение по объявлению (связь пользователей и лота) | `POST /api/messages?senderId=...&receiverId=...&listingId=...` |
| Переписка по объявлению | `GET /api/messages/listing/{listingId}` |
| Жалоба в рассмотрение | `POST /api/reports/{id}/in-review` |
| Решение жалобы и закрытие объявления **в одной транзакции** | `POST /api/reports/{id}/resolve-and-close-listing` |

Реализация: `ListingService`, `MessageService`, `ReportService` и соответствующие контроллеры.

### Postman (задание 3)

В коллекции **`adboard/adboard-postman-collection.json`** есть **все** запросы: полный CRUD по каждой сущности, примеры тел **JSON**, переменные (`baseUrl`, id), а также бизнес-операции.  
Сценарий «создать данные → выполнить операцию → увидеть результат» описан ниже.

### Сценарий проверки

1. Создать пользователей — `POST /api/users`  
2. Создать категорию — `POST /api/categories`  
3. Создать объявление — `POST /api/listings?ownerId=...&categoryId=...`  
4. Опубликовать — `POST /api/listings/{id}/publish`  
5. Отправить сообщение — `POST /api/messages?...`  
6. Посмотреть переписку — `GET /api/messages/listing/{listingId}`  
7. Подать жалобу — `POST /api/reports?...`  
8. В рассмотрение — `POST /api/reports/{id}/in-review`  
9. Решить жалобу и закрыть лот — `POST /api/reports/{id}/resolve-and-close-listing`, затем `GET` по объявлению и жалобе.

---

## Коротко: тема, сущности, возможности сервиса

| | |
|--|--|
| **Тема** | Онлайн-доска объявлений |
| **Сущности** | User, Category, Listing, Message, Report |
| **Операции** | Полный REST CRUD по пяти сущностям; публикация и закрытие объявления; переписка и просмотр диалога по объявлению; модерация жалоб, в том числе атомарное закрытие жалобы и связанного объявления |

---

## Задание 4. Базовая безопасность API

### Spring Security

- Подключён **`spring-boot-starter-security`**: **HTTP Basic Auth** и **CSRF** (`CookieCsrfTokenRepository`, заголовок `X-XSRF-TOKEN`, cookie `XSRF-TOKEN`).
- Публично без авторизации: `GET /`, `/hello`, `/info`, **`GET /api/csrf`**, **`POST /api/auth/register`**.
- Остальные **`/api/**`** — только для аутентифицированных пользователей.
- **`/api/users/**`** и **`/api/reports/**`** — только роль **ADMIN**.

### Пользователи и пароли

- Учётные записи хранятся в **PostgreSQL** (сущность `User`: `username`, `passwordHash` BCrypt, роль `USER` / `ADMIN`).
- Загрузка для Basic Auth: класс **`DatabaseUserDetailsService`** (`UserDetailsService`).
- **Регистрация:** `POST /api/auth/register` с телом JSON: `username`, `email`, `password`, `name`. Пароль: не короче 8 символов, хотя бы одна цифра и спецсимвол из `!@#$%^&*`.
- **Первый администратор** создаётся только из переменных окружения (не из кода и не из SQL в репозитории): `ADBOARD_BOOTSTRAP_ADMIN_USERNAME`, `ADBOARD_BOOTSTRAP_ADMIN_PASSWORD`.
- Создание/изменение пользователей администратором: `POST/PUT /api/users` с телом **`AdminUserRequest`** (поля `username`, `name`, `email`, `password`, `role`).

### Перед запросами с изменением данных

1. Выполнить **`GET /api/csrf`** (в том же клиенте сохранятся cookie сессии и `XSRF-TOKEN`).
2. Для `POST`/`PUT`/`DELETE` передать заголовок **`X-XSRF-TOKEN`** со значением из cookie `XSRF-TOKEN`.
3. Для защищённых эндпоинтов — **Basic Auth** (логин = `username` в БД, пароль — как при регистрации или bootstrap).

### Тесты

Профиль **`test`** использует встроенную **H2** (`src/test/resources/application-test.properties`), чтобы не требовать PostgreSQL при `mvn test`.

---

*Java 17, Spring Boot 3.5.5, порт по умолчанию 8081.*
