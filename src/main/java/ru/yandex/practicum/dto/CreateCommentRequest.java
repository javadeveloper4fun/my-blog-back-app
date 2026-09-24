package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос на создание нового комментария к посту.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: POST /api/posts/{id}/comments — в теле JSON текст и id поста.
 * Поля text, postId — обязательные («все поля обязательные»), проверяются через @Valid.
 */
@Data
public class CreateCommentRequest {
    /** Текст комментария */
    @NotBlank
    private String text;
    /** Идентификатор поста, к которому добавляется комментарий */
    @NotNull
    private Long postId;
}