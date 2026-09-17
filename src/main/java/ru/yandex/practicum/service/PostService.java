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

    /**
     * Получить список постов с поиском и пагинацией.
     *
     * @param search     строка поиска (слова с # фильтруют по тегам, остальные по title)
     * @param pageNumber номер страницы (начиная с 1)
     * @param pageSize   число постов на странице
     * @return ответ со списком постов и метаинформацией пагинации
     */
    PostListResponse getPosts(String search, int pageNumber, int pageSize);

    /**
     * Получить пост по идентификатору.
     *
     * @param id идентификатор поста
     * @return пост
     */
    PostResponse getPost(long id);

    /**
     * Создать новый пост.
     *
     * @param request запрос на создание поста (title, text, tags)
     * @return созданный пост
     */
    PostResponse createPost(CreatePostRequest request);

    /**
     * Обновить существующий пост.
     *
     * @param id      идентификатор поста
     * @param request запрос на обновление поста (title, text, tags)
     * @return обновлённый пост
     */
    PostResponse updatePost(long id, UpdatePostRequest request);

    /**
     * Удалить пост по идентификатору.
     *
     * @param id идентификатор поста
     */
    void deletePost(long id);

    /**
     * Увеличить количество лайков поста на 1.
     *
     * @param id идентификатор поста
     * @return обновлённое количество лайков
     */
    long addLike(long id);

    /**
     * Обновить картинку поста.
     *
     * @param id    идентификатор поста
     * @param image байты изображения
     */
    void updateImage(long id, byte[] image);

    /**
     * Получить картинку поста.
     *
     * @param id идентификатор поста
     * @return байты изображения
     */
    byte[] getImage(long id);
}
