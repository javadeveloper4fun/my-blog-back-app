package ru.yandex.practicum.dao;

import java.util.List;
import ru.yandex.practicum.model.Comment;

/**
 * Интерфейс доступа к данным комментариев.
 * Определяет CRUD-операции для комментариев к постам.
 */
public interface CommentDao {

    /** Получить все комментарии к посту */
    List<Comment> findByPostId(long postId);

    /** Получить комментарий по идентификаторам поста и комментария */
    Comment findById(long postId, long commentId);

    /** Создать новый комментарий */
    Comment save(Comment comment);

    /** Обновить существующий комментарий */
    Comment update(Comment comment);

    /** Удалить комментарий по идентификаторам поста и комментария */
    void deleteById(long postId, long commentId);
}
