package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Ответ с данными поста.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: получение поста (POST /api/posts/{id}),
 * создание (POST /api/posts) и редактирование (PUT /api/posts/{id}).
 */
@Data
public class PostResponse {
    /** Идентификатор поста */
    private Long id;
    /** Название поста */
    private String title;
    /** Текст поста в формате Markdown */
    private String text;
    /** Список тегов поста */
    private List<String> tags;
    /** Количество лайков */
    private Long likesCount;
    /** Количество комментариев */
    private Long commentsCount;
}