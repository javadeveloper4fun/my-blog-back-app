package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация Spring MVC.
 * Включает поддержку REST-контроллеров (@EnableWebMvc),
 * сканирование компонентов в пакете ru.yandex.practicum.
 *
 * Спринт 3: Тема 3 «Инфраструктурные бины Spring» (аннотации-конфигурации)
 * Тема 4 «Создание бинов через Java-аннотации» (@ComponentScan)
 * и Тема 8 «Практика по разработке Spring Framework» (@EnableWebMvc).
 * Реализация п. 13 (Java-конфигурация для интеграции с сервлет-контейнером).
 * Улучшение: добавлена настройка CORS — требуется для работы фронта на :80 с бэкендом на :8080.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.yandex.practicum")
public class WebConfig implements WebMvcConfigurer {

    /**
     * MultipartResolver для обработки запросов multipart/form-data
     * (Постановка: PUT /api/posts/{id}/image — фронтенд отправляет файл картинки).
     * Без него DispatcherServlet не разбирает multipart-запросы → 500.
     */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /**
     * Улучшение: разрешает CORS-запросы с любого origins для всех HTTP-методов.
     * Нужно при локальной разработке (фронт :80, бэкенд :8080 — разные порты = разные origins).
     * В продуктовой среде с общим reverse proxy CORS может не потребоваться.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
