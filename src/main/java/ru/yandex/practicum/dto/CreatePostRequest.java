package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * Запрос на создание нового поста.
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: POST /api/posts — в теле JSON название, текст Markdown и теги.
 * Поля title, text, tags — обязательные («все поля обязательные»), проверяются через @Valid.
 */
@Data
public class CreatePostRequest {
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