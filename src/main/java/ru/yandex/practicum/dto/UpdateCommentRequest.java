package ru.yandex.practicum.dto;

import lombok.Data;

/**
 * Запрос на редактирование комментария к посту.
 * Используется при PUT /api/posts/{id}/comments/{commentId}.
 * Фронтенд отправляет JSON с id комментария, текстом и id поста.
 */
@Data
public class UpdateCommentRequest {
    /** Идентификатор комментария для редактирования */
    private Long id;
    /** Текст комментария */
    private String text;
    /** Идентификатор поста, к которому относится комментарий */
    private Long postId;
}
