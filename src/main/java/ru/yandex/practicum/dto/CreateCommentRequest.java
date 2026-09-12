package ru.yandex.practicum.dto;

import lombok.Data;

/**
 * Запрос на создание нового комментария к посту.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: POST /api/posts/{id}/comments — в теле JSON текст и id поста.
 */
@Data
public class CreateCommentRequest {
    /** Текст комментария */
    private String text;
    /** Идентификатор поста, к которому добавляется комментарий */
    private Long postId;
}
