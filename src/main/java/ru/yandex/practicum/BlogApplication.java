package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа Spring Boot-приложения блога.
 * {@code @SpringBootApplication} включает автоконфигурацию,
 * компонентное сканирование и дополнительную конфигурацию.
 *
 * Спринт 4: Тема 2 «Spring Boot как развитие Spring Framework»
 * и Тема 5 «Запуск Spring Boot-приложения» (встроенный сервлет-контейнер).
 * Реализация постановки спринта 4: приложение переписано с Spring Boot,
 * упаковано в Executable Jar и запускается во встроенном контейнере
 * (вместо war-файла и внешнего сервлет-контейнера из спринта 3).
 */
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}