package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * Запрос на редактирование поста.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: PUT /api/posts/{id} — в теле JSON id, название, текст Markdown и теги.
 * Поля id, title, text, tags — обязательные («все поля обязательные»), проверяются через @Valid.
 */
@Data
public class UpdatePostRequest {
    /** Идентификатор поста для редактирования */
    @NotNull
    private Long id;
    /** Название поста */
    @NotBlank
    private String title;
    /** Текст поста в формате Markdown */
    @NotBlank
    private String text;
    /** Список тегов поста */
    @NotNull
    private List<String> tags;
}