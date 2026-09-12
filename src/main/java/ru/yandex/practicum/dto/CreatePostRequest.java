package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Запрос на создание нового поста.
 * Используется при POST /api/posts.
 * Фронтенд отправляет JSON с названием, текстом и тегами.
 */
@Data
public class CreatePostRequest {
    /** Название поста */
    private String title;
    /** Текст поста в формате Markdown */
    private String text;
    /** Список тегов поста */
    private List<String> tags;
}
