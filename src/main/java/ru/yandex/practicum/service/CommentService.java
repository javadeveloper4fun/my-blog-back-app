package ru.yandex.practicum.service;

import java.util.List;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.dto.UpdateCommentRequest;

/**
 * Интерфейс сервиса комментариев.
 * Определяет бизнес-логику для управления комментариями к постам.
 */
public interface CommentService {
    /**
     * Получить все комментарии к посту.
     *
     * @param postId идентификатор поста
     * @return список комментариев поста
     */
    List<CommentResponse> getCommentsByPostId(long postId);

    /**
     * Получить комментарий по идентификаторам поста и комментария.
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     * @return комментарий
     */
    CommentResponse getComment(long postId, long commentId);

    /**
     * Создать новый комментарий к посту.
     *
     * @param postId  идентификатор поста
     * @param request запрос на создание комментария (text)
     * @return созданный комментарий
     */
    CommentResponse createComment(long postId, CreateCommentRequest request);

    /**
     * Обновить существующий комментарий.
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     * @param request   запрос на обновление комментария (text)
     * @return обновлённый комментарий
     */
    CommentResponse updateComment(long postId, long commentId, UpdateCommentRequest request);

    /**
     * Удалить комментарий по идентификаторам поста и комментария.
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     */
    void deleteComment(long postId, long commentId);
}