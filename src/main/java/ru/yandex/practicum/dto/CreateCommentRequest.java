package ru.yandex.practicum.dto;

import lombok.Data;

/**
 * Запрос на создание нового комментария к посту.
 * Используется при POST /api/posts/{id}/comments.
 * Фронтенд отправляет JSON с текстом и id поста.
 */
@Data
public class CreateCommentRequest {
    /** Текст комментария */
    private String text;
    /** Идентификатор поста, к которому добавляется комментарий */
    private Long postId;
}
