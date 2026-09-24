package ru.yandex.practicum.dto;

import lombok.Data;

/**
 * Ответ с данными комментария.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON-ответы через Jackson).
 * Используется для ответов по постановке: получение комментария
 * (GET /api/posts/{id}/comments/{commentId}), создание (POST /api/posts/{id}/comments)
 * и редактирование (PUT /api/posts/{id}/comments/{commentId}).
 */
@Data
public class CommentResponse {
    /** Идентификатор комментария */
    private Long id;
    /** Текст комментария */
    private String text;
    /** Идентификатор поста, к которому относится комментарий */
    private Long postId;
}