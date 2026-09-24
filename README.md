# My Blog — бэкенд

[![Java](https://img.shields.io/badge/Java-21-0676BC)](https://www.java.com/)
[![Spring](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F)](https://spring.io/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/DB-H2%20in--memory-1E75B8)](#база-данных)

Бэкенд приложения-блога на **Java 21** и **Spring Boot 4.1.1**. Собирается исполняемым **JAR** (встроенный сервер внутри), разворачивается одной командой `java -jar`. Фронтенд (React) взаимодействует по REST: фронт — `http://localhost:80`, бэкенд — `http://localhost:8080`.

## Стек технологий

| Компонент | Версия |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Framework (Context, Web MVC, JDBC) | в составе Spring Boot 4.1.1 |
| Spring Data JDBC | в составе Spring Boot 4.1.1 |
| База данных | H2 (in-memory) |
| Система сборки | Maven (wrapper) |
| Тестирование | JUnit 6, Spring Test, Mockito, JsonPath |
| Прочее | Lombok, Jackson, Jakarta Servlet API 6.0 |

## Структура проекта

```
src/main/java/ru/yandex/practicum/
├── config/       # Java-конфигурация Spring (Web, Data)
├── controller/   # REST-контроллеры
├── service/      # бизнес-логика (интерфейсы + реализации)
├── dao/          # слой доступа к данным (интерфейсы + реализации)
├── dto/          # объекты передачи данных для запросов и ответов
├── exception/    # исключения и глобальный обработчик ошибок
└── model/        # сущности Post и Comment
src/main/resources/
├── schema.sql    # SQL-скрипт создания таблиц posts и comments
src/main/java/
└── BlogApplication.java   # точка входа Spring Boot (приложение-блог)
src/test/java/    # интеграционные и unit-тесты
```

## Начало работы

Требования:

- JDK 21.
- Docker Desktop (Windows/WSL2) либо собранный JAR.
- Свободные порты `8080` (бэкенд) и `80`/`8081` (фронтенд).

Быстрый старт:

```cmd
mvnw.cmd clean package
```

Запуск:

```cmd
java -jar target/my-blog-back-app-0.0.1-SNAPSHOT.jar
```

Бэкенд будет доступен на http://localhost:8080.

## Сборка

```cmd
mvnw.cmd clean package
```

Результат: `target/my-blog-back-app-0.0.1-SNAPSHOT.jar` — исполняемый JAR.

## Запуск тестов

```cmd
mvnw.cmd test
```

Покрытие: интеграционные тесты сервисов, DAO и MVC (`ApplicationTests`, `MvcIntegrationTest`), unit-тесты контроллеров (`PostControllerTest`, `CommentControllerTest`), тесты DAO (`PostDaoTest`, `CommentDaoTest`).

## Использование API

### Получение списка постов

```
GET /api/posts?search=Lalala&pageNumber=1&pageSize=5
```

Параметры: `search` — строка поиска, `pageNumber` — номер страницы, `pageSize` — размер страницы (все обязательные).

Ответ:

```json
{
  "posts": [
    {
      "id": 1,
      "title": "Название поста",
      "text": "Текст поста в формате Markdown...",
      "tags": ["tag_1", "tag_2"],
      "likesCount": 5,
      "commentsCount": 1
    }
  ],
  "hasPrev": false,
  "hasNext": false,
  "lastPage": 1
}
```

`posts` — список постов (текст обрезается до 128 символов с «…»), `hasPrev` — true, если есть предыдущая страница, `hasNext` — true, если есть следующая, `lastPage` — номер последней страницы.

### Получение поста

```
GET /api/posts/1
```

Ответ — один пост, текст не обрезается:

```json
{
  "id": 1,
  "title": "Название поста",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"],
  "likesCount": 5,
  "commentsCount": 1
}
```

### Создание поста

```
POST /api/posts
Content-Type: application/json
```

Тело:

```json
{ "title": "Название поста", "text": "Текст поста", "tags": ["tag_1", "tag_2"] }
```

`title`, `text`, `tags` — обязательные. Ответ — созданный пост с `id`, `likesCount: 0`, `commentsCount: 0`.

### Редактирование поста

```
PUT /api/posts/3
Content-Type: application/json
```

Тело:

```json
{ "id": 3, "title": "Название поста", "text": "Текст поста", "tags": [] }
```

`id`, `title`, `text`, `tags` — обязательные.

### Удаление поста

```
DELETE /api/posts/1
```

Удаляет пост и все его комментарии, возвращает `200 OK`.

### Лайки

```
POST /api/posts/1/likes
```

Инкремент числа лайков.

### Картинка поста

```
PUT /api/posts/1/image          # multipart/form-data, поле image
GET  /api/posts/1/image         # массив байт
```

### Комментарии

| Метод | Путь | Описание |
|---|---|---|
| `GET` | `/api/posts/1/comments` | список комментариев поста |
| `GET` | `/api/posts/1/comments/2` | один комментарий |
| `POST` | `/api/posts/1/comments` | создать комментарий (`{ "text": "...", "postId": 1 }`) |
| `PUT` | `/api/posts/1/comments/2` | изменить комментарий (`{ "id": 2, "text": "...", "postId": 1 }`) |
| `DELETE` | `/api/posts/1/comments/2` | удалить комментарий, `200 OK` |

Поля комментария: `id`, `text`, `postId` (обязательные).

### Правила поиска

- Строка поиска разбивается на слова по пробелам, пустые слова игнорируются.
- Слова, начинающиеся с `#`, считаются тегами и фильтруют посты по «И».
- Остальные слова склеиваются в подстроку и ищутся в названии поста.
- Фильтр по тегам и подстроке применяется одновременно («И»).

## База данных

Используется **H2 in-memory** (режим PostgreSQL). При старте автоматически выполняется `schema.sql`:

- `posts` — `id`, `title`, `text`, `likes_count`, `image`.
- `post_tags` — `post_id`, `tag` (нормализованные теги, `PRIMARY KEY (post_id, tag)`, FK на `posts` с `ON DELETE CASCADE`).
- `comments` — `id`, `text`, `post_id` (внешний ключ на `posts` с `ON DELETE CASCADE`).

Данные хранятся в памяти и стираются при перезапуске приложения.

## Мониторинг (Actuator)

Подключён Spring Boot Actuator. Доступные эндпоинты:

- `GET /actuator/health` — состояние приложения (`UP`/`DOWN`).
- `GET /actuator/info` — информация о сборке (версия, время).
- `GET /actuator/metrics` — метрики JVM и HTTP.

Включено в `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      probes:
        enabled: true
  info:
    build:
      enabled: true
```
