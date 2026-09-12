package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Запрос на создание нового поста.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: POST /api/posts — в теле JSON название, текст Markdown и теги.
 */
@Data
public class CreatePostRequest {
    /** Название поста */
    private String title;
    /** Текст поста в формате Markdown */
    private String text;
    /** Список тегов поста */
    private List<String> tags;
}
