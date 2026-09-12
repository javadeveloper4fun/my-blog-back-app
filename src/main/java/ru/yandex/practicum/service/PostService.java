package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.dto.UpdatePostRequest;

/**
 * Интерфейс сервиса постов.
 * Определяет бизнес-логику для управления постами блога.
 */
public interface PostService {

    /** Получить список постов с поиском и пагинацией */
    PostListResponse getPosts(String search, int pageNumber, int pageSize);

    /** Получить пост по идентификатору */
    PostResponse getPost(long id);

    /** Создать новый пост */
    PostResponse createPost(CreatePostRequest request);

    /** Обновить существующий пост */
    PostResponse updatePost(long id, UpdatePostRequest request);

    /** Удалить пост по идентификатору */
    void deletePost(long id);

    /** Увеличить количество лайков на 1 и вернуть новое значение */
    long addLike(long id);

    /** Обновить картинку поста */
    void updateImage(long id, byte[] image);

    /** Получить картинку поста */
    byte[] getImage(long id);
}
