# My Blog — бэкенд

[![Java](https://img.shields.io/badge/Java-21-0676BC)](https://www.java.com/)
[![Spring](https://img.shields.io/badge/Spring%20Framework-6.1-6DB33F)](https://spring.io/)
[![Tomcat](https://img.shields.io/badge/Tomcat-10.1-F8DC75)](https://tomcat.apache.org/)
[![Database](https://img.shields.io/badge/DB-H2%20in--memory-1E75B8)](#база-данных)
[![Build](https://img.shields.io/badge/Build-Gradle-02303A)](https://gradle.org/)

Бэкенд приложения-блога, написанный на **Java 21** и **Spring Framework 6.1+**.
Собирается в WAR-архив и разворачивается в сервлет-контейнере **Tomcat 10.1+**.
Фронтенд (React) взаимодействует с бэкендом по REST: фронт — `http://localhost:80`, бэкенд — `http://localhost:8080`.

## Возможности

- Лента постов с поиском по названию и тегам и пагинацией.
- Создание, получение, редактирование и удаление постов.
- Лайки постов.
- Загрузка и получение картинки поста.
- CRUD-операции с комментариями (при удалении поста комментарии удаляются каскадно).

## Оглавление

- [Стек технологий](#стек-технологий)
- [Структура проекта](#структура-проекта)
- [Начало работы](#начало-работы)
- [Сборка](#сборка)
- [Запуск тестов](#запуск-тестов)
- [Деплой в сервлет-контейнер](#деплой-в-сервлет-контейнер)
- [Запуск фронтенда](#запуск-фронтенда)
- [Использование API](#использование-api)
- [База данных](#база-данных)

## Стек технологий

| Компонент | Версия |
|---|---|
| Java | 21 |
| Spring Framework (Context, Web MVC, JDBC) | 6.1+ |
| Spring Data JDBC | 3.2 |
| База данных | H2 (in-memory) |
| Сервлет-контейнер | Tomcat 10.1+ |
| Система сборки | Gradle (wrapper) |
| Тестирование | JUnit 5, Spring Test, Mockito, JsonPath |
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
src/main/webapp/WEB-INF/web.xml   # конфигурация DispatcherServlet и контекста Spring
src/test/java/    # интеграционные и unit-тесты
```

## Начало работы

Требования:

- JDK 21.
- Docker Desktop (Windows/WSL2) либо Tomcat 10.1+.
- Свободные порты `8080` (бэкенд) и `80` (фронтенд).

Быстрый старт:

```cmd
gradlew.bat clean build
```

Собранный WAR: `build/libs/my-blog-back-app-0.1.0.war`. Разверните его в Tomcat (см. [Деплой](#деплой-в-сервлет-контейнер)) — бэкенд будет доступен на http://localhost:8080.

## Сборка

```cmd
gradlew.bat clean build
```

Результат: `build/libs/my-blog-back-app-0.1.0.war`.

## Запуск тестов

```cmd
gradlew.bat test
```

Покрытие: интеграционные тесты сервисов, DAO и MVC с БД H2 (`ApplicationTests`, `MvcIntegrationTest`), unit-тесты контроллеров (`PostControllerTest`, `CommentControllerTest`).

## Деплой в сервлет-контейнер

### Docker (рекомендуется)

```cmd
gradlew.bat clean build
docker run -d --name blog-backend -p 8080:8080 -v "build\libs\my-blog-back-app-0.1.0.war:/usr/local/tomcat/webapps/ROOT.war" tomcat:10.1
```

Остановить и удалить контейнер:

```cmd
docker stop blog-backend && docker rm blog-backend
```

Проверка: откройте http://localhost:8080/api/posts?search=&pageNumber=1&pageSize=5.

### Классический способ (без Docker)

1. Соберите WAR: `gradlew.bat clean build`.
2. Скопируйте `build/libs/my-blog-back-app-0.1.0.war` в каталог `webapps` Tomcat под именем `ROOT.war`.
3. Запустите Tomcat. Бэкенд будет доступен на http://localhost:8080.

## Запуск фронтенда

1. Распакуйте архив с фронтендом и перейдите в каталог с `docker-compose.yaml`.
2. Запустите: `docker compose up -d`.
3. Фронтенд будет доступен на http://localhost:80.

## Использование API

### Получение списка постов

```
GET /api/posts?search=Lalala&pageNumber=1&pageSize=5
```

Параметры: `search` — строка поиска, `pageNumber` — номер страницы, `pageSize` — количество постов на странице (все поля обязательные).

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

`posts` — список постов, `hasPrev` — true, если текущая страница не первая, `hasNext` — true, если текущая страница не последняя, `lastPage` — номер последней страницы. Поля поста: `id` — идентификатор, `title` — название, `text` — текст (в формате Markdown; если больше 128 символов, обрезается до 128 с добавлением «…»), `tags` — список тегов, `likesCount` — число лайков, `commentsCount` — число комментариев (все поля обязательные).

### Получение поста

```
POST /api/posts/1
```

Ответ — данные одного поста; текст не обрезается.

```json
{
  "id": 1,
  "title": "Название поста 1",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"],
  "likesCount": 5,
  "commentsCount": 1
}
```

### Добавление поста

```
POST /api/posts
Content-Type: application/json
```

Тело запроса:

```json
{ "title": "Название поста 3", "text": "Текст поста", "tags": ["tag_1", "tag_2"] }
```

`title`, `text`, `tags` — обязательные. Ответ — созданный пост с автоматически сгенерированным `id`, `likesCount: 0`, `commentsCount: 0`.

### Редактирование поста

```
PUT /api/posts/3
Content-Type: application/json
```

Тело запроса:

```json
{ "id": 3, "title": "Новое название", "text": "Новый текст", "tags": [] }
```

`id`, `title`, `text`, `tags` — обязательные. Ответ — обновлённый пост.

### Удаление поста

```
DELETE /api/posts/1
```

Удаляет пост вместе со всеми комментариями, возвращает `200 OK`.

### Инкремент числа лайков поста

```
POST /api/posts/1/likes
```

Добавляет +1 к числу лайков и возвращает обновлённое число лайков (число в теле ответа).

### Обновление картинки поста

```
PUT /api/posts/1/image
Content-Type: multipart/form-data
```

Фронтенд присылает файл в поле `image`, возвращается `200 OK`.

### Получение картинки поста

```
GET /api/posts/1/image
```

Возвращает массив байт картинки в теле ответа.

### Комментарии

| Метод | Путь | Описание |
|---|---|---|
| `GET` | `/api/posts/1/comments` | список комментариев поста |
| `GET` | `/api/posts/1/comments/2` | один комментарий |
| `POST` | `/api/posts/1/comments` | создать комментарий (`{ "text": "...", "postId": 1 }`) |
| `PUT` | `/api/posts/1/comments/2` | отредактировать комментарий (`{ "id": 2, "text": "...", "postId": 1 }`) |
| `DELETE` | `/api/posts/1/comments/2` | удалить комментарий, `200 OK` |

Поля комментария: `id`, `text`, `postId` (все поля обязательные).

### Правила поиска

- Строка поиска разбивается на слова по пробелам, пустые слова игнорируются.
- Слова, начинающиеся с `#`, считаются тегами и фильтруют посты по «И».
- Остальные слова склеиваются в подстроку и ищутся в названии поста.
- Фильтр по тегам и подстроке применяется одновременно («И»).

## База данных

Используется **H2 in-memory** (режим PostgreSQL). При старте приложения автоматически выполняется `schema.sql`:

- `posts` — `id`, `title`, `text`, `tags`, `likes_count`, `image`.
- `comments` — `id`, `text`, `post_id` (внешний ключ с `ON DELETE CASCADE`).

Данные хранятся в памяти и стираются при перезапуске приложения.