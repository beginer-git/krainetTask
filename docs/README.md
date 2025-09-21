# Тестовое задание: AUTH & NOTIFICATION сервисы

## Описание проекта

Проект состоит из двух микросервисов:

1. **auth-service** — авторизация, аутентификация, CRUD для работы с пользователями.  
2. **notification-service** — отправка уведомлений на почту при изменениях в пользователях.  

Дополнительно используются:  
- **MySQL** — база данных  
- **RabbitMQ** — брокер сообщений  
- **MailHog** — сервис для просмотра писем

---

## Функциональные требования

### AUTH-сервис

- CRUD для пользователей (создать, посмотреть, удалить)  
- Поля модели пользователя:
  - `username`
  - `password`
  - `email`
  - `firstName`
  - `lastName`
  - `role` (например, USER / ADMIN)  
- Разделение на роли:
  - **USER** — может работать только со своим аккаунтом  
  - **ADMIN** — может управлять любым аккаунтом  

### NOTIFICATION-сервис

- Отправка уведомлений на почту при создании, изменении или удалении пользователей  
- Сообщения приходят всем пользователям с ролью **ADMIN**  

**Пример письма:**

Тема: Создан/Изменен/Удален пользователь {username}
Текст: Создан/Изменен/Удален пользователь с именем - {username}, паролем - {password} и почтой - {email}.

---

## Сборка и запуск проекта

### Сборка микросервисов:

cd auth-service
mvn clean package
cd notification-service
mvn clean package

### Запуск всей системы через Docker Compose:

docker-compose up --build

### Открыть интерактивную консоль MySQL внутри контейнера:

docker exec -it krainettask-auth-db-1 mysql -u root_test -ppassword_test

### Остановить контейнеры:

docker-compose down -v

### Доступные сервисы:

MailHog: http://localhost:8025 <br>
RabbitMQ: http://localhost:15672 <br>
Swagger: http://localhost:8080/swagger-ui/index.html <br>

---

## Пример работы в http-clinet:

запрос:

POST http://localhost:8080/api/auth/register <br>
Content-Type: application/json <br>

{ <br>
  "username": "john_doe", <br>
  "password": "JohnDoe2025!", <br>
  "email": "john.doe@example.com", <br>
  "firstName": "John", <br>
  "lastName": "Doe", <br>
  "role": "ADMIN" <br>
} <br>

ответ: 

{ <br>
  "id": 4, <br>
  "username": "john_doe", <br>
  "email": "john.doe@example.com", <br>
  "firstName": "John", <br>
  "lastName": "Doe", <br>
  "role": "ADMIN" <br>
} <br>

запрос:

POST http://localhost:8080/api/auth/login <br>
Content-Type: application/json <br>

{ <br>
  "username": "john_doe", <br>
  "password": "JohnDoe2025!" <br>
} <br>

ответ:

eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1ODQyOTU3MCwiZXhwIjoxNzU4NDMzMTcwfQ.q0d78knk3MW_bdRiYYdV3CvX4IoG33de7Js0SPMVgypgFSAgMoDMqodqOq72btW- <br>

запрос:

GET http://localhost:8080/api/users <br>
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1ODQyOTU3MCwiZXhwIjoxNzU4NDMzMTcwfQ.q0d78knk3MW_bdRiYYdV3CvX4IoG33de7Js0SPMVgypgFSAgMoDMqodqOq72btW- <br>

ответ:

[ <br>
  { <br>
    "id": 1, <br>
    "username": "maria", <br>
    "email": "maria.smith@example.com", <br>
    "firstName": "Maria", <br>
    "lastName": "Smith", <br>
    "role": "USER" <br>
  }, <br>
  { <br>
    "id": 2, <br>
    "username": "takashi", <br>
    "email": "takashi.tanaka@example.com", <br>
    "firstName": "Takashi", <br>
    "lastName": "Tanaka", <br>
    "role": "USER" <br>
  }, <br>
  { <br>
    "id": 3, <br>
    "username": "sophie", <br>
    "email": "sophie.martin@example.com", <br>
    "firstName": "Sophie", <br>
    "lastName": "Martin", <br>
    "role": "ADMIN" <br>
  }, <br>
  { <br>
    "id": 4, <br>
    "username": "john_doe", <br>
    "email": "john.doe@example.com", <br>
    "firstName": "John", <br>
    "lastName": "Doe", <br>
    "role": "ADMIN" <br>
  } <br>
] <br>

запрос:

GET http://localhost:8080/api/users/1 <br>
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1ODQyOTU3MCwiZXhwIjoxNzU4NDMzMTcwfQ.q0d78knk3MW_bdRiYYdV3CvX4IoG33de7Js0SPMVgypgFSAgMoDMqodqOq72btW- <br>

ответ:

{ <br>
  "id": 1, <br>
  "username": "maria", <br>
  "email": "maria.smith@example.com", <br>
  "firstName": "Maria", <br>
  "lastName": "Smith", <br>
  "role": "USER" <br>
} <br>

запрос:

PUT http://localhost:8080/api/users/1 <br>
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1ODQyOTU3MCwiZXhwIjoxNzU4NDMzMTcwfQ.q0d78knk3MW_bdRiYYdV3CvX4IoG33de7Js0SPMVgypgFSAgMoDMqodqOq72btW- <br>
Content-Type: application/json <br>

{ <br>
  "password": "Alice2025!", <br>
  "email": "alice.wonder@example.com", <br>
  "firstName": "Alice", <br>
  "lastName": "Wonder" <br>
} <br>

ответ:

{ <br>
  "id": 1, <br>
  "username": "maria", <br>
  "email": "alice.wonder@example.com", <br>
  "firstName": "Alice", <br>
  "lastName": "Wonder", <br>
  "role": "USER" <br>
} <br>

запрос:

DELETE http://localhost:8080/api/users/2 <br>
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1ODQyOTU3MCwiZXhwIjoxNzU4NDMzMTcwfQ.q0d78knk3MW_bdRiYYdV3CvX4IoG33de7Js0SPMVgypgFSAgMoDMqodqOq72btW- <br>

ответ:

HTTP/1.1 200 <br>

запрос:

GET http://localhost:8080/api/users <br>
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1ODQyOTU3MCwiZXhwIjoxNzU4NDMzMTcwfQ.q0d78knk3MW_bdRiYYdV3CvX4IoG33de7Js0SPMVgypgFSAgMoDMqodqOq72btW- <br>

ответ:

[ <br>
  { <br>
    "id": 1, <br>
    "username": "maria", <br>
    "email": "alice.wonder@example.com", <br>
    "firstName": "Alice", <br>
    "lastName": "Wonder", v
    "role": "USER" <br>
  }, <br>
  { <br>
    "id": 3, <br>
    "username": "sophie", <br>
    "email": "sophie.martin@example.com", <br>
    "firstName": "Sophie", <br>
    "lastName": "Martin", <br>
    "role": "ADMIN" <br>
  }, <br>
  { <br>
    "id": 4, <br>
    "username": "john_doe", <br>
    "email": "john.doe@example.com", <br>
    "firstName": "John", <br>
    "lastName": "Doe", <br>
    "role": "ADMIN" <br>
  } <br>
] <br>