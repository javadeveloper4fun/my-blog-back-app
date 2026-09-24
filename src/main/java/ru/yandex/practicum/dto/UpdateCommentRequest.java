package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос на редактирование комментария к посту.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: PUT /api/posts/{id}/comments/{commentId} — в теле JSON id комментария,
 * текст и id поста.
 * Поля id, text, postId — обязательные («все поля обязательные»), проверяются через @Valid.
 */
@Data
public class UpdateCommentRequest {
    /** Идентификатор комментария для редактирования */
    @NotNull
    private Long id;
    /** Текст комментария */
    @NotBlank
    private String text;
    /** Идентификатор поста, к которому относится комментарий */
    @NotNull
    private Long postId;
}