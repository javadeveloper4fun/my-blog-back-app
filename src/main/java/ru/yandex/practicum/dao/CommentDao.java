package ru.yandex.practicum.dao;

import java.util.List;
import ru.yandex.practicum.model.Comment;

/**
 * Интерфейс доступа к данным комментариев.
 * Определяет CRUD-операции для комментариев к постам.
 */
public interface CommentDao {

    /**
     * Получить все комментарии к посту.
     *
     * @param postId идентификатор поста
     * @return список комментариев поста
     */
    List<Comment> findByPostId(long postId);

    /**
     * Получить комментарий по идентификаторам поста и комментария.
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     * @return комментарий
     */
    Comment findById(long postId, long commentId);

    /**
     * Создать новый комментарий.
     *
     * @param comment комментарий без id (id присваивается БД)
     * @return созданный комментарий с назначенным id
     */
    Comment save(Comment comment);

    /**
     * Обновить существующий комментарий в рамках указанного поста.
     * Обновление выполняется по паре postId + commentId: запрос к одному посту
     * не может случайно изменить комментарий другого поста.
     *
     * @param comment комментарий с заполненными id, postId и новым текстом
     * @return обновлённый комментарий (перечитанный из БД)
     * @throws ru.yandex.practicum.exception.NotFoundException если комментарий
     *         не найден в рамках указанного поста
     */
    Comment update(Comment comment);

    /**
     * Удалить комментарий по идентификаторам поста и комментария.
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     */
    void deleteById(long postId, long commentId);
}
