package ru.yandex.practicum.dto;

import lombok.Data;

/**
 * Запрос на редактирование комментария к посту.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: PUT /api/posts/{id}/comments/{commentId} — в теле JSON id комментария,
 * текст и id поста.
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
