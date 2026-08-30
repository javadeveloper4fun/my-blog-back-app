# My Blog — бэкенд

Бэкенд приложения-блога на Java 21 и Spring Framework.
Собирается в WAR и разворачивается в сервлет-контейнере Tomcat 10.1+.

## Запуск
- Фронтенд: http://localhost:80
- Бэкенд: http://localhost:8080/api/posts

## Запуск через Docker (Windows / WSL2 / Docker Desktop)

### Сборка WAR
```cmd
gradlew.bat clean build
```

### Запуск бэкенда в Tomcat (Docker)
```cmd
docker run -d --name blog-backend -p 8080:8080 -v "build\libs\my-blog-back-app-0.1.0.war:/usr/local/tomcat/webapps/ROOT.war" tomcat:10.1
```

### Остановка и удаление контейнера
```cmd
docker stop blog-backend && docker rm blog-backend
```

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
```cmd
gradlew.bat build
```

## Тесты
```cmd
gradlew.bat test
```
