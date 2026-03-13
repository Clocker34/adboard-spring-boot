# Adboard – сервис объявлений

## Тема проекта

Онлайн-доска объявлений, где пользователи публикуют объявления в разных категориях, могут отправлять сообщения владельцам и оставлять жалобы на нарушение правил.

## Основные сущности

- **User** – пользователь сервиса (username, email, дата регистрации, пароль).
- **Category** – категория объявлений (название, описание).
- **Listing** – объявление (заголовок, описание, цена, владелец, категория, статус).
- **Message** – сообщение, отправленное владельцу объявления.
- **Report** – жалоба на объявление (причина, статус, автор, связанное объявление).

---

## Операции сервиса

### CRUD

**Пользователи**

- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

**Категории**

- `GET /api/categories`
- `GET /api/categories/{id}`
- `POST /api/categories`
- `PUT /api/categories/{id}`
- `DELETE /api/categories/{id}`

**Объявления**

- `GET /api/listings`
- `GET /api/listings/{id}`
- `POST /api/listings`
- `PUT /api/listings/{id}`
- `DELETE /api/listings/{id}`

---

### Бизнес‑операции (задание 3)

1. **Создать объявление от пользователя в категории**  
   `POST /api/listings/user/{userId}/category/{categoryId}`

2. **Отправить сообщение владельцу объявления**  
   `POST /api/messages/listing/{listingId}/from/{senderId}`

3. **Оставить жалобу на объявление**  
   `POST /api/reports/listing/{listingId}/from/{reporterId}`

4. **Взять жалобу в работу (скрыть объявление)**  
   `POST /api/reports/{reportId}/start-review`

5. **Завершить рассмотрение жалобы (подтвердить / отклонить)**  
   `POST /api/reports/{reportId}/complete-review`

---

### Поиск объявлений

Фильтрация объявлений по категории, владельцу, статусу и диапазону цен:

- `GET /api/listings/search?categoryId=&ownerId=&status=&minPrice=&maxPrice=`

---

## Безопасность (задание 4)

Сервис использует Spring Security и Basic Auth.

### Аутентификация

- Схема: **HTTP Basic Auth**.
- Предопределённые пользователи (in‑memory):
  - **ADMIN**:  
    - логин: `admin`  
    - пароль: `adminpass`
  - **USER**:  
    - логин: `student`  
    - пароль: `password`

### Авторизация и роли

- Роль **ADMIN**:
  - имеет доступ ко всем эндпоинтам сервиса;
  - может управлять пользователями (`/api/users/**`);
  - может работать с жалобами (`/api/reports/**`).

- Роль **USER**:
  - имеет доступ к пользовательским операциям (объявления, сообщения, поиск);
  - не имеет доступа к административным эндпоинтам `/api/users/**` и `/api/reports/**`.

Правила безопасности (упрощённо):

- `POST /api/auth/register` – доступен всем (без авторизации).
- `/api/users/**`, `/api/reports/**` – только роль ADMIN.
- Остальные эндпоинты `/api/**` – любая аутентифицированная роль (USER или ADMIN).

### Регистрация пользователя

Регистрация нового пользователя доступна без авторизации.

- `POST /api/auth/register`

Пример тела запроса:

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "Qwerty1!"
}
Пароль должен быть:

не короче 8 символов;

содержать хотя бы одну цифру;

содержать хотя бы один спецсимвол из набора !@#$%^&*.

Тестовые данные
Примеры начальных данных в БД:

Category id = 1: «Автомобили»

User id = 1: «Иван Иванов»

Listing id = 1: «Продаю Toyota Camry»
