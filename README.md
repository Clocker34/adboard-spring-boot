# Adboard – сервис объявлений

## Тема проекта
Онлайн-доска объявлений, где пользователи публикуют объявления в разных категориях, могут отправлять сообщения владельцам и оставлять жалобы на нарушение правил.

## Основные сущности
- User – пользователь сервиса (username, email, дата регистрации).
- Category – категория объявлений (название, описание).
- Listing – объявление (заголовок, описание, цена, владелец, категория, статус).
- Message – сообщение, отправленное владельцу объявления.
- Report – жалоба на объявление (причина, статус, автор, связанное объявление).

## Операции сервиса

### CRUD
- Пользователи:
  - `GET /api/users`
  - `GET /api/users/{id}`
  - `POST /api/users`
  - `PUT /api/users/{id}`
  - `DELETE /api/users/{id}`

- Категории:
  - `GET /api/categories`
  - `GET /api/categories/{id}`
  - `POST /api/categories`
  - `PUT /api/categories/{id}`
  - `DELETE /api/categories/{id}`

- Объявления:
  - `GET /api/listings`
  - `GET /api/listings/{id}`
  - `POST /api/listings`
  - `PUT /api/listings/{id}`
  - `DELETE /api/listings/{id}`

### Бизнес-операции (задание 3)
1. Создать объявление от пользователя в категории  
   `POST /api/listings/user/{userId}/category/{categoryId}`

2. Отправить сообщение владельцу объявления  
   `POST /api/messages/listing/{listingId}/from/{senderId}`

3. Оставить жалобу на объявление  
   `POST /api/reports/listing/{listingId}/from/{reporterId}`

4. Взять жалобу в работу (скрыть объявление)  
   `POST /api/reports/{reportId}/start-review`

5. Завершить рассмотрение жалобы (подтвердить / отклонить)  
   `POST /api/reports/{reportId}/complete-review`

### Поиск объявлений
- Фильтрация объявлений по категории, владельцу, статусу и диапазону цен:  
  `GET /api/listings/search?categoryId=&ownerId=&status=&minPrice=&maxPrice=`


text

## Тестовые данные
- Category id=1: "Автомобили"  
- User id=1: "Иван Иванов"  
- Listing id=1: "Продаю Toyota Camry"
