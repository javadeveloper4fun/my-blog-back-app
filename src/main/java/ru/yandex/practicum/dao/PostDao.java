package ru.yandex.practicum.dao;

import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.model.Post;

import java.util.List;

/**
 * Интерфейс доступа к данным постов.
 * Определяет CRUD-операции для постов блога.
 */
public interface PostDao {

    /** Получить список постов с поиском и пагинацией */
    PostListResponse findAll(String search, int pageNumber, int pageSize);

    /** Получить пост по идентификатору */
    Post findById(long id);

    /** Создать новый пост */
    Post save(Post post);

    /** Обновить существующий пост */
    Post update(Post post);

    /** Удалить пост по идентификатору */
    void deleteById(long id);

    /** Увеличить количество лайков на 1 и вернуть новое значение */
    long incrementLikes(long id);

    /** Обновить картинку поста */
    void updateImage(long id, byte[] image);

    /** Получить картинку поста */
    byte[] getImage(long id);
}
