package ru.yandex.practicum.dao;

import java.util.List;
import ru.yandex.practicum.model.Post;

/**
 * Интерфейс доступа к данным постов.
 * Определяет CRUD-операции для постов блога.
 */
public interface PostDao {

    /**
     * Получить страницу постов с поиском и пагинацией.
     * Только данные из БД: без обрезки текста и без формирования DTO ответа API.
     *
     * @param search     строка поиска (слова с # фильтруют по тегам, остальные — по title)
     * @param pageNumber номер страницы (начиная с 1)
     * @param pageSize   число постов на странице
     * @return посты текущей страницы
     */
    List<Post> findAll(String search, int pageNumber, int pageSize);

    /**
     * Получить общее количество постов, удовлетворяющих поиску.
     * Используется сервисом для расчёта метаинформации пагинации.
     *
     * @param search строка поиска (та же, что передаётся в {@link #findAll})
     * @return общее количество постов по поиску
     */
    long countPosts(String search);

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
