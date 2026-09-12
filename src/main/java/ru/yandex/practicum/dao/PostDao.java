package ru.yandex.practicum.dao;

import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.model.Post;

/**
 * Интерфейс доступа к данным постов.
 * Определяет CRUD-операции для постов блога.
 */
public interface PostDao {

    /**
     * Получить список постов с поиском и пагинацией.
     *
     * @param search     строка поиска (слова с # фильтруют по тегам, остальные — по title)
     * @param pageNumber номер страницы (начиная с 1)
     * @param pageSize   число постов на странице
     * @return ответ со списком постов и метаинформацией пагинации
     */
    PostListResponse findAll(String search, int pageNumber, int pageSize);

    /**
     * Получить пост по идентификатору.
     *
     * @param id идентификатор поста
     * @return пост
     */
    Post findById(long id);

    /**
     * Создать новый пост.
     *
     * @param post пост без id (id присваивается БД)
     * @return созданный пост с назначенным id
     */
    Post save(Post post);

    /**
     * Обновить существующий пост.
     *
     * @param post пост с заполненным id и новыми значениями полей
     * @return обновлённый пост
     */
    Post update(Post post);

    /**
     * Удалить пост по идентификатору.
     *
     * @param id идентификатор поста
     */
    void deleteById(long id);

    /**
     * Увеличить количество лайков поста на 1.
     *
     * @param id идентификатор поста
     * @return обновлённое количество лайков
     */
    long incrementLikes(long id);

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
