package ru.yandex.practicum.model;

import lombok.Data;

/**
 * Модель комментария к посту.
 * Соответствует таблице comments базы данных H2.
 * При удалении поста комментарии удаляются автоматически (ON DELETE CASCADE).
 * Спринт 3: Тема 2 «Spring как IoC-контейнер» / Тема 4 «Создание бинов через Java-аннотации»
 * — модель используется слоями DAO и Service в цепочке Controller -> Service -> DAO -> БД.
 *
 * Реализация п. 9 (проектирование слоёв: Model) и п. 10 (проектирование моделей по SOLID/YAGNI).
 * П. 11 (структура БД: таблица comments).
 */
@Data
public class Comment {
    /** Идентификатор комментария (генерируется автоматически) */
    private Long id;
    /** Текст комментария */
    private String text;
    /** Идентификатор поста, к которому относится комментарий */
    private Long postId;
}