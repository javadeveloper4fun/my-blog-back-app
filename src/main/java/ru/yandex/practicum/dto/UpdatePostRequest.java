package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Запрос на редактирование поста.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: PUT /api/posts/{id} — в теле JSON id, название, текст Markdown и теги.
 */
@Data
public class UpdatePostRequest {
    /** Идентификатор поста для редактирования */
    private Long id;
    /** Название поста */
    private String title;
    /** Текст поста в формате Markdown */
    private String text;
    /** Список тегов поста */
    private List<String> tags;
}
