package ru.yandex.practicum.dto;

import lombok.Data;
import java.util.List;

/**
 * Ответ с данными одного поста.
 * Используется при получении поста (POST /api/posts/{id}),
 * создании поста (POST /api/posts) и редактировании (PUT /api/posts/{id}).
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
