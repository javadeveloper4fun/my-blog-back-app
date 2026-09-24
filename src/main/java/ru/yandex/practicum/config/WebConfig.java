package ru.yandex.practicum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация CORS для REST-эндпоинтов.
 *
 * Спринт 4: Spring Boot автоконфигурирует MVC и multipart —
 * остаётся только разрешить кросс-доменные запросы
 * с фронтенда на :80 к бэкенду на :8080.
 * Тема 6 «Настройка приложения»: конфигурация через Java-код (вместо web.xml).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}