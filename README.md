# Доска объявлений - Задание 2

## Тема
Доска объявлений с возможностью размещать объявления по категориям, обмениваться сообщениями и оставлять жалобы.

## Основные сущности
- **Category** - категории объявлений (id, name, description)
- **User** - пользователи (id, name, email)  
- **Listing** - объявления (id, title, description, price, authorId, categoryId, status)
- **Message** - сообщения (id, text, senderId, recipientId, listingId)
- **Report** - жалобы (id, reason, reporterId, targetListingId)

## REST API (полный CRUD)
POST/GET/PUT/DELETE /api/categories
POST/GET/PUT/DELETE /api/users
POST/GET/PUT/DELETE /api/listings
POST/GET/PUT/DELETE /api/messages
POST/GET/PUT/DELETE /api/reports

text

## Тестовые данные
- Category id=1: "Автомобили"  
- User id=1: "Иван Иванов"  
- Listing id=1: "Продаю Toyota Camry"
