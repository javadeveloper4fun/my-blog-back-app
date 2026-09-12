package ru.yandex.practicum.model;

import lombok.Data;

/**
 * Сущность комментария к посту.
 * Сохраняется в таблице comments базы данных H2.
 * При удалении поста все его комментарии удаляются (ON DELETE CASCADE).
 *
 * Реализация п. 9 (проектирование слоёв: Model) и п. 10 (проектирование классов по SOLID/YAGNI).
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
