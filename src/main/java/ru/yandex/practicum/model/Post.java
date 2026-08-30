package ru.yandex.practicum.model;

import lombok.Data;

/**
 * Сущность поста блога.
 * Сохраняется в таблице posts базы данных H2.
 */
@Data
public class Post {
    /** Идентификатор поста (генерируется автоматически) */
    private Long id;
    /** Название поста */
    private String title;
    /** Текст поста в формате Markdown */
    private String text;
    /** Теги поста через запятую (например: "tag_1,tag_2") */
    private String tags;
    /** Количество лайков */
    private Long likesCount;
    /** Картинка поста в виде массива байтов */
    private byte[] image;
}
