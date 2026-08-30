package ru.yandex.practicum.dto;

import lombok.Data;

/**
 * Ответ с данными одного комментария.
 * Используется при получении комментария (GET /api/posts/{id}/comments/{commentId}),
 * создании (POST /api/posts/{id}/comments) и редактировании (PUT /api/posts/{id}/comments/{commentId}).
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
