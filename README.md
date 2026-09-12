# My Blog — бэкенд

Бэкенд приложения-блога, написанный на **Java 21** и **Spring Framework 6.1+**.
Собирается в WAR-архив и разворачивается в сервлет-контейнере **Tomcat 10.1+**.
Фронтенд (React) взаимодействует с бэкендом по REST на портах `80` и `8080` соответственно.

## Возможности

- Получение списка постов с **поиском** (по названию и тегам) и **пагинацией**.
- Создание, получение, редактирование и удаление постов.
- Добавление лайков постам.
- Загрузка и получение картинки поста.
- CRUD-операции с комментариями к постам.
- При удалении поста его комментарии удаляются автоматически (каскадное удаление).
- Обработка ошибок: `404 Not Found`, `500 Internal Server Error` в JSON-формате.

## Технологии

| Компонент | Версия |
|---|---|
| Java | 21 |
| Spring Framework (Context, Web MVC, JDBC) | 6.1+ |
| Spring Data JDBC | 3.2 |
| База данных | H2 (in-memory) |
| Сервлет-контейнер | Tomcat 10.1+ / Jetty |
| Система сборки | Gradle (wrapper) |
| Тестирование | JUnit 5, Spring Test, Mockito, JsonPath |
| Прочее | Lombok, Jackson, Jakarta Servlet API 6.0 |

## Требования для запуска

- JDK 21
- Tomcat 10.1+ (или Docker Desktop на Windows/WSL2)
- (опционально) фронтенд из архива проекта на Nginx

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

## Сборка бэкенда

```cmd
gradlew.bat clean build
```

Результат: `build/libs/my-blog-back-app-0.1.0.war`.

## Запуск тестов

```cmd
gradlew.bat test
```

Покрытие:
- **Интеграционные тесты сервисов и DAO** (`ApplicationTests`) — работа с реальной БД H2: создание/поиск/удаление постов, лайки, CRUD комментариев.
- **Интеграционные тесты MVC** (`MvcIntegrationTest`) — полный контекст Spring с H2: REST-эндпоинты, поиск, пагинация, каскадное удаление.
- **Unit-тесты контроллеров** (`PostControllerTest`, `CommentControllerTest`) — MockMvc + Mockito.

## Деплой в сервлет-контейнер

### Docker (рекомендуется)

Собрать WAR и запустить Tomcat с бэкендом:

```cmd
gradlew.bat clean build
docker run -d --name blog-backend -p 8080:8080 -v "build\libs\my-blog-back-app-0.1.0.war:/usr/local/tomcat/webapps/ROOT.war" tomcat:10.1
```

Остановить и удалить контейнер:

```cmd
docker stop blog-backend && docker rm blog-backend
```

Проверка: откройте http://localhost:8080/api/posts.

### Классический способ (без Docker)

1. Соберите WAR: `gradlew.bat clean build`.
2. Скопируйте `build/libs/my-blog-back-app-0.1.0.war` в каталог `webapps` Tomcat под именем `ROOT.war`.
3. Запустите Tomcat. Бэкенд будет доступен на http://localhost:8080.

## Запуск фронтенда

1. Распакуйте архив с фронтендом и перейдите в каталог с `docker-compose.yaml`.
2. Запустите: `docker compose up -d`.
3. Фронтенд будет доступен на http://localhost:80.

## Использование API

Бэкенд запущен на http://localhost:8080.

### Список постов

```
GET /api/posts?search=Lalala&pageNumber=1&pageSize=5
```

Параметры: `search` — строка поиска, `pageNumber` — номер страницы, `pageSize` — количество постов на странице.

Правила поиска:
- Строка поиска разбивается на слова по пробелам, пустые слова игнорируются.
- Слова, начинающиеся с `#`, считаются тегами и фильтруют посты по «И».
- Остальные слова склеиваются в подстроку и ищутся в названии поста.
- Фильтр по тегам и подстроке применяется одновременно («И»).

Ответ:

```json
{
  "posts": [
    {
      "id": 1,
      "title": "Название поста",
      "text": "Текст поста...",
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

Текст поста в списке обрезается до 128 символов с добавлением «…».

### Получение поста

```
POST /api/posts/1
```

Ответ — данные одного поста (текст не обрезается).

### Создание поста

```
POST /api/posts
Content-Type: application/json
```

Тело запроса:

```json
{ "title": "Название поста", "text": "Текст поста", "tags": ["tag_1", "tag_2"] }
```

Ответ содержит созданный пост с `id`, `likesCount: 0`, `commentsCount: 0`.

### Редактирование поста

```
PUT /api/posts/3
Content-Type: application/json
```

Тело запроса:

```json
{ "id": 3, "title": "Новое название", "text": "Новый текст", "tags": [] }
```

### Удаление поста

```
DELETE /api/posts/1
```

Удаляет пост вместе со всеми комментариями, возвращает `200 OK`.

### Лайк

```
POST /api/posts/1/likes
```

Увеличивает счётчик лайков на 1, возвращает новое значение в теле ответа.

### Картинка поста

Загрузка картинки:

```
PUT /api/posts/1/image
Content-Type: multipart/form-data
```

Поле формы — `image` (пример: `Content-Disposition: form-data; name="image"; filename="image.jpg"`).

Получение картинки:

```
GET /api/posts/1/image
```

### Комментарии

| Метод | Путь | Описание |
|---|---|---|
| `GET` | `/api/posts/1/comments` | список комментариев поста |
| `GET` | `/api/posts/1/comments/2` | один комментарий |
| `POST` | `/api/posts/1/comments` | создать комментарий (`{ "text": "...", "postId": 1 }`) |
| `PUT` | `/api/posts/1/comments/2` | отредактировать комментарий (`{ "id": 2, "text": "...", "postId": 1 }`) |
| `DELETE` | `/api/posts/1/comments/2` | удалить комментарий, `200 OK` |

Формат ответа комментария:

```json
{ "id": 2, "text": "Текст комментария", "postId": 1 }
```

## База данных

Используется **H2 in-memory** (режим PostgreSQL). При старте приложения автоматически выполняется `schema.sql`:

- `posts` — `id`, `title`, `text`, `tags`, `likes_count`, `image`.
- `comments` — `id`, `text`, `post_id` (внешний ключ с `ON DELETE CASCADE`).

Данные хранятся в памяти и стираются при перезапуске приложения.