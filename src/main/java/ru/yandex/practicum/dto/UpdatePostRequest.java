package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Запрос на редактирование поста.
 * Используется при PUT /api/posts/{id}.
 * Фронтенд отправляет JSON с id, названием, текстом и тегами.
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
