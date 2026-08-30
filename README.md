# My Blog — бэкенд

Бэкенд приложения-блога на Java 21 и Spring Framework.
Собирается в WAR и разворачивается в сервлет-контейнере Tomcat 10.1+.

## Запуск
- Фронтенд: http://localhost:80
- Бэкенд: http://localhost:8080/api/posts

## Технологии
- Java 21
- Gradle (wrapper)
- Spring Framework 6.1+ (Web MVC, JDBC, Context)
- Spring Data JDBC
- H2 Database (in-memory)
- JUnit 5 + Spring Test
- Lombok
- Jakarta Servlet API 6.0

## Сборка
```bash
./gradlew build
```

## Тесты
```bash
./gradlew test
```
