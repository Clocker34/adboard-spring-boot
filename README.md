# Задание 3. Adboard — база данных и бизнес-операции

## Тема проекта

**Онлайн-доска объявлений:** пользователи публикуют объявления в категориях, обмениваются сообщениями по объявлению, пользователи могут подавать жалобы на объявления. Данные хранятся в **PostgreSQL**, доступ — через **Spring Data JPA** (не в памяти).

## Основные сущности

| Сущность | Назначение | Связи и ограничения |
|----------|------------|---------------------|
| **User** | Пользователь | Уникальный **email**. One-to-many к объявлениям (владелец), сообщениям (отправитель/получатель), жалобам (автор). |
| **Category** | Категория объявлений | Уникальное **name**. One-to-many к объявлениям. |
| **Listing** | Объявление | Many-to-one к **User** (owner), **Category**; статусы `DRAFT` / `PUBLISHED` / `CLOSED`, даты создания/обновления/закрытия. |
| **Message** | Сообщение в контексте объявления | Many-to-one к **User** (sender, receiver) и **Listing**. |
| **Report** | Жалоба на объявление | Many-to-one к **User** (author) и **Listing**; статусы `NEW` / `IN_REVIEW` / `RESOLVED` / `REJECTED`. |

Схема таблиц создаётся Hibernate (`spring.jpa.hibernate.ddl-auto=update`) на основе сущностей в `adboard/src/main/java/ru/rkjrth/adboard/entity/`.

## База данных и конфигурация

- **СУБД:** PostgreSQL.
- Параметры подключения задаются в `adboard/src/main/resources/application.properties` (URL, пользователь, пароль).

Чувствительные значения для продакшена или сдачи лучше не фиксировать в репозитории: переопределите через **переменные окружения** (Spring Boot подхватывает их автоматически):

- `SPRING_DATASOURCE_URL` — например `jdbc:postgresql://localhost:5432/adboard`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

**БД в Docker (пример):** контейнер с Postgres должен слушать тот же хост/порт, что и в JDBC URL. Просмотр таблиц:

```bash
docker exec -it <имя_контейнера> psql -U <postgres_user> -d adboard -c "\dt"
```

## CRUD по сущностям

Контроллеры читают и пишут в БД через сервисы и репозитории.

| Ресурс | Базовый путь | Примечание |
|--------|--------------|------------|
| Пользователи | `GET/POST/PUT/DELETE /api/users` | Тело: `name`, `email` |
| Категории | `GET/POST/PUT/DELETE /api/categories` | Тело: `name`, `description` |
| Объявления | `GET/POST/PUT/DELETE /api/listings` | Создание: query `ownerId`, `categoryId` + тело объявления |
| Сообщения | `GET/POST/PUT/DELETE /api/messages` | Создание: query `senderId`, `receiverId`, `listingId` + тело `text` |
| Жалобы | `GET/POST/PUT/DELETE /api/reports` | Создание: query `authorId`, `listingId` + тело причины/статуса |

Служебные эндпоинты: `GET /`, `/hello`, `/info`.

## Бизнес-операции (не CRUD)

Реализовано **6** операций с предметной логикой (требование — не меньше 5). Места в коде: `ListingController` + `ListingService`, `MessageController` + `MessageService`, `ReportController` + `ReportService`.

| # | Операция | HTTP | Кратко |
|---|----------|------|--------|
| 1 | Публикация объявления | `POST /api/listings/{id}/publish` | Статус объявления → `PUBLISHED`. |
| 2 | Закрытие объявления | `POST /api/listings/{id}/close` | Статус → `CLOSED`, заполняется `closedAt`. |
| 3 | Сообщение по объявлению | `POST /api/messages?...` | Создание сообщения с привязкой к отправителю, получателю и объявлению. |
| 4 | Переписка по объявлению | `GET /api/messages/listing/{listingId}` | Список сообщений по данному объявлению (несколько сущностей в выборке). |
| 5 | Жалоба в рассмотрение | `POST /api/reports/{id}/in-review` | Статус жалобы → `IN_REVIEW`. |
| 6 | Решение жалобы и закрытие лота | `POST /api/reports/{id}/resolve-and-close-listing` | Жалоба → `RESOLVED`, связанное объявление закрывается; **одна транзакция** на две сущности. |

Реализация в коде: `ListingService`, `MessageService`, `ReportService` и соответствующие REST-контроллеры в `adboard/src/main/java/ru/rkjrth/adboard/`.

## Коллекция запросов (Postman)

Файл: **`adboard/adboard-postman-collection.json`**

- Переменная `baseUrl` (по умолчанию `http://localhost:8081`).
- Переменные для id: `userId`, `userId2`, `categoryId`, `listingId`, `reportId`, `messageId` — заполняйте после создания сущностей.

Импорт: Postman → Import → выбрать этот JSON.

## Запуск приложения

```text
cd adboard
mvnw.cmd spring-boot:run
```

(На Linux/macOS: `./mvnw spring-boot:run`.)

Порт: **8081** (см. `application.properties`).

Перед запуском убедитесь, что PostgreSQL доступен по URL из конфигурации и база данных создана (например, `CREATE DATABASE adboard;`).

## Сценарий проверки (создать данные → операция → результат)

1. `POST /api/users` — создать двух пользователей (для переписки).
2. `POST /api/categories` — категория.
3. `POST /api/listings?ownerId=...&categoryId=...` — черновик объявления.
4. `POST /api/listings/{id}/publish` — объявление опубликовано.
5. `POST /api/messages?senderId=...&receiverId=...&listingId=...` — сообщение по объявлению.
6. `GET /api/messages/listing/{listingId}` — видна переписка.
7. `POST /api/reports?authorId=...&listingId=...` — жалоба.
8. `POST /api/reports/{id}/in-review` — жалоба в работе.
9. `POST /api/reports/{id}/resolve-and-close-listing` — жалоба решена, объявление закрыто; проверка: `GET /api/listings/{id}` и `GET /api/reports/{id}`.

---

*Кратко: сервис умеет полный CRUD по пяти сущностям, шесть бизнес-операций для жизненного цикла объявления, переписки и модерации жалоб, с транзакцией при совместном обновлении жалобы и объявления.*
