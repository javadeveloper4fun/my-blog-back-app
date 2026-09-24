package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Ответ со списком постов для главной страницы (лента постов).
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (JSON через Jackson).
 * Постановка: GET /api/posts?search=&pageNumber=&pageSize= — в ответе поле posts
 * и метаинформация пагинации (hasPrev, hasNext, lastPage).
 */
@Data
public class PostListResponse {
    /** Список постов текущей страницы */
    private List<PostResponse> posts;
    /** Есть ли предыдущая страница (true, если текущая страница не первая) */
    private boolean hasPrev;
    /** Есть ли следующая страница (true, если текущая страница не последняя) */
    private boolean hasNext;
    /** Номер последней страницы */
    private int lastPage;
}