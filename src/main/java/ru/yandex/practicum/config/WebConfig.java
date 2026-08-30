package ru.yandex.practicum.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация Spring MVC.
 * Включает поддержку REST-контроллеров (@EnableWebMvc),
 * сканирование компонентов в пакете ru.yandex.practicum
 * и настройку CORS для запросов от фронтенда.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.yandex.practicum")
public class WebConfig implements WebMvcConfigurer {

    /**
     * Разрешает CORS-запросы с любого origins для всех HTTP-методов.
     * Фронтенд работает на :80, бэкенд на :8080 — без этого браузер заблокирует запросы.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
