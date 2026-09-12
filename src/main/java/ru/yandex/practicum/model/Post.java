package ru.yandex.practicum.model;

import lombok.Data;

/**
 * Модель записи, соответствующая строке таблицы posts БД H2.
 * Спринт 3: Тема 2 «Spring как IoC-контейнер» / Тема 4 «Создание бинов через Java-аннотации»
 * — модель используется слоями DAO и Service в цепочке Controller -> Service -> DAO -> БД.
 *
 * Реализация п. 9 (проектирование слоёв: Model) и п. 10 (проектирование моделей по SOLID/YAGNI).
 * П. 11 (структура БД: таблица posts).
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
    /** Количество комментариев (заполняется через подзапрос в DAO) */
    private Long commentsCount;
}
