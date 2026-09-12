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

    /** Получить все комментарии к посту */
    List<CommentResponse> getCommentsByPostId(long postId);

    /** Получить комментарий по идентификаторам поста и комментария */
    CommentResponse getComment(long postId, long commentId);

    /** Создать новый комментарий к посту */
    CommentResponse createComment(long postId, CreateCommentRequest request);

    /** Обновить существующий комментарий */
    CommentResponse updateComment(long postId, long commentId, UpdateCommentRequest request);

    /** Удалить комментарий по идентификаторам поста и комментария */
    void deleteComment(long postId, long commentId);
}
