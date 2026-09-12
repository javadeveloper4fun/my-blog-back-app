package ru.yandex.practicum.dto;

import java.util.List;
import lombok.Data;

/**
 * Ответ со списком постов для главной страницы (ленты).
 * Используется при получении списка постов (GET /api/posts?search=&pageNumber=&pageSize=).
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
